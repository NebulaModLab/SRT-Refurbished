/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import sj.editor.data.commands.TFVContainer;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;
import sj.editor.ui.SRTInterface;
import sj.editor.ui.SpellcheckManager;
import sj.misc.Misc;

/**
 * Author: SafariJohn
 */
public class TextPanel extends JSplitPane implements SRTInterface {
    private static final Logger logger = Logger.getLogger(TextPanel.class.getName());

    private final JPanel textPanel = new JPanel();
    private final JLabel textLabel = new JLabel("Text:");
    private final JScrollPane textScrollPane = new JScrollPane();
    private final JTextArea textArea = new JTextArea();

    private final JSplitPane split2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private final JPanel variablesPanel = new JPanel();
    private final JLabel variablesLabel = new JLabel("Known Variables:");
    private final JLabel filterLabel = new JLabel("Filter:");
    private final JTextField filterField = new JTextField();
    private final JScrollPane variablesScrollPane = new JScrollPane();
    private final JList variablesList = new JList();
    private final DefaultListModel variablesListModel = new DefaultListModel();

    private final JPanel optionsPanel = new JPanel();
    private final JLabel optionsLabel = new JLabel("Dialog Options:");
    private final JScrollPane optionsScrollPane = new JScrollPane();
    private final JTextArea optionsArea = new JTextArea();

    private boolean writeLock;
    private boolean scanForTFVs;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;
    private String filter;

    public TextPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        logger.log(Level.FINE, "Constructing");
        setBorder(BorderFactory.createEmptyBorder());

        setLeftComponent(textPanel);
        setRightComponent(split2);

        setDividerLocation(150);

        writeLock = false;
        scanForTFVs = false;
        suppressChange = false;
        changeTime = 0;
        updateLater = false;
        filter = "";

        logger.log(Level.FINE, "Constructing DnD transfer handler.");
        TransferHandler dnd = new TFVDnDHandler();

        textArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newText = "";
                try {
                    newText = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                textUpdated(newText);
            }
            //</editor-fold>
        });

        textArea.setToolTipText("<html>Text to add to the dialog when the rule is executed."
                     + "<br><br>Variations can be separated by an \"OR\" on a separate line;"
                    + "<br>if this is done, one will be picked randomly.</html>");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        SpellcheckManager.register(textArea, "TEXT_AREA", true, false, true, true);

        textScrollPane.setBorder(BorderFactory.createEtchedBorder());
        textScrollPane.setViewportView(textArea);

        GroupLayout layout = new GroupLayout(textPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(textLabel)
                    .addComponent(textScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(textLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        //</editor-fold>
        textPanel.setLayout(layout);

        split2.setBorder(BorderFactory.createEmptyBorder());
        split2.setLeftComponent(variablesPanel);
        split2.setRightComponent(optionsPanel);


        filterField.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
        });
        filterField.getDocument().addDocumentListener(new DocumentListener() {
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
                try {
                    filter = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                refreshInterface();
            }
            //</editor-fold>
        });

        filterField.setToolTipText("Filtering is not case sensitive and does not detect ruleset names.");

        variablesScrollPane.setBorder(BorderFactory.createEtchedBorder());
        variablesScrollPane.setViewportView(variablesList);

        variablesList.setDragEnabled(true);
        variablesList.setTransferHandler(dnd);
        variablesList.setToolTipText("<html>Variables are tokens that are replaced during execution."
                    + "<br>Variables can come from memory, but vanilla tokens take priority.");
        variablesList.setModel(variablesListModel);

        layout = new GroupLayout(variablesPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout">
        layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup()
                                                        .addComponent(variablesLabel)
                                                        .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(filterLabel)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(filterField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addComponent(variablesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addContainerGap())
        );
        layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addComponent(variablesLabel)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(filterLabel)
                                                        .addComponent(filterField))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(variablesScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addContainerGap())
        );
        //</editor-fold>
        variablesPanel.setLayout(layout);


        optionsArea.setTransferHandler(new TransferHandler() {
            //<editor-fold defaultstate="collapsed">
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection(((JTextArea) c).getSelectedText());
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

                JTextArea tc = (JTextArea) support.getComponent();
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
        optionsArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newOptions = "";
                try {
                    newOptions = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                optionsUpdated(newOptions);
            }
            //</editor-fold>
        });

        optionsArea.setToolTipText("The dialog options to show when the rule is executed.");
        optionsArea.setLineWrap(true);
        optionsArea.setWrapStyleWord(true);

        SpellcheckManager.register(optionsArea, "OPTIONS_AREA", true, false, true, true);

        optionsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        optionsScrollPane.setViewportView(optionsArea);

        layout = new GroupLayout(optionsPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(optionsLabel)
                    .addComponent(optionsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(optionsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        //</editor-fold>
        optionsPanel.setLayout(layout);
    }

    private void textUpdated(String newText) {
        if (!RulesManager.getActiveRule().getRule().getText().equals(newText)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setText(newText);

            boolean canRefresh = System.currentTimeMillis() > changeTime;
            changeTime = System.currentTimeMillis() + 100;

            writeLock = true;
            if (canRefresh) MainWindow.getInstance().refreshAllData();
            else {
                updateLater = true;
                java.awt.EventQueue.invokeLater(getRefreshLaterRunnable());
            }
        }
    }

    private void optionsUpdated(String newOptions) {
        if (!RulesManager.getActiveRule().getRule().getOptions().equals(newOptions)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setOptions(newOptions);

            boolean canRefresh = System.currentTimeMillis() > changeTime;
            changeTime = System.currentTimeMillis() + 100;

            writeLock = true;
            scanForTFVs = true;
            if (canRefresh) MainWindow.getInstance().refreshAllData();
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

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();
        boolean canScan = true;

        if (writeLock || MainWindow.getInstance().isSummaryLock()) {
            writeLock = false;
            canScan = false;
        } else {
            suppressChange = true;
            textArea.setText(activeFile.getRule().getText());
            optionsArea.setText(activeFile.getRule().getOptions());
            suppressChange = false;
        }

        if (activeFile.getRulesetId() < 0 || !activeFile.isRule()) {
            textArea.setEditable(false);
            optionsArea.setEditable(false);
        } else {
            textArea.setEditable(true);
            optionsArea.setEditable(true);
        }

        //<editor-fold defaultstate="collapsed" desc="Read New Variables">
        if (canScan && scanForTFVs) {
            List<String> tVariables = new ArrayList<>();

            for (String s : activeFile.getRule().getText().split("([ \\n])")) {
                String variable;
                if (s.startsWith("$")) variable = s.replaceFirst("[^$\\w].*", "");
                else continue;

                if (RulesetsManager.contains(variable, RulesetsManager.DataType.TVARIABLE, activeFile.getRulesetId())
                            || tVariables.contains(variable) || variable.matches("[ +]")) continue;

                tVariables.add(variable);
            }

            List<String> cVariables = new ArrayList<>();
            List<String> sVariables = new ArrayList<>();

            for (String variable : activeFile.getRule().getOptions().split("\n")) {
                if (variable.isEmpty()) continue;

                for (int i = -10; i < 1000; i++) {
                    if (variable.startsWith(i + ":")) {
                        variable = variable.replaceFirst(i + ":", "");
                        break;
                    }
                }

                variable = variable.replaceFirst(":.+", "");
    //                variable = variable.concat(" (Dialog Option)");

                if (cVariables.contains(variable) || RulesetsManager.contains(variable, RulesetsManager.DataType.CVARIABLE, activeFile.getRulesetId())) { }
                else cVariables.add(variable);

                if (sVariables.contains(variable) || RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, activeFile.getRulesetId())) { }
                else sVariables.add(variable);

            }

            if (activeFile.getRulesetId() >= 0) {
                Ruleset ruleset = RulesetsManager.getRuleset(activeFile.getRulesetId());
                ruleset.getTempCVariables().addAll(cVariables);
                ruleset.getTempSVariables().addAll(sVariables);
                ruleset.getTempTVariables().addAll(tVariables);
            }

            scanForTFVs = false;
        }
        //</editor-fold>

        variablesListModel.clear();
        for (String variable : RulesetsManager.getVanilla().getTVariables()) {
            if (!variable.toLowerCase().contains(filter.toLowerCase())) continue;

            variablesListModel.addElement(new TFVContainer(variable, RulesetsManager.getVanilla().getId()));
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            List<String> variables = new ArrayList<>();
            variables.addAll(ruleset.getTVariables());
            variables.addAll(ruleset.getTempTVariables());

            for (String variable : variables) {
                if (!variable.toLowerCase().contains(filter.toLowerCase())) continue;

                variablesListModel.addElement(new TFVContainer(variable, ruleset.getId()));
            }
        }

        //<editor-fold defaultstate="collapsed" desc="Find/Replace">
        SearchResult result = SearchManager.getSearchResult();

        if (result != null && (result.getColumn() == SearchResult.Column.TEXT
                    || result.getColumn() == SearchResult.Column.OPTIONS)) {
            JTextArea area;
            if (result.getColumn() == SearchResult.Column.TEXT) {
                area = textArea;
            } else {
                area = optionsArea;
            }

            if (SearchManager.isSearchingFlagRaised()) {
                area.getCaret().setSelectionVisible(true);
                area.setSelectionStart(result.getIndexes().get(0));
                area.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }

            if (SearchManager.catchReplaceChange()) {
                // Convert String to char[], then to Character[], then to List<Character>
                List<Character> text = Misc.toCharacterList(area.getText());

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
                if (!RulesManager.getActiveRule().getRule().getText().equals(newText)
                            && result.getColumn() == SearchResult.Column.TEXT) {
                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    RulesManager.getActiveRule().getRule().setText(newText);

                    suppressChange = true;
                    textArea.setText(activeFile.getRule().getText());
                    suppressChange = false;

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();
                } else if (!RulesManager.getActiveRule().getRule().getOptions().equals(newText)
                            && result.getColumn() == SearchResult.Column.OPTIONS) {
                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    RulesManager.getActiveRule().getRule().setOptions(newText);

                    suppressChange = true;
                    textArea.setText(activeFile.getRule().getOptions());
                    suppressChange = false;

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();
                }
            }
        } else if (!textArea.isFocusOwner() && !optionsArea.isFocusOwner()) {
            textArea.getCaret().setSelectionVisible(false);
            optionsArea.getCaret().setSelectionVisible(false);
        } else if (!textArea.isFocusOwner()) {
            textArea.getCaret().setSelectionVisible(false);
        } else if (!optionsArea.isFocusOwner()) {
            optionsArea.getCaret().setSelectionVisible(false);
        }
        //</editor-fold>
    }

    @Override
    public void getPreferences(Preferences prefs) {
        int mainLoc = prefs.getInt("textMainDivider", 150);
        setDividerLocation(mainLoc);

        int coLoc = prefs.getInt("textCODivider", 325);
        split2.setDividerLocation(coLoc);
    }

    @Override
    public void setPreferences(Preferences prefs) {
        prefs.putInt("textMainDivider", getDividerLocation());
        prefs.putInt("textCODivider", split2.getDividerLocation());
    }

}
