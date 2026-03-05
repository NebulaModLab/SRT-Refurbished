/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;
import sj.editor.MainWindow;
import sj.editor.data.RulesetsManager;
import sj.editor.data.rules.DirectoryFile;
import sj.editor.data.rules.RuleFile;

/**
 * @author SafariJohn
 */
public class TreeTransferHandler extends TransferHandler {
    private static final Logger logger = Logger.getLogger(TreeTransferHandler.class.getName());

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    boolean success;

    public TreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                        + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }

        success = false;
    }

    private RuleFile getRuleFromPath(TreePath path) {
        return getRuleFromNode((DefaultMutableTreeNode) path.getLastPathComponent());
    }

    private RuleFile getRuleFromNode(DefaultMutableTreeNode node) {
        return (RuleFile) node.getUserObject();
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) return false;
        if (!support.isDataFlavorSupported(nodesFlavor)) return false;

        support.setShowDropLocation(true);

        // Get the path of the drop location
        TreePath dropPath = ((JTree.DropLocation) support.getDropLocation()).getPath();

        if (dropPath == null || dropPath.getPathCount() == 1) return false;

        RuleFile rulesetRoot = (RuleFile) ((DefaultMutableTreeNode) dropPath.getPathComponent(1)).getUserObject();

        // Can't drop on a rule or spacer
        RuleFile dropTarget = (RuleFile) ((DefaultMutableTreeNode) dropPath.getLastPathComponent()).getUserObject();
        if (!dropTarget.isDirectory()) return false;

        // Do not allow a directory to be dropped inside itself.
        try {
            // Get the import nodes
            DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) support.getTransferable().getTransferData(nodesFlavor);

            boolean emptyMove = false;

            for (DefaultMutableTreeNode node : nodes) {
                // For move actions, make sure at least one non-vanilla rule is being dragged.
//                if (getRuleFromNode(node).getRulesetId() != RulesetsManager.getVanillaId()
//                            && support.getUserDropAction() == MOVE) emptyMove = false;

                // Check if any of the transferred nodes are in the drop location's path
                for (Object pathObject : dropPath.getPath()) {
                    DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode) pathObject;
                    if (pathNode == pathNode.getRoot()) continue;

                    if (getRuleFromNode(node).equals(getRuleFromNode(pathNode))) return false;
                }
            }

            if (support.getUserDropAction() == MOVE && emptyMove) return false;

        } catch (UnsupportedFlavorException | IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
        }

//        System.out.println(dropPath);

        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        RulesTree tree = (RulesTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();

        if (paths != null) {
            for (TreePath path : paths) {
                if (path.getPathCount() == 2) continue;

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                if (nodes.contains(node)) continue;

                nodes.add(node);
            }

            if (nodes.isEmpty()) return null;

            DefaultMutableTreeNode[] transfer = nodes.toArray(new DefaultMutableTreeNode[nodes.size()]);
            return new NodesTransferable(transfer);
        }

        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) return false;

        // Extract transfer data.
        List<DefaultMutableTreeNode> nodes;
        Transferable t;
        try {
            t = support.getTransferable();
            nodes = Arrays.asList((DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor));
        } catch (UnsupportedFlavorException | java.io.IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
            return false;
        }

        // Get drop location info.
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        int childIndex = dl.getChildIndex();
        DirectoryFile destDir = (DirectoryFile) ((DefaultMutableTreeNode) dl.getPath().getLastPathComponent()).getUserObject();

        // Get target rulesetId
        int targetRuleset = destDir.getRulesetId();

        List<DirectoryFile> directories = new ArrayList<>();
        List<RuleFile> rules = new ArrayList<>();
        // Copy
        if (support.getUserDropAction() == COPY) {
            for (DefaultMutableTreeNode node : nodes) {
                RuleFile rule = getRuleFromNode(node);

                // Store for processing
                if (rule.isDirectory()) directories.add(new DirectoryFile((DirectoryFile) rule, false));
                else rules.add(new RuleFile(rule, false));
            }
        }
        // Move
        else {
            for (DefaultMutableTreeNode node : nodes) {
                RuleFile rule = getRuleFromNode(node);

                // Store for processing
                if (rule.isDirectory()) directories.add((DirectoryFile) rule);
                else rules.add(rule);
            }
         }

        // Backup rulesets
        List<RuleFile> backup = new ArrayList<>();
        if (support.getUserDropAction() != COPY) {
            backup.addAll(rules);
            backup.addAll(directories);
        }
        RulesetsManager.backupTree(backup);

        int dirIndex = -1;
        int prevDirs = 0;
        int ruleIndex = -1;
        int prevRules = 0;
        boolean removeLeading = false;
        boolean removeTrailing = false;

        if (childIndex < 0) {
            //<editor-fold defaultstate="collapsed" desc="DropMode.ON">
            dirIndex = destDir.getBranches().size();

            for (DirectoryFile dir : directories) {
                if (dir.getParentId() == destDir.getId()) prevDirs++;
            }

            ruleIndex = destDir.getLeaves().size();

            for (RuleFile rule : rules) {
                if (rule.getParentId() == destDir.getId()) prevRules++;
            }

            if (ruleIndex == 0) removeLeading = true;
            removeTrailing = true;
            //</editor-fold>
        } else {
            //<editor-fold defaultstate="collapsed" desc="DropMode.INSERT">
            int destBranches = destDir.getBranches().size();
            int destLeaves = destDir.getLeaves().size();

            if (childIndex < destBranches) {
                dirIndex = childIndex;

                for (DirectoryFile dir : directories) {
                    if (dir.getParentId() == destDir.getId()
                                && destDir.getBranches().indexOf(dir) < childIndex) prevDirs++;
                }

                ruleIndex = destLeaves;

                for (RuleFile rule : rules) {
                    if (rule.getParentId() == destDir.getId()) prevRules++;
                }

                if (ruleIndex == 0) removeLeading = true;
                removeTrailing = true;
            }
            // else
            if (childIndex == destBranches) {
                dirIndex = destBranches;

                for (DirectoryFile dir : directories) {
                    if (dir.getParentId() == destDir.getId()) prevDirs++;
                }

                ruleIndex = 0;

                prevRules = 0;

                removeLeading = true;
                removeTrailing = true;
            }
            // else
            if (childIndex > destBranches) {
                childIndex -= destBranches;

                dirIndex = destBranches;

                for (DirectoryFile dir : directories) {
                    if (dir.getParentId() == destDir.getId()) prevDirs++;
                }

                if (childIndex < destLeaves) {
                    ruleIndex = childIndex;

                    for (RuleFile rule : rules) {
                        if (rule.getParentId() == destDir.getId()
                                    && destDir.getLeaves().indexOf(rule) < childIndex) prevRules++;
                    }

                    if (ruleIndex == 0) removeLeading = true;
                    removeTrailing = false;
                }
                // else
                if (childIndex >= destLeaves) {
                    ruleIndex = destLeaves;

                    for (RuleFile rule : rules) {
                        if (rule.getParentId() == destDir.getId()) prevRules++;
                    }

                    if (ruleIndex == 0) removeLeading = true;
                    removeTrailing = true;
                }
            }
            //</editor-fold>
        }

        // Delete leading/trailing spacers
        List<RuleFile> remove = new ArrayList<>();
        boolean leading = true;
        List<RuleFile> trailing = new ArrayList<>();
        for (RuleFile rule : rules) {
            if (removeLeading && leading && rule.isSpacer()) remove.add(rule);
            else leading = false;

            if (removeTrailing && !leading && rule.isSpacer()) trailing.add(rule);
            else trailing.clear();
        }

        remove.addAll(trailing);
        rules.removeAll(remove);

        for (DirectoryFile dir : directories) {
            if (prevDirs > 0) {
                dirIndex--;
                prevDirs--;
            }

            destDir.addBranch(dir, dirIndex++);

            dir.setRulesetId(targetRuleset);
        }

        for (RuleFile rule : rules) {
            if (prevRules > 0) {
                ruleIndex--;
                prevRules--;
            }

            destDir.addLeaf(rule, ruleIndex++);

            rule.setRulesetId(targetRuleset);
        }

        success = true;

        return true;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (success) {
            RulesetsManager.updateIdOverlaps();
            MainWindow.getInstance().refreshAllData();
        }
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);

            return nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}