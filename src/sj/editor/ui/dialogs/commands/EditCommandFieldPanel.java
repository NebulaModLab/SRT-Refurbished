/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.commands;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.FieldType;
import sj.editor.data.commands.CommandField;
import sj.editor.ui.data.KeyFilter;

/**
 * @author SafariJohn
 */
public abstract class EditCommandFieldPanel extends JPanel {
    private static final Color ERROR_COLOR = Color.RED;
    private static Color NORMAL_COLOR = Color.BLACK;

    private final JLabel nameLabel, typeLabel;
    private final JTextField nameField;
    private final JComboBox typeBox;
    private final JCheckBox minCheck, maxCheck, optionalCheck;
    private final JSpinner minSpinner, maxSpinner;
    private final JButton optionsButton, removeButton;


    private final CommandField field;
    private boolean allowOptional;

    private boolean toRemove;

    public EditCommandFieldPanel(CommandField cField) {
        allowOptional = false;
        toRemove = false;

        if (cField == null) {
            field = new CommandField(FieldType.NONE);
        } else {
//            field = new CommandField(cField);
            field = cField;
        }

        nameLabel = new JLabel("Field:");
        nameField = new JTextField();

        nameField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
            public void removeUpdate(DocumentEvent e) { changedUpdate(e); }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String name = nameField.getText().trim();
                field.setName(name);
                notifyParent();
            }
        });

        if (field.getType() == Command.FieldType.KEY) {
            AbstractDocument doc = (AbstractDocument) nameField.getDocument();
            doc.setDocumentFilter(new KeyFilter(doc.getLength()));
        }

        nameField.setText(field.getName());

        typeLabel = new JLabel("Type:");
        typeBox = getFieldTypeBox();
        if (field.getType() == FieldType.NONE) {
            typeLabel.setText("Select type:");
            typeBox.addItem(FieldType.NONE);
            typeBox.setSelectedItem(FieldType.NONE);
        } else {
            typeBox.setSelectedItem(field.getType());
        }
        typeBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                FieldType type = (FieldType) e.getItem();


                if (type != field.getType()) {
                    field.setType(type);
                    typeBox.removeItem(FieldType.NONE);
                    rebuildLayout();

                    if (field.getType() != FieldType.INTEGER
                                && field.getType() != FieldType.FLOAT) {
                        field.setHasMin(false);
                        field.setHasMax(false);
                    }
                }
            }
        });

        minCheck = new JCheckBox("Min:");
        minCheck.setSelected(field.hasMin());
        minCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                field.setHasMin(minCheck.isSelected());
            }
        });

        maxCheck = new JCheckBox("Max:");
        maxCheck.setSelected(field.hasMax());
        maxCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                field.setHasMax(maxCheck.isSelected());
            }
        });

        minSpinner = new JSpinner();
        maxSpinner = new JSpinner();

        resetNumberSpinnerModels();


        optionsButton = new JButton();
        optionsButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOptionsDialog();
            }
        });

        NORMAL_COLOR = optionsButton.getForeground();
        if (field.getOptions().isEmpty()) {
            optionsButton.setForeground(ERROR_COLOR);
        }

        optionsButton.setText("Set Options");


        optionalCheck = new JCheckBox("Optional?");
        optionalCheck.setHorizontalTextPosition(SwingConstants.LEFT);
        optionalCheck.setSelected(field.isOptional());
        optionalCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                field.setOptional(optionalCheck.isSelected());
            }
        });

        removeButton = new JButton();
        removeButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toRemove = true;
                notifyParent();
            }
        });
        removeButton.setText("X");

        setLayout(getPanelLayout());
    }

    private JComboBox getFieldTypeBox() {
        JComboBox<Command.FieldType> box = new JComboBox<>();
        box.addItem(Command.FieldType.KEY);
        box.addItem(Command.FieldType.STRING);
        box.addItem(Command.FieldType.BOOLEAN);
        box.addItem(Command.FieldType.INTEGER);
        box.addItem(Command.FieldType.FLOAT);
        box.addItem(Command.FieldType.COLOR);
        box.addItem(Command.FieldType.ENUM);

        box.setMaximumSize(box.getPreferredSize());

        return box;
    }

    private void resetNumberSpinnerModels() {
        SpinnerModel minModel;
        SpinnerModel maxModel;
        if (field.getType() == Command.FieldType.INTEGER) {
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;

            minModel = new SpinnerNumberModel(field.getMin(), min, max, 1);
            maxModel = new SpinnerNumberModel(field.getMax(), min, max, 1);

            minModel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    float value = (int) minSpinner.getValue();

                    field.setMin(value);

                    if (field.hasMax() && field.getMax() < value) field.setMax(value);
                }
            });

            maxModel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    float value = (int) maxSpinner.getValue();

                    field.setMax(value);

                    if (field.hasMin() && field.getMin() < value) field.setMin(value);
                }
            });
        } else {
            Float min = -Float.MAX_VALUE;
            Float max = Float.MAX_VALUE;
            Float step = 1f;

            minModel = new SpinnerNumberModel((Float) field.getMin(), min, max, step);
            maxModel = new SpinnerNumberModel((Float) field.getMax(), min, max, step);

            minModel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    float value = (float) minSpinner.getValue();

                    field.setMin(value);

                    if (field.hasMax() && field.getMax() < value) field.setMax(value);
                }
            });

            maxModel.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    float value = (float) maxSpinner.getValue();

                    field.setMax(value);

                    if (field.hasMin() && field.getMin() < value) field.setMin(value);
                }
            });
        }

        minSpinner.setModel(minModel);
        maxSpinner.setModel(maxModel);
    }

    public void rebuildLayout() {
        setLayout(getPanelLayout());

        if (field.getType() != FieldType.NONE) {
            typeLabel.setText("Type:");
        }

        if (field.getType() == Command.FieldType.KEY) {
            AbstractDocument doc = (AbstractDocument) nameField.getDocument();
            doc.setDocumentFilter(new KeyFilter(doc.getLength()));
        } else {
            AbstractDocument doc = (AbstractDocument) nameField.getDocument();
            doc.setDocumentFilter(null);
        }

        nameField.setText(field.getName());

        if (field.getOptions().isEmpty()) optionsButton.setForeground(ERROR_COLOR);
        else optionsButton.setForeground(NORMAL_COLOR);

        resetNumberSpinnerModels();

        boolean numVisible = field.getType() == Command.FieldType.INTEGER
                    || field.getType() == Command.FieldType.FLOAT;
        boolean enumVisible = field.getType() == FieldType.ENUM;

        minCheck.setVisible(numVisible);
        minSpinner.setVisible(numVisible);
        maxCheck.setVisible(numVisible);
        maxSpinner.setVisible(numVisible);
        optionsButton.setVisible(enumVisible);
        optionalCheck.setVisible(allowOptional);
    }

    private LayoutManager getPanelLayout() {
        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateContainerGaps(false);

        // Create horizontal groupings
        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseHSequence = layout.createSequentialGroup();

        baseHGroup.addGroup(baseHSequence);

        if (field.getType() != FieldType.NONE) {
            baseHSequence.addComponent(nameLabel);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
            baseHSequence.addComponent(nameField, 0, 0, Short.MAX_VALUE);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
        }

        baseHSequence.addComponent(typeLabel);
        baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
        baseHSequence.addComponent(typeBox);
        baseHSequence.addPreferredGap(ComponentPlacement.RELATED);

        if (field.getType() == Command.FieldType.INTEGER
                    || field.getType() == Command.FieldType.FLOAT) {
            baseHSequence.addComponent(minCheck);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
            baseHSequence.addComponent(minSpinner, 0, 0, Short.MAX_VALUE);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
            baseHSequence.addComponent(maxCheck);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
            baseHSequence.addComponent(maxSpinner, 0, 0, Short.MAX_VALUE);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
        }

        if (field.getType() == Command.FieldType.ENUM) {
            baseHSequence.addComponent(optionsButton);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
        }

        if (allowOptional) {
            baseHSequence.addComponent(optionalCheck);
            baseHSequence.addPreferredGap(ComponentPlacement.RELATED);
        }

        if (field.getType() != FieldType.NONE) baseHSequence.addComponent(removeButton);

        layout.setHorizontalGroup(baseHGroup);


        Group baseVGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);

        if (field.getType() != FieldType.NONE) {
            baseVGroup.addComponent(nameLabel);
            baseVGroup.addComponent(nameField,
                        nameField.getPreferredSize().height,
                        nameField.getPreferredSize().height,
                        nameField.getPreferredSize().height);
        }

        baseVGroup.addComponent(typeLabel);
        baseVGroup.addComponent(typeBox);

        if (field.getType() == Command.FieldType.INTEGER
                    || field.getType() == Command.FieldType.FLOAT) {
            baseVGroup.addComponent(minCheck);
            baseVGroup.addComponent(minSpinner,
                        minSpinner.getPreferredSize().height,
                        minSpinner.getPreferredSize().height,
                        minSpinner.getPreferredSize().height);
            baseVGroup.addComponent(maxCheck);
            baseVGroup.addComponent(maxSpinner,
                        maxSpinner.getPreferredSize().height,
                        maxSpinner.getPreferredSize().height,
                        maxSpinner.getPreferredSize().height);
        }

        if (field.getType() == Command.FieldType.ENUM) {
            baseVGroup.addComponent(optionsButton,
                        optionsButton.getPreferredSize().height,
                        optionsButton.getPreferredSize().height,
                        optionsButton.getPreferredSize().height);
        }

        if (allowOptional) {
            baseVGroup.addComponent(optionalCheck);
        }

        if (field.getType() != FieldType.NONE) {
            baseVGroup.addComponent(removeButton);
//                        removeButton.getPreferredSize().height,
//                        removeButton.getPreferredSize().height,
//                        removeButton.getPreferredSize().height);
        }

        layout.setVerticalGroup(baseVGroup);


        return layout;
    }

    private void showOptionsDialog() {
        new OptionsDialog(field).setVisible(true);

        if (field.getOptions().isEmpty()) optionsButton.setForeground(ERROR_COLOR);
        else optionsButton.setForeground(NORMAL_COLOR);

        notifyParent();
    }

    protected abstract void notifyParent();

    public void setAllowOptional(boolean allow) {
        allowOptional = allow;
        rebuildLayout();
    }

    public boolean shouldRemove() {
        return toRemove;
    }

    public boolean validateField() {
        if (field.getType() == FieldType.NONE) return true;


        int emptyLength = 0;
        if (field.getType() == FieldType.KEY) emptyLength = 1;

        if (field.getName().length() <= emptyLength) return false;

        if (field.getType() == FieldType.ENUM && field.getOptions().isEmpty()) return false;

        return !shouldRemove();
    }

    public CommandField getField() {
        return field;
    }
}