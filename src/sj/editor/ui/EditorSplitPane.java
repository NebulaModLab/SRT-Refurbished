/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;
import sj.misc.Misc;

/**
 * @author SafariJohn (original SRT)
 */
public class EditorSplitPane extends JSplitPane implements SRTInterface {
    private static final Logger logger = Logger.getLogger(EditorSplitPane.class.getName());

    private final JPanel treePanel = new JPanel();
    private final JLabel treeLabel = new JLabel("Rules:");
    private final JButton addDirectoryButton = new JButton();
    private final JButton addRuleButton = new JButton();
    private final JScrollPane rulesScrollPane = new JScrollPane();
    private final RulesTree rulesTree;

    private final JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private final JPanel dataPanel = new JPanel();
    private final JLabel idLabel = new JLabel("ID:");
    private final JTextField idField = new JTextField();
    private final RuleDataTabbedPane ruleDataPanes;
    private final LinkedRulesPanel linkedPanel;

    private boolean writeLock;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;


    public boolean isSuppressChange() {
        return suppressChange;
    }

    public JTextField getIdField() {
        return idField;
    }

    public EditorSplitPane() {
        super(JSplitPane.HORIZONTAL_SPLIT);
        logger.log(Level.FINE, "Constructing");
        setBorder(BorderFactory.createEmptyBorder());

        rulesTree = new RulesTree();
        ruleDataPanes = new RuleDataTabbedPane();
        linkedPanel = new LinkedRulesPanel();

        setLeftComponent(treePanel);
        setRightComponent(split2);
        setDividerLocation(150);

        changeTime = 0;
        updateLater = false;

        // add node buttons
        logger.log(Level.FINER, "Adding directory/rule buttons.");
        addDirectoryButton.setText("D");
        addDirectoryButton.setToolTipText("Create a new directory in the active directory or the directory of the active rule.");
        addDirectoryButton.setMargin(new java.awt.Insets(2, 5, 2, 4));
//        addDirectoryButton.setTransferHandler(new AddRuleTransferHandler(true));
        addDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { addDirectory(); }
        });

        addRuleButton.setText("R");
        addRuleButton.setToolTipText("Create a new rule in the active directory or the directory of the active rule.");
        addRuleButton.setMargin(new java.awt.Insets(2, 5, 2, 4));
//        addRuleButton.setTransferHandler(new AddRuleTransferHandler(false));
        addRuleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { addRule(); }
        });

        logger.log(Level.FINER, "Creating rules scroll pane.");
        rulesScrollPane.setBorder(BorderFactory.createEtchedBorder());
        rulesScrollPane.setViewportView(rulesTree);

        logger.log(Level.FINE, "Laying out rules tree panel.");
        GroupLayout layout = new GroupLayout(treePanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(treeLabel)
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(addDirectoryButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(addRuleButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
//                            .addComponent(reloadRulesButton))
                        .addComponent(rulesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
//                    .addContainerGap()
                    .addGap(4)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(treeLabel)
                        .addComponent(addDirectoryButton)
                        .addComponent(addRuleButton))
//                        .addComponent(reloadRulesButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rulesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        treePanel.setLayout(layout);


        split2.setBorder(BorderFactory.createEmptyBorder());
        split2.setLeftComponent(dataPanel);
        split2.setRightComponent(linkedPanel);
        split2.setDividerLocation(650);

        writeLock = false;
        suppressChange = false;

        logger.log(Level.FINER, "Adding transfer handler to id field.");
        idField.setTransferHandler(new TransferHandler() {
            //<editor-fold defaultstate="collapsed">
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(((JTextField) c).getSelectedText());
            }

            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY_OR_MOVE;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) return false;

                String data;
                try {
                    data = (String) support.getTransferable().getTransferData(
                                DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException | java.io.IOException ex) {
                    logger.log(Level.FINE, ex.toString(), ex);
                    return false;
                }

                JTextField tc = (JTextField) support.getComponent();
                tc.replaceSelection(data);
                return true;
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                if (support.isDrop()) return false;

                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }
            //</editor-fold>
        });
        logger.log(Level.FINER, "Adding document listener to id field.");
        // CONTINUE | DISABLE / REPLACE THIS
        idField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (suppressChange) return;

                String newId = "";
                try {
                    newId = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                idUpdated(newId);
            }
            //</editor-fold>
        });

        // Throw SOME kind of listener to the UI
//.addMouseMotionListener(new MouseMotionListener() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                mouseMoved(e);
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                if (INSTANCE.editorPanes.isSuppressChange()) return;
//                JTextField idField = INSTANCE.editorPanes.getIdField();
//                String newId = "";
//                try {
//                    if (!idField.getDocument().getText(0, idField.getDocument().getLength()).equals(newId)) {
//                        newId = idField.getDocument().getText(0, idField.getDocument().getLength());
//                        INSTANCE.editorPanes.idUpdated(newId);
//                    }
//                } catch (BadLocationException ex) {
//                    logger.log(Level.INFO, ex.toString(), ex);
//                }
//
//            }
//        });


        idField.setToolTipText("<html>The identifier for this rule."
                    + "<br>Rules with the same ID overwrite each other by load order.</html>");

        logger.log(Level.FINE, "Laying out rule data panel.");
        layout = new GroupLayout(dataPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(idLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(idField))
                        .addComponent(ruleDataPanes))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(idLabel)
                        .addComponent(idField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(ruleDataPanes, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        dataPanel.setLayout(layout);

//        treePanel.setBackground(new Color(23, 23, 23));
//        treeLabel.setForeground(Color.white);
////        addDirectoryButton.setBackground(new Color(18,18,18));
////        addRuleButton.setBackground(new Color(18,18,18));
//        rulesScrollPane.setBackground(new Color(40,40,40));
//        rulesTree.setBackground(new Color(40,40,40));
//        split2.setBackground(new Color(40,40,40));
//        dataPanel.setBackground(new Color(40,40,40));
//        idLabel.setForeground(Color.white);
//        idField.setBackground(new Color(40,40,40));
//        ruleDataPanes.setBackground(new Color(40,40,40));
//        linkedPanel.setBackground(new Color(40,40,40));
    }

    public void idUpdated(String newId) {
        boolean canRefresh = System.currentTimeMillis() > changeTime;
        changeTime = System.currentTimeMillis() + 100;

        newId = newId.trim();

        RuleFile activeRule = RulesManager.getActiveRule();

        if (!activeRule.getName().equals(newId)) {
            if (RulesManager.doBackup(activeRule.getId())) {
                RulesManager.backupActiveFile();
            }

            activeRule.getRule().setId(newId);
            if (!activeRule.isDirectory()) RulesetsManager.updateIdOverlaps();

            writeLock = true;
            if (canRefresh) {
                MainWindow.getInstance().refreshAllData();
            }
            else {
                updateLater = true;
                java.awt.EventQueue.invokeLater(getRefreshLaterRunnable());
            }
        }
    }

    private Runnable getRefreshLaterRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (updateLater && System.currentTimeMillis() > changeTime) {
                    updateLater = false;
                    MainWindow.getInstance().refreshAllData();
                } else if (updateLater) {
                    java.awt.EventQueue.invokeLater(this);
                }
            }
        };
    }

    private void addDirectory() {
        RuleFile dir = RulesManager.getActiveRule();
        if (!dir.isDirectory()) dir = RulesetsManager.getDirectory(dir.getParentId());
        if (dir == null) return;

        DirectoryFile newFile = new DirectoryFile();
        newFile.getRule().setId("NEW DIRECTORY");

        RulesetsManager.backupTree(newFile);

        ((DirectoryFile) dir).addBranch(newFile);
        MainWindow.getInstance().refreshAllData();
    }

    private void addRule() {
        RuleFile dir = RulesManager.getActiveRule();
        if (!dir.isDirectory()) dir = RulesetsManager.getDirectory(dir.getParentId());
        if (dir == null) return;

        RuleFile newFile = new RuleFile();
        newFile.getRule().setId("NEW RULE");

        RulesetsManager.backupTree(newFile);

        ((DirectoryFile) dir).addLeaf(newFile);
        RulesetsManager.updateIdOverlaps();
        MainWindow.getInstance().refreshAllData();
    }

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();

        if (RulesetsManager.getRulesets().isEmpty() || activeFile.getRulesetId() < 0) {
            addDirectoryButton.setEnabled(false);
            addRuleButton.setEnabled(false);
        } else {
            addDirectoryButton.setEnabled(true);
            addRuleButton.setEnabled(true);
        }

        rulesTree.refreshInterface();

        if (writeLock) writeLock = false;
        else  {
            suppressChange = true;
            idField.setText(activeFile.getRule().getId());
            suppressChange = false;
        }

        if (RulesetsManager.isIdOverlapped(activeFile)) idField.setForeground(Color.RED);
        else idField.setForeground(idLabel.getForeground());

        if (activeFile.getRulesetId() < 0) idField.setEditable(false);
        else idField.setEditable(true);

        ruleDataPanes.refreshInterface();
        linkedPanel.refreshInterface();

        // Need to make dividers move properly on resize

        //<editor-fold defaultstate="collapsed" desc="Find/Replace">
        SearchResult result = SearchManager.getSearchResult();

        if (result != null && result.getColumn() == SearchResult.Column.ID) {
            if (SearchManager.isSearchingFlagRaised()) {
                idField.getCaret().setSelectionVisible(true);
                idField.setSelectionStart(result.getIndexes().get(0));
                idField.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }

            if (SearchManager.catchReplaceChange()) {
                // Convert String to char[], then to Character[], then to List<Character>
                List<Character> text = Misc.toCharacterList(idField.getText());

                char[] replacement = SearchManager.getReplacement().toCharArray();


                // Replace the pattern, character by character
                int index = result.getIndexes().get(0);
                int searchLength = SearchManager.getSearchPattern().length();
                int removalIndex = -1;
                for (int i = index; i < index + searchLength; i++) {
                    // Exception for if replacement is smaller.
                    if (i - index >= replacement.length) {
                        removalIndex = i;
                        break;
                    }

                    // Exception for if replacement is larger.
                    if (i - index == searchLength - 1) {
                        text.set(i, replacement[i - index]);
                        i++;

                        for (int j = i - index; j < replacement.length; j++) {
                            text.add(i, replacement[j]);
                            i++;
                        }
                        break;
                    }

                    // Normal replacement
                    text.set(i, replacement[i - index]);
                }

                if (removalIndex >= 0) {
                    for (int i = removalIndex + (searchLength - replacement.length) - 1;
                                i >= removalIndex; i--) {
                        text.remove(i);
                    }
                }

                // Convert back to String.
                String newText = Misc.toString(text);
                if (!RulesManager.getActiveRule().getRule().getId().equals(newText)) {

                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    activeFile.getRule().setId(newText);
                    if (!activeFile.isDirectory()) RulesetsManager.updateIdOverlaps();

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();

                    suppressChange = true;
                    idField.setText(activeFile.getRule().getId());
                    suppressChange = false;
                }
            }
        } else if (!idField.isFocusOwner()) {
            idField.getCaret().setSelectionVisible(false);
        }
        //</editor-fold>
    }

    @Override
    public void getPreferences(Preferences prefs) {

        // left divider 410 right divider 540
        logger.log(Level.FINEST, "Getting and setting divider position preferences.");
        int leftLoc = prefs.getInt("mainLeftDivider", 150);
        if (MainWindow.getSettings() != null && MainWindow.getSettings().doResetDividers()) {
            leftLoc = 410;
            prefs.putInt("mainLeftDivider", leftLoc);
        }
        setDividerLocation(leftLoc);

        int rightLoc = prefs.getInt("mainRightDivider", 650);
        if (MainWindow.getSettings() != null && MainWindow.getSettings().doResetDividers()) {
            rightLoc = 540;
            prefs.putInt("mainRightDivider", rightLoc);
        }
        split2.setDividerLocation(rightLoc);

        rulesTree.getPreferences(prefs);
        ruleDataPanes.getPreferences(prefs);
        linkedPanel.getPreferences(prefs);
    }

    @Override
    public void setPreferences(Preferences prefs) {
        prefs.putInt("mainLeftDivider", getDividerLocation());
        prefs.putInt("mainRightDivider", split2.getDividerLocation());

        rulesTree.setPreferences(prefs);
        ruleDataPanes.setPreferences(prefs);
        linkedPanel.setPreferences(prefs);
    }

}
