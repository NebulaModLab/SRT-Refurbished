/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.*;

import jdk.jfr.internal.tool.Main;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Refurbished)
 */
public class RulesTree extends JTree implements SRTInterface {
    private static final Logger logger = Logger.getLogger(RulesTree.class.getName());
    private final DefaultMutableTreeNode root;
    private final DefaultTreeModel model;

    private List<TreePath> selections;
    private boolean pathLock;
    private RuleFile selected;

    public RulesTree() {
        logger.log(Level.FINE, "Constructing");
        root = new DefaultMutableTreeNode("Root");
        model = new DefaultTreeModel(root);

        selections = new ArrayList<>();
        pathLock = false;
        selected = new RuleFile();

        setToolTipText("<html>Drag and drop rules and folders to rearrange them. Hold control to copy."
                    + "<br>Press the delete key to delete the selected rules/folders.</html>");

        setModel(model);

        setRootVisible(false);
        setShowsRootHandles(true);
        setExpandsSelectedPaths(true);
//        setScrollsOnExpand(true);
        setCellRenderer(new RulesTreeCellRenderer());

        addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent evt) { selectionChanged(evt); }
        });

        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new TreeTransferHandler());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftArrowPressed();
                }

                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    enterPressed();
                }

                if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteRules();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() >= 2) {
                    JTextField idField = MainWindow.getInstance().getEditorPanes().getIdField();
                try {
                    String newId = idField.getDocument().getText(0, idField.getDocument().getLength());
                    MainWindow.getInstance().getEditorPanes().idUpdated(newId);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }

                    RulesManager.setActiveRule(selected);
                    MainWindow.getInstance().refreshAllData();
//                }
            }
        });


        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                MainWindow.getInstance().setTreeInFocus(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                MainWindow.getInstance().setTreeInFocus(false);
            }
        });

        addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                if (!pathLock) refreshInterface();
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                if (!pathLock) refreshInterface();
            }
        });
    }

    private void selectionChanged(TreeSelectionEvent evt) {

        if (pathLock) return;

        if (getSelectionPaths() != null) {
            selections = Arrays.asList(getSelectionPaths());
        } else return;

        TreePath[] paths = evt.getPaths();
        RuleFile newRule = null;

        for (TreePath path : paths) {
            if (newRule == null) {
                if (evt.isAddedPath(path)) {
                    newRule = getRuleFromPath(path);
                }
            }
        }

        // Save new rule to be (potentially) set as the active rule
        if (newRule != null) {
            selected = newRule;
        } else {
            // Else, set active rule to empty rule
            selected = new RuleFile();
            RulesManager.setActiveRule(new RuleFile());

            MainWindow.getInstance().refreshAllData();
        }
    }

    private void leftArrowPressed() {
        RuleFile rule = RulesManager.getActiveRule();
        if (rule.getRulesetId() < 0) return;
        TreePath path = getSelectionPath();

        if (path.isDescendant(getPathForRule(rule))) {
            RulesManager.setActiveRule(selected);
        }

        MainWindow.getInstance().refreshAllData();
        collapsePath(path);
    }

    private void enterPressed() {
        RulesManager.setActiveRule(selected);
        MainWindow.getInstance().refreshAllData();
    }

    private void deleteRules() {
        if (getSelectionPaths() == null) return;

        List<TreePath> paths = Arrays.asList(getSelectionPaths());

        // Check for root directories and close those rulesets instead.
//        paths = closeRulesets(paths);

        List<RuleFile> backupRules = new ArrayList<>();

        for (TreePath path : paths) {
            RuleFile file = getRuleFromPath(path);
            Ruleset ruleset = RulesetsManager.getRuleset(file.getRulesetId());

            if (file.equals(ruleset.getRootDirectory())) {}
            else {
                backupRules.add(file);
                if (file.isDirectory()) unrollRuleFilesRecursive((DirectoryFile) file, backupRules);
            }
        }

        if (backupRules.isEmpty()) return;
        // Backup rulesets
        RulesetsManager.backupTree(backupRules);

        // Delete selected
        for (TreePath path : paths) {
            RuleFile file = getRuleFromPath(path);
            if (file.getParentId() < 0) continue; // don't delete roots because that is stupid

            Ruleset ruleset = RulesetsManager.getRuleset(file.getRulesetId());
            DirectoryFile parent = ruleset.getDirectory(file.getParentId());

            if (file.isDirectory()) parent.removeBranch((DirectoryFile) file);
            else parent.removeLeaf(file);

            if (RulesManager.getActiveRule().equals(file)) {
                RulesManager.setActiveRule(new RuleFile());
            }
        }

        setSelectionPath(null);

        RulesetsManager.updateIdOverlaps();

        MainWindow.getInstance().refreshAllData();
    }

    private void unrollRuleFilesRecursive(DirectoryFile dir, List<RuleFile> files) {
        for (DirectoryFile branch : dir.getBranches()) {
            files.add(branch);
            unrollRuleFilesRecursive(branch, files);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            files.add(leaf);
        }
    }

    /**
     * Closes rulesets instead of deleting their rules.
     * Unused at present
     * @param nodes
     * @return nodes minus closed rulesets
     */
    private List<TreePath> closeRulesets(List<TreePath> nodes) {
        List<TreePath> setRoots = new ArrayList<>();
        for (TreePath path : nodes) {
            RuleFile file = getRuleFromPath(path);
            Ruleset ruleset = RulesetsManager.getRuleset(file.getRulesetId());

            if (file.equals(ruleset.getRootDirectory())) {
                setRoots.add(path);
                RulesetsManager.getRulesets().remove(ruleset);
            }
        }

        // Remove any node that is a descendant of a root node
        // And the root nodes themselves
        List<TreePath> toRemove = new ArrayList<>();
        for (TreePath setRoot : setRoots) {
            for (TreePath path : nodes) {
                if (path.isDescendant(setRoot)) toRemove.add(path);
            }
        }

        nodes.removeAll(setRoots);
        nodes.removeAll(toRemove);

        return nodes;
    }

    private RuleFile getRuleFromPath(TreePath path) {
        return getRuleFromNode((DefaultMutableTreeNode) path.getLastPathComponent());
    }

    private RuleFile getRuleFromNode(DefaultMutableTreeNode node) {
        if (node == root) return null;
        else return (RuleFile) node.getUserObject();
    }

    private TreePath getPathForRule(RuleFile rule) {
        // Create "path" of RuleFile ids going up rule's parent list
        List<Integer> parentPath = new ArrayList<>();
        parentPath.add(rule.getId());

        DirectoryFile parent = RulesetsManager.getDirectory(rule.getParentId());
        while (parent != null) {
            parentPath.add(0, parent.getId());
            parent = RulesetsManager.getDirectory(parent.getParentId());
        }

        // Start at rule's set's root node, find each parent recursively until reach rule's node
        TreeNode node = getNodeFromIdPath(parentPath, (TreeNode) model.getRoot());

        // Return path from model's root to rule's node
        return new TreePath(model.getPathToRoot(node));
    }

    private TreeNode getNodeFromIdPath(List<Integer> parentPath, TreeNode branch) {
        Enumeration children = ((DefaultMutableTreeNode) branch).children();

        while (children.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
            RuleFile file = (RuleFile) node.getUserObject();

            if (file.getId() == parentPath.get(0)) {
                parentPath.remove(0);
                if (parentPath.isEmpty()) return node;
                else return getNodeFromIdPath(parentPath, node); // Recursion
            }
        }

        return null;
    }

    private void recursiveNodeInsertion(DirectoryFile directory, DefaultMutableTreeNode dirNode,
                List<TreePath> expPaths, List<TreePath> newPaths, List<TreePath> visiblePaths) {

        int index = 0;

        for (DirectoryFile branch : directory.getBranches()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(branch);
            model.insertNodeInto(node, dirNode, index++);

            recursiveNodeInsertion(branch, node, expPaths, newPaths, visiblePaths);

            for (TreePath selection : selections) {
                if (getRuleFromPath(selection).equals(branch)) {
                    newPaths.add(new TreePath(model.getPathToRoot(node)));
                }
            }

            for (TreePath path : expPaths) {
                if (getRuleFromPath(path).equals(branch)) {
                    visiblePaths.add(new TreePath(model.getPathToRoot(node)));
                }
            }
        }

        for (RuleFile leaf : directory.getLeaves()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(leaf);
            model.insertNodeInto(node, dirNode, index++);

            for (TreePath selection : selections) {
                if (getRuleFromPath(selection).equals(leaf)) {
                    newPaths.add(new TreePath(model.getPathToRoot(node)));
                }
            }

            for (TreePath path : expPaths) {
                if (getRuleFromPath(path).equals(leaf)) {
                    visiblePaths.add(new TreePath(model.getPathToRoot(node)));
                }
            }
        }
    }

    private long changeTime = 0;

    @Override
    public void refreshInterface() {
        boolean canRefresh = System.currentTimeMillis() > changeTime;
        changeTime = System.currentTimeMillis() + 1000;

//        if (!canRefresh) return;

        Enumeration ep = getExpandedDescendants(new TreePath(model.getPathToRoot(root)));
        List<TreePath> expPaths = new ArrayList<>();
        if (ep != null) {
            while (ep.hasMoreElements()) {
                TreePath path = (TreePath) ep.nextElement();

                if (((DefaultMutableTreeNode) path.getLastPathComponent()) == root) continue;

                expPaths.add(path);
            }
        }

        root.removeAllChildren();

        // Create node trees from rulesets
        List<DirectoryFile> rootFiles = new ArrayList<>();
        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            rootFiles.add(ruleset.getRootDirectory());
        }

        int rootIndex = 0;
        List<TreePath> newPaths = new ArrayList<>();
        List<TreePath> visiblePaths = new ArrayList<>();
        for (DirectoryFile rootFile : rootFiles) {
            DefaultMutableTreeNode rulesetRoot = new DefaultMutableTreeNode(rootFile);
            model.insertNodeInto(rulesetRoot, root, rootIndex++);

            for (TreePath selection : selections) {
                if (getRuleFromPath(selection).equals(rootFile)) {
                    newPaths.add(new TreePath(model.getPathToRoot(rulesetRoot)));
                }
            }

            for (TreePath path : expPaths) {
                if (getRuleFromPath(path).equals(rootFile)) {
                    visiblePaths.add(new TreePath(model.getPathToRoot(rulesetRoot)));
                }
            }

            int index = 0;
            for (DirectoryFile branch : rootFile.getBranches()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(branch);
                model.insertNodeInto(node, rulesetRoot, index++);

                recursiveNodeInsertion(branch, node, expPaths, newPaths, visiblePaths);

                for (TreePath selection : selections) {
                    if (getRuleFromPath(selection).equals(branch)) {
                        newPaths.add(new TreePath(model.getPathToRoot(node)));
                    }
                }

                for (TreePath path : expPaths) {
                    if (getRuleFromPath(path).equals(branch)) {
                        visiblePaths.add(new TreePath(model.getPathToRoot(node)));
                    }
                }
            }

            for (RuleFile leaf : rootFile.getLeaves()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(leaf);
                model.insertNodeInto(node, rulesetRoot, index++);

                for (TreePath selection : selections) {
                    if (getRuleFromPath(selection).equals(leaf)) {
                        newPaths.add(new TreePath(model.getPathToRoot(node)));
                    }
                }

                for (TreePath path : expPaths) {
                    if (getRuleFromPath(path).equals(leaf)) {
                        visiblePaths.add(new TreePath(model.getPathToRoot(node)));
                    }
                }
            }
        }


        selections = new ArrayList<>();
        selections.addAll(newPaths);

        expPaths.clear();
        expPaths.addAll(visiblePaths);

        model.nodeStructureChanged(root);

        pathLock = true; // Disable refreshing from tree expansion/collapse
        for (TreePath path : expPaths) {
            setExpandedState(path, true);
        }

        // This catches when LinkedRulesPanel changes the active rule.
        if (MainWindow.getInstance().catchLinkChange()) {
            // Find active rule's path
            TreePath selectedPath = getPathForRule(RulesManager.getActiveRule());

            // Set selection to active rule's path
            setExpandedState(selectedPath, true);
            setSelectionPath(selectedPath);

            // Center scrollPane on selection
            scrollPathToVisible(selectedPath);
        } else {
            TreePath[] paths = new TreePath[selections.size()];
            setSelectionPaths(selections.toArray(paths));
        }
        pathLock = false; // Reenable

    }

    @Override
    public void getPreferences(Preferences prefs) {}

    @Override
    public void setPreferences(Preferences prefs) {}

    class RulesTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value.equals(root) || !leaf) return this;

            RuleFile file = getRuleFromNode((DefaultMutableTreeNode) value);
            if (!file.isRule()) return this;

            boolean overlap = RulesetsManager.isIdOverlapped(file);

            if (overlap) {
                if (RulesetsManager.isIdenticalMatch(file)) setForeground(Color.ORANGE);
                else setForeground(Color.RED);
            }
//            else setForeground(Color.BLACK);

            return this;
        }
    }
}