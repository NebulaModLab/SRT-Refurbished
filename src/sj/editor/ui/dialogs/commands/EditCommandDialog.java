/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.commands;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import sj.editor.MainWindow;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.ShowOn;
import sj.editor.data.commands.CommandField;
import sj.editor.data.rules.RulesManager;

/**
 * Used to define and edit commands."
 * @author SafariJohn
 */
public class EditCommandDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(EditCommandDialog.class.getName());

    private final JPanel commandPanel = new JPanel();

    // Title
    // Command Name
    private final JLabel commandNameLabel = new JLabel("Name:");
    private final JTextField commandNameField = new JTextField();

    // Check boxes for which screens it should be shown on
    private final JLabel showOnScreenLabel = new JLabel("Show for:");
    private final JCheckBox conditionCheckBox = new JCheckBox("Conditions");
    private final JCheckBox scriptCheckBox = new JCheckBox("Script");

    // Displays any notes
    private final JLabel notesLabel = new JLabel("Notes:");
    private final JTextArea notesArea = new JTextArea();

    // Whole thing needs to be in a scroll pane
    private final JScrollPane fieldsScrollPane = new JScrollPane();
    private final JPanel fieldsPanel = new JPanel();

    private final List<EditCommandFieldPanel> fieldLines = new ArrayList<>();

    private final Map<String, Boolean> readyFlags = new HashMap<>();

    // Input row shows input name, type, and whether it is optional or not
    // Enums need to be defined and listed somehow
    // Integers and floats need minimums and maximums
    // Setting type to NONE removes a row??? No, NONE should only appear appear when adding
    // Removing a row should be done by an X

    // Whole thing needs to be in a scroll pane

    // At bottom are ok and cancel buttons
    // Exit button at top is cancel
    private final JButton okayButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final Command com;
    private final Command originalCom;
    private boolean canceled;

    public EditCommandDialog(Command com) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Edit Command");
        setAlwaysOnTop(true);
        setResizable(true);

        originalCom = com;
        canceled = false;

        if (com != null) this.com = new Command(com, com.getRulesetId());
        else this.com = new Command(RulesManager.getActiveRule().getRulesetId());

        com = this.com;

        commandNameField.setText(com.getName());
        commandNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
            public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateName();
            }
        });

        conditionCheckBox.setSelected(com.isConditionCommand());
        conditionCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setWhichPanels();
            }
        });

        scriptCheckBox.setSelected(com.isScriptCommand());
        scriptCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setWhichPanels();
            }
        });

        setWhichPanels(); // Lazy initial enable/disable

        notesArea.setText(com.getNotes());
        notesArea.setBorder(BorderFactory.createEtchedBorder());
        notesArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
            public void removeUpdate(DocumentEvent e) { changedUpdate(e); }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateNotes();
            }
        });


        fieldsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        fieldsScrollPane.setViewportView(fieldsPanel);
        fieldsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        fieldsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        okayButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canceled = false;
                setVisible(false);
                applyResult();
            }
        });
        okayButton.setText("Confirm");
        okayButton.setEnabled(false);

        cancelButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canceled = true;
                setVisible(false);
                applyResult();
            }
        });
        cancelButton.setText("Cancel");

        rebuildDialog();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            canceled = true;
            applyResult();
        }
    }

    private void applyResult() {
        if (canceled) return;

        Ruleset ruleset = RulesetsManager.getRuleset(com.getRulesetId());

        if (ruleset == null) return; // Ruleset was closed before add/edit was finished

        if (originalCom != null) {
            int index = ruleset.getCommands().indexOf(originalCom);
            // Index check because the original command could have been deleted
            if (index >= 0) {
                ruleset.getCommands().remove(originalCom);
                ruleset.getCommands().add(index, com);
            } else {
                ruleset.getCommands().add(com);
            }
        } else {
            ruleset.getCommands().add(com);
        }

        RulesetsManager.getCommandsChangedRulesets().add(com.getRulesetId());

        MainWindow.getInstance().refreshAllData();
    }

    private void rebuildDialog() {
        invalidate();

        // Rebuild the field components
        readyFlags.clear();
        fieldLines.clear();
        createInputFieldComponents();

        fieldsPanel.setLayout(getFieldsPanelLayout(fieldsPanel));
        commandPanel.setLayout(getCommandPanelLayout(commandPanel));

        getContentPane().setLayout(getDialogLayout());

        setMinimumSize(new Dimension(getPreferredSize().width + 100,
                    getPreferredSize().height + 100));
    }

    private void setWhichPanels() {
        boolean cSelected = conditionCheckBox.isSelected();
        boolean sSelected = scriptCheckBox.isSelected();

        if (cSelected && sSelected) {
            com.setWhichPanelsToShowOn(ShowOn.BOTH);
            conditionCheckBox.setEnabled(true);
            scriptCheckBox.setEnabled(true);
        } else if (cSelected) {
            com.setWhichPanelsToShowOn(ShowOn.COND);
            conditionCheckBox.setEnabled(false);
            scriptCheckBox.setEnabled(true);
        } else if (sSelected) {
            com.setWhichPanelsToShowOn(ShowOn.SCRIPT);
            conditionCheckBox.setEnabled(true);
            scriptCheckBox.setEnabled(false);
        }

    }

    //<editor-fold defaultstate="collapsed" desc="Layout Methods">
    private LayoutManager getDialogLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);

        // Create horizontal groupings
        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseHSequence = layout.createSequentialGroup();
        Group firstTierHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup buttonsHLine = layout.createSequentialGroup();

        baseHGroup.addGroup(baseHSequence);
        baseHSequence.addGroup(firstTierHGroup);
        firstTierHGroup.addComponent(commandPanel);
        firstTierHGroup.addGroup(buttonsHLine);
        buttonsHLine.addGap(0, 10, Short.MAX_VALUE)
                    .addComponent(okayButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cancelButton);

        layout.setHorizontalGroup(baseHGroup);


        Group baseVGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseVSequence = layout.createSequentialGroup();
        Group buttonsVLine = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        baseVGroup.addGroup(baseVSequence);
        baseVSequence.addComponent(commandPanel);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        baseVSequence.addGroup(buttonsVLine);
        buttonsVLine.addComponent(okayButton)
                    .addComponent(cancelButton);

        layout.setVerticalGroup(baseVGroup);


        return layout;
    }

    private LayoutManager getCommandPanelLayout(JPanel panel) {
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);

        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup nameHSequence = layout.createSequentialGroup();
        SequentialGroup panelsHSequence = layout.createSequentialGroup();
        SequentialGroup notesHSequence = layout.createSequentialGroup();

        baseHGroup.addGroup(nameHSequence);
            nameHSequence.addComponent(commandNameLabel);
            nameHSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            nameHSequence.addComponent(commandNameField, 0, 0, Short.MAX_VALUE);
        baseHGroup.addGroup(panelsHSequence);
            panelsHSequence.addComponent(showOnScreenLabel);
            panelsHSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            panelsHSequence.addComponent(conditionCheckBox);
            panelsHSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            panelsHSequence.addComponent(scriptCheckBox);
        baseHGroup.addGroup(notesHSequence);
            notesHSequence.addComponent(notesLabel);
            notesHSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
            notesHSequence.addComponent(notesArea);
        baseHGroup.addComponent(fieldsScrollPane);

        layout.setHorizontalGroup(baseHGroup);


        Group baseVGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        SequentialGroup baseVSequence = layout.createSequentialGroup();
        Group nameVGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        Group panelsVGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);
        Group notesVGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        baseVGroup.addGroup(baseVSequence);
        baseVSequence.addGroup(nameVGroup);
            nameVGroup.addComponent(commandNameLabel);
            nameVGroup.addComponent(commandNameField,
                        commandNameField.getPreferredSize().height,
                        commandNameField.getPreferredSize().height,
                        commandNameField.getPreferredSize().height);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        baseVSequence.addGroup(panelsVGroup);
            panelsVGroup.addComponent(showOnScreenLabel);
            panelsVGroup.addComponent(conditionCheckBox);
            panelsVGroup.addComponent(scriptCheckBox);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        baseVSequence.addGroup(notesVGroup);
            notesVGroup.addComponent(notesLabel);
            notesVGroup.addComponent(notesArea);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        baseVSequence.addComponent(fieldsScrollPane);

        layout.setVerticalGroup(baseVGroup);

        return layout;
    }

    private LayoutManager getFieldsPanelLayout(JPanel panel) {
        GroupLayout layout = new GroupLayout(panel);

        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseHSequence = layout.createSequentialGroup();
        Group firstTierHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        baseHGroup.addGroup(baseHSequence);
        baseHSequence.addGroup(firstTierHGroup);
        createHorizontalFieldGroups(layout, firstTierHGroup);

        layout.setHorizontalGroup(baseHGroup);

        Group baseVGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseVSequence = layout.createSequentialGroup();

        baseVGroup.addGroup(baseVSequence);
        createVerticalFieldGroups(layout, baseVSequence);

        layout.setVerticalGroup(baseVGroup);

        return layout;
    }

    private void createInputFieldComponents() {
        EditCommandFieldPanel lastPanel = null;
        for (CommandField field : com.getFields()) {

            EditCommandFieldPanel panel = new EditCommandFieldPanel(field) {
                @Override
                public void notifyParent() {
                    removeDeletedFields();
                    validateCommand();
                }
            };
            fieldLines.add(panel);

            lastPanel = panel;
        }

        if (lastPanel != null) lastPanel.setAllowOptional(true);

        addNewFieldLine();
    }

    private void createHorizontalFieldGroups(GroupLayout layout, Group parent) {
        // Go line by line
        for (EditCommandFieldPanel panel : fieldLines) {
            parent.addComponent(panel);
        }
    }

    private void createVerticalFieldGroups(GroupLayout layout, SequentialGroup parent) {
        // Go line by line
        boolean first = true;
        for (EditCommandFieldPanel panel : fieldLines) {
            if (!first) parent.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

            parent.addComponent(panel);

            first = false;
        }
    }
    //</editor-fold>

    private void addNewFieldLine() {
        EditCommandFieldPanel newLine = getNewFieldPanel();

        fieldLines.add(newLine);

        fieldsPanel.setLayout(getFieldsPanelLayout(fieldsPanel));
    }

    private EditCommandFieldPanel getNewFieldPanel() {
        return new EditCommandFieldPanel(null) {
            private boolean isNewLine = true;
            @Override
            public void rebuildLayout() {
                super.rebuildLayout();

                if (!isNewLine) return;
                isNewLine = false;

                addCommandField(getField());
            }

            @Override
            public void notifyParent() {
                removeDeletedFields();
                validateCommand();
            }
        };
    }

    private void addCommandField(CommandField field) {
        com.getFields().add(field);

        if (fieldLines.size() > 1) fieldLines.get(fieldLines.size() - 2).setAllowOptional(false);
        fieldLines.get(fieldLines.size() - 1).setAllowOptional(true);

        fieldsPanel.setLayout(getFieldsPanelLayout(fieldsPanel));
        commandPanel.setLayout(getCommandPanelLayout(commandPanel));

        addNewFieldLine();

        setMinimumSize(new Dimension(getPreferredSize().width + 100,
                    getPreferredSize().height + 100));
    }

    private void removeDeletedFields() {
        List<EditCommandFieldPanel> toRemove = new ArrayList<>();
        for (EditCommandFieldPanel line : fieldLines) {
            if (line.shouldRemove()) {
                toRemove.add(line);
                com.getFields().remove(line.getField());
                fieldsPanel.remove(line);
            }
        }
        fieldLines.removeAll(toRemove);

        fieldsPanel.setLayout(getFieldsPanelLayout(fieldsPanel));

    }

    private void validateCommand() {
        boolean allTrue = true;

        if (commandNameField.getText().isEmpty()) allTrue = false;

        for (EditCommandFieldPanel fieldPanel : fieldLines) {
            allTrue &= fieldPanel.validateField();
        }

        okayButton.setEnabled(allTrue);
    }

    private void updateName() {
        com.setName(commandNameField.getText());
    }

    private void updateNotes() {
        com.setNotes(notesArea.getText());
    }
}
