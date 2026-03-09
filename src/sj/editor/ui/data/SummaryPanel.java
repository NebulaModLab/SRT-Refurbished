/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.rules.RuleFile;
import sj.editor.data.rules.RulesManager;
import sj.editor.ui.SRTInterface;
import sj.editor.ui.SpellcheckManager;

/**
 * @author SafariJohn (original SRT)
 */
public class SummaryPanel extends JPanel implements SRTInterface {
    private static final Logger logger = Logger.getLogger(SummaryPanel.class.getName());

    private final JLabel triggerLabel = new JLabel("Trigger:");
    private final JTextField triggerField = new JTextField();

    private final JLabel conditionsLabel = new JLabel("Conditions:");
    private final JScrollPane conditionsScrollPane = new JScrollPane();
    private final JTextArea conditionsArea = new JTextArea();

    private final JLabel scriptLabel = new JLabel("Script:");
    private final JScrollPane scriptScrollPane = new JScrollPane();
    private final JTextArea scriptArea = new JTextArea();

    private final JLabel textLabel = new JLabel("Text:");
    private final JScrollPane textScrollPane = new JScrollPane();
    private final JTextArea textArea = new JTextArea();

    private final JLabel optionsLabel = new JLabel("Dialog Options:");
    private final JScrollPane optionsScrollPane = new JScrollPane();
    private final JTextArea optionsArea = new JTextArea();

    private final JLabel notesLabel = new JLabel("Notes:");
    private final JScrollPane notesScrollPane = new JScrollPane();
    private final JTextArea notesArea = new JTextArea();

    private boolean writeLock;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;

    public SummaryPanel() {
        logger.log(Level.FINE, "Constructing");
        writeLock = false;
        suppressChange = false;
        changeTime = 0;
        updateLater = false;

//        logger.log(Level.FINEST, "Setting up trigger field.");
        triggerField.setEditable(false);
        triggerField.setToolTipText("The trigger defines what type of action may cause this rule to be executed.");
        triggerField.getDocument().addDocumentListener(new DocumentListener() {
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

                String newTrigger = "";
                try {
                    newTrigger = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                triggerUpdated(newTrigger);
            }
            //</editor-fold>
        });

//        logger.log(Level.FINEST, "Setting up conditions field.");
        conditionsArea.setEditable(false);
        conditionsArea.setLineWrap(true);
        conditionsArea.setWrapStyleWord(true);
        conditionsArea.setToolTipText("All conditions must match if a rule is to be executed.");
        conditionsArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newConditions = "";
                try {
                    newConditions = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                conditionsUpdated(newConditions);
            }
            //</editor-fold>
        });
        conditionsScrollPane.setViewportView(conditionsArea);
        conditionsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        SpellcheckManager.register(conditionsArea, "SUM_COND_AREA", true, false, true, true);

//        logger.log(Level.FINEST, "Setting up script field.");
        scriptArea.setEditable(false);
        scriptArea.setLineWrap(true);
        scriptArea.setWrapStyleWord(true);
        scriptArea.setToolTipText("Expressions to run when the rule is executed.");
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
        scriptScrollPane.setViewportView(scriptArea);
        scriptScrollPane.setBorder(BorderFactory.createEtchedBorder());
        SpellcheckManager.register(scriptArea, "SUM_SCRIPT_AREA", true, false, true, true);

//        logger.log(Level.FINEST, "Setting up text field.");
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setToolTipText("Text to add to the dialog when the rule is executed.");
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
        textScrollPane.setViewportView(textArea);
        textScrollPane.setBorder(BorderFactory.createEtchedBorder());
        SpellcheckManager.register(textArea, "SUM_TEXT_AREA", true, false, true, true);

//        logger.log(Level.FINEST, "Setting up options field.");
        optionsArea.setEditable(false);
        optionsArea.setLineWrap(true);
        optionsArea.setWrapStyleWord(true);
        optionsArea.setToolTipText("The dialog options to show when the rule is executed.");
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
        optionsScrollPane.setViewportView(optionsArea);
        optionsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        SpellcheckManager.register(optionsArea, "SUM_OPTIONS_AREA", true, false, true, true);

//        logger.log(Level.FINEST, "Setting up notes field.");
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setToolTipText("Anything put here is ignored by the game.");
        notesArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newNotes = "";
                try {
                    newNotes = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                notesUpdated(newNotes);
            }
            //</editor-fold>
        });
        notesScrollPane.setBorder(BorderFactory.createEtchedBorder());
        notesScrollPane.setViewportView(notesArea);
        SpellcheckManager.register(notesArea, "SUM_NOTES_AREA", true, false, true, true);


//        logger.log(Level.FINE, "Laying out panel.");
        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(triggerLabel)
                    .addComponent(triggerField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(conditionsLabel)
                    .addComponent(conditionsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scriptLabel)
                    .addComponent(scriptScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(textLabel)
                    .addComponent(textScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optionsLabel)
                    .addComponent(optionsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(notesLabel)
                    .addComponent(notesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(triggerLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(triggerField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conditionsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(conditionsScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scriptLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scriptScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notesLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notesScrollPane, 25, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        setLayout(layout);
    }

    //<editor-fold defaultstate="collapsed" desc="Update Methods">
    private void triggerUpdated(String newTrigger) {
        if (!RulesManager.getActiveRule().getRule().getTrigger().equals(newTrigger)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setTrigger(newTrigger);
            refreshAll();
        }
    }

    private void conditionsUpdated(String newConditions) {
        if (!RulesManager.getActiveRule().getRule().getConditions().equals(newConditions)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setConditions(newConditions);
            refreshAll();
        }
    }

    private void scriptUpdated(String newScript) {
        if (!RulesManager.getActiveRule().getRule().getScript().equals(newScript)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setScript(newScript);
            refreshAll();
        }
    }

    private void textUpdated(String newText) {
        if (!RulesManager.getActiveRule().getRule().getText().equals(newText)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setText(newText);
            refreshAll();
        }
    }

    private void optionsUpdated(String newOptions) {
        if (!RulesManager.getActiveRule().getRule().getOptions().equals(newOptions)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setOptions(newOptions);
            refreshAll();
        }
    }

    private void notesUpdated(String newNotes) {
        if (!RulesManager.getActiveRule().getRule().getNotes().equals(newNotes)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setNotes(newNotes);
            refreshAll();
        }
    }

    private void refreshAll() {
        boolean canRefresh = System.currentTimeMillis() > changeTime;
        changeTime = System.currentTimeMillis() + 100;

        writeLock = true;
        if (canRefresh) {
            MainWindow.getInstance().setSummaryLock(true);
            MainWindow.getInstance().refreshAllData();
            MainWindow.getInstance().setSummaryLock(false);
        } else {
            updateLater = true;
            java.awt.EventQueue.invokeLater(getRefreshLaterRunnable());
        }
    }

    private Runnable getRefreshLaterRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (updateLater && System.currentTimeMillis() > changeTime) {
                    updateLater = false;
                    MainWindow.getInstance().setSummaryLock(true);
                    MainWindow.getInstance().refreshAllData();
                    MainWindow.getInstance().setSummaryLock(false);
                } else if (updateLater) {
                    java.awt.EventQueue.invokeLater(this);
                }
            }
        };
    }
    //</editor-fold>

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();

        if (activeFile.getRulesetId() < 0) {
            triggerField.setEditable(false);
            conditionsArea.setEditable(false);
            scriptArea.setEditable(false);
            textArea.setEditable(false);
            optionsArea.setEditable(false);
            notesArea.setEditable(false);
        } else {
            if (activeFile.isDirectory()) {
                triggerField.setEditable(false);
                conditionsArea.setEditable(false);
                scriptArea.setEditable(false);
                textArea.setEditable(false);
                optionsArea.setEditable(false);
            } else {
                triggerField.setEditable(true);
                conditionsArea.setEditable(true);
                scriptArea.setEditable(true);
                textArea.setEditable(true);
                optionsArea.setEditable(true);
            }
            notesArea.setEditable(true);
        }


        if (writeLock || MainWindow.getInstance().isSummaryLock()) {
            writeLock = false;
        } else {
            suppressChange = true;
            triggerField.setText(activeFile.getRule().getTrigger());
            conditionsArea.setText(activeFile.getRule().getConditions());
            scriptArea.setText(activeFile.getRule().getScript());
            textArea.setText(activeFile.getRule().getText());
            optionsArea.setText(activeFile.getRule().getOptions());
            notesArea.setText(activeFile.getRule().getNotes());

            revalidate();
            suppressChange = false;
        }

    }

    @Override
    public void getPreferences(Preferences prefs) {}

    @Override
    public void setPreferences(Preferences prefs) {}

}
