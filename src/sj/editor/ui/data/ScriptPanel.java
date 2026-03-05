/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import sj.editor.data.commands.TFVContainer;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.commands.Command;
import sj.editor.data.rules.*;
import sj.editor.ui.SRTInterface;
import sj.editor.ui.SpellcheckManager;
import sj.editor.ui.dialogs.commands.EditCommandDialog;
import sj.editor.ui.dialogs.FunctionDialog;
import sj.misc.Misc;

/**
 * @author SafariJohn
 */
public class ScriptPanel extends JSplitPane implements SRTInterface {
    private static final Logger logger = Logger.getLogger(ScriptPanel.class.getName());

    private final JPanel scriptPanel = new JPanel();
    private final JLabel scriptLabel = new JLabel("Script:");
    private final JScrollPane scriptScrollPane = new JScrollPane();
    private final JTextArea scriptArea = new JTextArea();

    private final JSplitPane funVarPane = new JSplitPane();

    private final JPanel functionsPanel = new JPanel();
    private final JLabel functionsLabel = new JLabel("Known Commands:");
    private final JButton addFunctionButton = new JButton("Add");
    private final JButton editFunctionButton = new JButton("Edit");
    private final JLabel filter1Label = new JLabel("Filter:");
    private final JTextField filter1Field = new JTextField();
    private final JScrollPane functionsScrollPane = new JScrollPane();
    private final JList functionsList = new JList();
    private final DefaultListModel functionsListModel = new DefaultListModel();

    private final JPanel variablesPanel = new JPanel();
    private final JLabel variablesLabel = new JLabel("Known Variables:");
    private final JLabel filter2Label = new JLabel("Filter:");
    private final JTextField filter2Field = new JTextField();
    private final JScrollPane variablesScrollPane = new JScrollPane();
    private final JList variablesList = new JList();
    private final DefaultListModel variablesListModel = new DefaultListModel();

    private boolean writeLock;
    private boolean scanForTFVs;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;
    private String filter1;
    private String filter2;

    public ScriptPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        logger.log(Level.FINE, "Constructing");
        setBorder(BorderFactory.createEmptyBorder());

        setLeftComponent(scriptPanel);
        setRightComponent(funVarPane);

        setDividerLocation(150);

        writeLock = false;
        scanForTFVs = false;
        suppressChange = false;
        changeTime = 0;
        updateLater = false;
        filter1 = "";
        filter2 = "";

        logger.log(Level.FINE, "Constructing DnD transfer handler.");

        scriptArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newScript = "";
                try {
                    newScript = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                scriptUpdated(newScript);
            }
            //</editor-fold>
        });

        scriptArea.setToolTipText("Expressions to run when the rule is executed.");
        scriptArea.setLineWrap(true);
        scriptArea.setWrapStyleWord(true);
        scriptArea.setTransferHandler(new TextAndCommandTransferHandler());

        SpellcheckManager.register(scriptArea, "SCRIPT_AREA", true, false, true, true);

        scriptScrollPane.setBorder(BorderFactory.createEtchedBorder());
        scriptScrollPane.setViewportView(scriptArea);

        GroupLayout layout = new GroupLayout(scriptPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(scriptLabel)
                        .addComponent(scriptScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scriptLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scriptScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        scriptPanel.setLayout(layout);

        funVarPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        funVarPane.setLeftComponent(functionsPanel);
        funVarPane.setRightComponent(variablesPanel);
        funVarPane.setBorder(BorderFactory.createEmptyBorder());

        addFunctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { addFunction(); }
        });
        addFunctionButton.setEnabled(false);
        addFunctionButton.setToolTipText("Add a command to the list of known commands.");

        editFunctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { editFunction(); }
        });
        editFunctionButton.setEnabled(false);
        editFunctionButton.setToolTipText("Edit the first selected command.");

        filter1Field.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
        });
        filter1Field.getDocument().addDocumentListener(new DocumentListener() {
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
                    filter1 = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                refreshInterface();
            }
            //</editor-fold>
        });

        filter1Field.setToolTipText("Filtering is not case sensitive and does not detect ruleset names.");

        functionsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        functionsScrollPane.setViewportView(functionsList);

        functionsList.setDragEnabled(true);
        functionsList.setTransferHandler(new CommandDnDHandler());
        functionsList.setToolTipText("<html>A command points to an arbitrary piece of Java code that the script can pass parameters to."
                    + "<br>Press the delete key to remove the selected commands from the list.</html>");
        functionsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) { functionsListSelectionChanged(); }
        });
        functionsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteFunction();
            }
        });
        functionsList.setModel(functionsListModel);

        layout = new GroupLayout(functionsPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup()
                                                        .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(functionsLabel)
                                                                    .addGap(10, 10, Short.MAX_VALUE)
                                                                    .addComponent(addFunctionButton)
                                                                    .addComponent(editFunctionButton))
                                                        .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(filter1Label)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(filter1Field, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addComponent(functionsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addContainerGap())
        );
        layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(functionsLabel)
                                                        .addComponent(addFunctionButton)
                                                        .addComponent(editFunctionButton))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(filter1Label)
                                                        .addComponent(filter1Field))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(functionsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addContainerGap())
        );
        //</editor-fold>
        functionsPanel.setLayout(layout);

        filter2Field.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
        });
        filter2Field.getDocument().addDocumentListener(new DocumentListener() {
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
                    filter2 = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                refreshInterface();
            }
            //</editor-fold>
        });

        filter2Field.setToolTipText("Filtering is not case sensitive and does not detect ruleset names.");

        variablesScrollPane.setBorder(BorderFactory.createEtchedBorder());
        variablesScrollPane.setViewportView(variablesList);

        variablesList.setDragEnabled(true);
        variablesList.setTransferHandler(new TFVDnDHandler());
        variablesList.setToolTipText("<html>A variable listed here may not be accessible from this rule."
                    + "<br>Press the delete key to remove the selected variables from the list.</html>");
        variablesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteVariable();
            }
        });
        variablesList.setModel(variablesListModel);

        layout = new GroupLayout(variablesPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                            .addContainerGap()
                                            .addGroup(layout.createParallelGroup()
                                                        .addComponent(variablesLabel)
                                                        .addGroup(layout.createSequentialGroup()
                                                                    .addComponent(filter2Label)
                                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                    .addComponent(filter2Field, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                                        .addComponent(filter2Label)
                                                        .addComponent(filter2Field))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(variablesScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addContainerGap())
        );
        //</editor-fold>
        variablesPanel.setLayout(layout);
    }

    private void scriptUpdated(String newScript) {
        if (!RulesManager.getActiveRule().getRule().getScript().equals(newScript)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setScript(newScript);

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

    private void functionsListSelectionChanged() {
//        TFVContainer function = (TFVContainer) functionsList.getSelectedValue();
//        if (function == null || RulesetsManager.getRuleset(function.getRulesetId()) == null) editFunctionButton.setEnabled(false);
//        else editFunctionButton.setEnabled(true);

        Command com = (Command) functionsList.getSelectedValue();
        if (com == null || RulesetsManager.getRuleset(com.getRulesetId()) == null) editFunctionButton.setEnabled(false);
        else editFunctionButton.setEnabled(true);
    }

    private void addFunction() {
        EditCommandDialog dialog = new EditCommandDialog(null);
        dialog.setVisible(true);
        // Result is applied by dialog
    }

    private void editFunction() {
        // Get first selected command to edit
        Command com = (Command) functionsList.getSelectedValue();
        if (com == null) return;

        EditCommandDialog dialog = new EditCommandDialog(com);
        dialog.setVisible(true);
        // Result is applied by dialog
    }

    private void deleteFunction() {
//        List<TFVContainer> functions = functionsList.getSelectedValuesList();
//
//        for (TFVContainer function : functions) {
//            Ruleset ruleset = RulesetsManager.getRuleset(function.getRulesetId());
//            if (ruleset != null) ruleset.getSFunctions().remove(function.getTFV());
//        }

        List<Command> commands = functionsList.getSelectedValuesList();

        for (Command com : commands) {
            Ruleset ruleset = RulesetsManager.getRuleset(com.getRulesetId());
            if (ruleset != null) {
                ruleset.getCommands().remove(com);
                RulesetsManager.getCommandsChangedRulesets().add(ruleset.getId());
            }
        }

        MainWindow.getInstance().refreshAllData();
    }

    private void deleteVariable() {
        List<TFVContainer> variables = variablesList.getSelectedValuesList();

        for (TFVContainer variable : variables) {
            Ruleset ruleset = RulesetsManager.getRuleset(variable.getRulesetId());
            if (ruleset != null) {
                ruleset.getSVariables().remove(variable.getTFV());
                ruleset.getTempSVariables().remove(variable.getTFV());
            }
        }

        MainWindow.getInstance().refreshAllData();
    }

    @Override
    public void refreshInterface() {
        // Band-aid. Don't know why it is unsetting after the constructor.
        // Hard to troubleshoot since it doesn't use an instance variable of the Component.
        if (!(scriptArea.getTransferHandler() instanceof TextAndCommandTransferHandler)) {
            scriptArea.setTransferHandler(new TextAndCommandTransferHandler());
        }


        RuleFile activeFile = RulesManager.getActiveRule();
        boolean canScan = true;

        if (writeLock || MainWindow.getInstance().isSummaryLock()) {
            writeLock = false;
            canScan = false;
        } else {
            suppressChange = true;
            scriptArea.setText(activeFile.getRule().getScript());
            suppressChange = false;
        }

        if (activeFile.getRulesetId() < 0) {
            scriptArea.setEditable(false);
            addFunctionButton.setEnabled(false);
        } else {
            if (!activeFile.isRule()) scriptArea.setEditable(false);
            else scriptArea.setEditable(true);
            addFunctionButton.setEnabled(true);
        }

        //<editor-fold defaultstate="collapsed" desc="Read New Functions/Variables">
        if (canScan && scanForTFVs) {
            List<String> sFunctions = new ArrayList<>();
            List<String> sVariables = new ArrayList<>();

            for (String s : activeFile.getRule().getScript().split("\n")) {
                if (s.isEmpty() || s.startsWith("#")) continue;

                if (s.startsWith("!")) s = s.replaceFirst("!", "");

                if (s.startsWith("$")) {
                    String variable = s.replaceAll("[ <>=!].+", "");

                    if (RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, activeFile.getRulesetId())
                                || sVariables.contains(variable)) continue;

                    sVariables.add(variable);
                } else {
//                    String function = s.replaceAll(" .+", "");
//
//                    if (RulesetsManager.contains(function, RulesetsManager.DataType.SFUNCTION, activeFile.getRulesetId())
//                                || sFunctions.contains(function)) continue;
//
//                    sFunctions.add(function);
                }
            }

            if (activeFile.getRulesetId() >= 0) {
                Ruleset ruleset = RulesetsManager.getRuleset(activeFile.getRulesetId());
//                ruleset.getSFunctions().addAll(sFunctions);
                ruleset.getTempSVariables().addAll(sVariables);
            }

            scanForTFVs = false;
        }
        //</editor-fold>

        functionsListModel.clear();
        variablesListModel.clear();
//        for (String function : RulesetsManager.getVanilla().getSFunctions()) {
//            if (!function.toLowerCase().contains(filter1.toLowerCase())) continue;
//
//            functionsListModel.addElement(new TFVContainer(function, RulesetsManager.getVanilla().getId()));
//        }

        for (Command com : RulesetsManager.getVanilla().getCommands()) {
            if (!com.getName().toLowerCase().contains(filter1.toLowerCase())) continue;
            if (!com.isScriptCommand()) continue;

            functionsListModel.addElement(com);
        }


        for (String variable : RulesetsManager.getVanilla().getSVariables()) {
            if (!variable.toLowerCase().contains(filter2.toLowerCase())) continue;

            variablesListModel.addElement(new TFVContainer(variable, RulesetsManager.getVanilla().getId()));
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
//            for (String function : ruleset.getSFunctions()) {
//                if (!function.toLowerCase().contains(filter1.toLowerCase())) continue;
//
//                functionsListModel.addElement(new TFVContainer(function, ruleset.getId()));
//            }

            for (Command com : ruleset.getCommands()) {
                if (!com.getName().toLowerCase().contains(filter1.toLowerCase())) continue;
                if (!com.isScriptCommand()) continue;

                functionsListModel.addElement(com);
            }

            List<String> variables = new ArrayList<>();
            variables.addAll(ruleset.getSVariables());
            variables.addAll(ruleset.getTempSVariables());

            for (String variable : variables) {
                if (!variable.toLowerCase().contains(filter2.toLowerCase())) continue;

                variablesListModel.addElement(new TFVContainer(variable, ruleset.getId()));
            }
        }


        //<editor-fold defaultstate="collapsed" desc="Find/Replace">
        SearchResult result = SearchManager.getSearchResult();

        if (result != null && result.getColumn() == SearchResult.Column.SCRIPT) {
            if (SearchManager.isSearchingFlagRaised()) {
                scriptArea.getCaret().setSelectionVisible(true);
                scriptArea.setSelectionStart(result.getIndexes().get(0));
                scriptArea.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }

            if (SearchManager.catchReplaceChange()) {
                // Convert String to char[], then to Character[], then to List<Character>
                List<Character> text = Misc.toCharacterList(scriptArea.getText());

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
                if (!RulesManager.getActiveRule().getRule().getScript().equals(newText)) {

                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    RulesManager.getActiveRule().getRule().setScript(newText);

                    suppressChange = true;
                    scriptArea.setText(activeFile.getRule().getScript());
                    suppressChange = false;

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();
                }
            }
        } else if (!scriptArea.isFocusOwner()) {
            scriptArea.getCaret().setSelectionVisible(false);
        }
        //</editor-fold>

    }

    @Override
    public void getPreferences(Preferences prefs) {
        int mainLoc = prefs.getInt("scriptMainDivider", 150);
        setDividerLocation(mainLoc);

        int cvLoc = prefs.getInt("scriptCVDivider", 325);
        funVarPane.setDividerLocation(cvLoc);
    }

    @Override
    public void setPreferences(Preferences prefs) {
        prefs.putInt("scriptMainDivider", getDividerLocation());
        prefs.putInt("scriptCVDivider", funVarPane.getDividerLocation());
    }

}
