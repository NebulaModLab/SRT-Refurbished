/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.commands;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.Group;
import javax.swing.*;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.FieldType;
import sj.editor.data.commands.CommandField;
import sj.editor.data.commands.TFVContainer;
import sj.editor.ui.data.KeyFilter;
import sj.editor.ui.data.TFVDnDHandler;

/**
 * Author: SafariJohn
 */
public class CommandInputDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(CommandInputDialog.class.getName());
    // Used to fill out the fields for a command when copy-pasting it

    private final JPanel commandPanel = new JPanel();
    private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private final JLabel nameLabel = new JLabel();

    // Displays any notes
    private final JTextArea notesArea = new JTextArea();

    // Whole thing needs to be in a scroll pane
    private final JScrollPane fieldsScrollPane = new JScrollPane();
    private final JPanel fieldsPanel = new JPanel();

    // List of lines, with each member of a line mapped to a column
    private static final List<Map> INPUT_LINES = new ArrayList<>();
    private static final String COL_NAME = "0nameColumn";
    private static final String COL_ONE = "1colOne";
    private static final String COL_TWO = "2colTwo";
    private static final String COL_THREE = "3colThree";
    private static final String COL_FOUR = "4colFour";

    private static final Map<String, Boolean> READY_FLAGS = new HashMap<>();

    // If there are key fields, show known keys

    private final JPanel keyPanel = new JPanel();
    private final JLabel keysLabel = new JLabel("Known Variables:");
    private final JLabel filterLabel = new JLabel("Filter:");
    private final JTextField filterField = new JTextField();
    private final JScrollPane keyScrollPane = new JScrollPane();
    private final JList keyList = new JList();
    private final DefaultListModel keyListModel = new DefaultListModel();

    // At bottom are ok and cancel buttons
    // Exit button at top is cancel
    private final JButton okayButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final Command com;
    private String filter;

    private boolean canceled;

    public CommandInputDialog(Command com) {
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        // Dialog is disposed of by TextAndCommandTransferHandler

        setMinimumSize(new Dimension(400, 350));
        setAlwaysOnTop(true);
        setResizable(true);
        setModal(true);

        this.com = com;
        canceled = false;
        filter = "";


        split.setBorder(BorderFactory.createEtchedBorder());
        split.setDividerLocation(220);


        setTitle("Fill Out \"" + com.getName() + "\" Command");

        nameLabel.setText(com.getName());

        notesArea.setText(com.getNotes());
        notesArea.setEditable(false);

        // Rebuild the field components
        READY_FLAGS.clear();
        INPUT_LINES.clear();
        createInputFieldComponents();

        fieldsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        fieldsScrollPane.setViewportView(fieldsPanel);
        fieldsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        fieldsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fieldsPanel.setLayout(getFieldsPanelLayout(fieldsPanel));

        commandPanel.setLayout(getCommandPanelLayout(commandPanel));

        // Lay out keys list
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

                refreshKeyList();
            }
            //</editor-fold>
        });

        filterField.setToolTipText("Filtering is not case sensitive and does not detect ruleset names.");

        keyScrollPane.setBorder(BorderFactory.createEtchedBorder());
        keyScrollPane.setViewportView(keyList);

        keyList.setDragEnabled(true);
        keyList.setTransferHandler(new TFVDnDHandler());
        keyList.setToolTipText("<html>A variable listed here may not be accessible from this rule."
                    + "<br>Drag and drop is currently broken for reasons unknown. Copy-paste using the keyboard still works.</html>");
        keyList.setModel(keyListModel);

        GroupLayout layout = new GroupLayout(keyPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(keysLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(filterLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(filterField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(keyScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(keysLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(filterLabel)
                        .addComponent(filterField))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(keyScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        keyPanel.setLayout(layout);

        refreshKeyList();


        okayButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canceled = false;
                setVisible(false);
            }
        });
        okayButton.setText("Confirm");
//        okayButton.setEnabled(false);
        Map<String, Component> okayMap = new HashMap<>();
        okayMap.put(COL_THREE, okayButton);
        INPUT_LINES.add(okayMap); // So OptionalAction can access it

        boolean allTrue = true;
        for (String key : READY_FLAGS.keySet()) {
            allTrue &= READY_FLAGS.get(key);
        }
        okayButton.setEnabled(allTrue);

        cancelButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canceled = true;
                setVisible(false);
            }
        });
        cancelButton.setText("Cancel");

        getContentPane().setLayout(getDialogLayout());
//        getContentPane().validate();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            canceled = true;
        }
    }

    public String getResult() {
        if (canceled) return "";

        String result = com.getName();

        for (Map line : INPUT_LINES) {
            // Check if an optional field is being skipped
            Object n = line.get(COL_NAME);
            if (n instanceof JCheckBox) {
                JCheckBox box = (JCheckBox) n;
                if (!box.isSelected()) continue;
            }

            Object c = line.get(COL_ONE);

            if (c instanceof JComboBox) {
                JComboBox box = (JComboBox) c;
                result += " " + box.getSelectedItem();
            }

            if (c instanceof JTextField) {
                JTextField field = (JTextField) c;
                result += " " + field.getText().trim();
            }

            if (c instanceof JSpinner) {
                JSpinner num = (JSpinner) c;
                result += " " + num.getValue();
            }
        }

        return result;
    }

    private LayoutManager getCommandPanelLayout(JPanel panel) {
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateContainerGaps(true);

        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseHSequence = layout.createSequentialGroup();
        Group firstTierHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

        baseHGroup.addGroup(baseHSequence);
        baseHSequence.addGroup(firstTierHGroup);
        firstTierHGroup.addComponent(nameLabel);
        if (!com.getNotes().isEmpty()) firstTierHGroup.addComponent(notesArea);
        firstTierHGroup.addComponent(fieldsScrollPane);

        layout.setHorizontalGroup(baseHGroup);


        Group baseVGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseVSequence = layout.createSequentialGroup();

        baseVGroup.addGroup(baseVSequence);
        baseVSequence.addComponent(nameLabel);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        if (!com.getNotes().isEmpty()) {
            baseVSequence.addComponent(notesArea);
            baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        }
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

    private LayoutManager getDialogLayout() {
        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setAutoCreateContainerGaps(true);

        boolean showKeyList = false;

        for (CommandField field : com.getFields()) {
            if (field.getType() == FieldType.KEY
                        || field.getType() == FieldType.STRING) {
                showKeyList = true;
                break;
            }
        }

        if (showKeyList) {
            split.setLeftComponent(commandPanel);
            split.setRightComponent(keyPanel);
        }

        // Create horizontal groupings
        Group baseHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup baseHSequence = layout.createSequentialGroup();
        Group firstTierHGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup buttonsHLine = layout.createSequentialGroup();

        baseHGroup.addGroup(baseHSequence);
        baseHSequence.addGroup(firstTierHGroup);
        if (showKeyList) firstTierHGroup.addComponent(split);
        else firstTierHGroup.addComponent(commandPanel);
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
        if (showKeyList) baseVSequence.addComponent(split);
        else baseVSequence.addComponent(commandPanel);
        baseVSequence.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);
        baseVSequence.addGroup(buttonsVLine);
            buttonsVLine.addComponent(okayButton)
            .addComponent(cancelButton);

        layout.setVerticalGroup(baseVGroup);


        return layout;
    }

    private void createInputFieldComponents() {
        for (CommandField field : com.getFields()) {
            FieldType type = field.getType();
            int index = com.getFields().indexOf(field);

            switch (type) {
                case BOOLEAN:
                    INPUT_LINES.add(createBooleanGroup(field, index));
                    break;
                case ENUM:
                    INPUT_LINES.add(createEnumGroup(field, index));
                    break;
                case FLOAT:
                case INTEGER:
                    boolean intType = type == FieldType.INTEGER;
                    INPUT_LINES.add(createNumberGroup(field, intType, index));
                    break;
                case KEY:
                case STRING:
                    boolean keyType = type == FieldType.KEY;
                    INPUT_LINES.add(createStringGroup(field, keyType, index));
                    break;
                default:
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Component creation methods">
    private Component createNameLine(CommandField field, int index) {
        if (field.isOptional()) {
            JCheckBox nameBox = new JCheckBox(field.getName(), true);
            nameBox.setAction(new OptionalAction(index));
            nameBox.setText(field.getName());
            return nameBox;
        } else {
            return new JLabel(field.getName());
        }
    }

    private Map<String, Component> createBooleanGroup(
                CommandField field, int index) {
        Map<String, Component> line = new HashMap<>();

        line.put(COL_NAME, createNameLine(field, index));

        JComboBox yesNo = new JComboBox();
        yesNo.addItem("true");
        yesNo.addItem("false");

        line.put(COL_ONE, yesNo);

        return line;
    }

    private Map<String, Component> createEnumGroup(
                CommandField field, int index) {
        Map<String, Component> line = new HashMap<>();

        line.put(COL_NAME, createNameLine(field, index));

        JComboBox selection = new JComboBox();
        for (String opt : field.getOptions()) {
            selection.addItem(opt);
        }

        line.put(COL_ONE, selection);

        return line;
    }


    private Map<String, Component> createNumberGroup(
                CommandField field, boolean isInt, int index) {
        Map<String, Component> line = new HashMap<>();

        line.put(COL_NAME, createNameLine(field, index));

        SpinnerModel model;
        if (isInt) {
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;
            int start = 0;

            if (field.hasMin()) min = (int) field.getMin();
            if (field.hasMax()) max = (int) field.getMax();

            if (start < min) start = min;
            if (start > max) start = max;

            model = new SpinnerNumberModel(start, min, max, 1);
        } else {
            Float min = Float.MIN_VALUE;
            Float max = Float.MAX_VALUE;
            Float start = 0f;
            Float step = 1f;

            if (field.hasMin()) min = field.getMin();
            if (field.hasMax()) max = field.getMax();

            if (start < min) start = min;
            if (start > max) start = max;

            if (max - min < 3f) step = 0.1f;

            model = new SpinnerNumberModel(start, min, max, step);
        }

        JSpinner spinner = new JSpinner(model);

        line.put(COL_ONE, spinner);

        float minF = field.getMin();
        float maxF = field.getMax();

        if (isInt) {
            minF = (int) minF;
            maxF = (int) maxF;
        }

        if (field.hasMin() && field.hasMax()) {
            line.put(COL_TWO, new JLabel("Min: " + minF));
            line.put(COL_THREE, new JLabel("Max: " + maxF));
        } else if (field.hasMin()) {
            line.put(COL_TWO, new JLabel("Min: " + minF));
        } else if (field.hasMax()) {
            line.put(COL_TWO, new JLabel("Max: " + maxF));
        }



        return line;
    }

    private Map<String, Component> createStringGroup(
                CommandField field, boolean isKey, int index) {
        Map<String, Component> line = new HashMap<>();

        line.put(COL_NAME, createNameLine(field, index));


        JTextField textComp = new JTextField();

        if (isKey) {
            AbstractDocument doc = (AbstractDocument) textComp.getDocument();
            doc.setDocumentFilter(new KeyFilter(doc.getLength()));
        }

        textComp.setTransferHandler(new TransferHandler() {
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
//                if (support.isDrop()) return false;

                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }
            //</editor-fold>
        });

        line.put(COL_ONE, textComp);

        // Blank text areas cause the okay button to grey out
        READY_FLAGS.put(textComp.getDocument().toString(), false);
        textComp.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changedUpdate(e); }
            public void removeUpdate(DocumentEvent e) { changedUpdate(e); }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (e.getDocument().getLength() > 0) {
                    READY_FLAGS.put(e.getDocument().toString(), true);
                } else {
                    READY_FLAGS.put(e.getDocument().toString(), false);
                    okayButton.setEnabled(false);
                    return;
                }

                boolean allTrue = true;
                for (String key : READY_FLAGS.keySet()) {
                    allTrue &= READY_FLAGS.get(key);
                }

                if (allTrue) okayButton.setEnabled(true);
            }
        });


        return line;

    }

    private static class OptionalAction extends AbstractAction {
        private final int index;

        public OptionalAction(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Map<String, Component> line = INPUT_LINES.get(index);

            JCheckBox optional = (JCheckBox) line.get(COL_NAME);
            boolean selected = optional.isSelected();

            for (String key : line.keySet()) {
                if (key.equals(COL_NAME)) continue;

                Component c = line.get(key);
                c.setEnabled(selected);

                // Disabled textFields don't block completing the command
                if (c instanceof JTextComponent) {
                    Document doc = ((JTextComponent) c).getDocument();
                    Component okayButton = (Component) INPUT_LINES
                                .get(INPUT_LINES.size() - 1).get(COL_THREE);

                    if (!selected) {
                        READY_FLAGS.put(doc.toString(), true);
                    } else {
                        if (doc.getLength() > 0) {
                            READY_FLAGS.put(doc.toString(), true);
                        } else {
                            READY_FLAGS.put(doc.toString(), false);
                            okayButton.setEnabled(false);
                            return;
                        }
                    }

                    boolean allTrue = true;
                    for (String k : READY_FLAGS.keySet()) {
                        allTrue &= READY_FLAGS.get(k);
                    }

                    if (allTrue) okayButton.setEnabled(true);
                }
            }
        }

    }
//</editor-fold>


    private void createHorizontalFieldGroups(GroupLayout layout, Group parent) {
        // Go line by line
        for (Map<String, Component> line : INPUT_LINES) {
            SequentialGroup group = layout.createSequentialGroup();
            parent.addGroup(group);

            // Adding all the components
            boolean first = true;
            List<String> keys = new ArrayList<>(line.keySet());
            keys.sort(String.CASE_INSENSITIVE_ORDER);
            for (String key : keys) {
                Component c = line.get(key);

                if (!first) group.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

                if (c instanceof JLabel || c instanceof JCheckBox) {
                    group.addComponent(c);
                } else {
                    group.addComponent(c, 0, 0, Short.MAX_VALUE);
                }


                first = false;
            }

            group.addContainerGap();
        }
    }

    private void createVerticalFieldGroups(GroupLayout layout, SequentialGroup parent) {
        // Go line by line
        boolean first = true;
        for (Map<String, Component> line : INPUT_LINES) {
            if (!first) parent.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

            Group group = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            parent.addGroup(group);

            // Adding all the components
            List<String> keys = new ArrayList<>(line.keySet());
            keys.sort(String.CASE_INSENSITIVE_ORDER);
            for (String key : keys) {
                Component c = line.get(key);
                group.addComponent(c, c.getPreferredSize().height,
                            c.getPreferredSize().height,
                            c.getPreferredSize().height);
            }

            first = false;
        }
    }

    private void refreshKeyList() {
        keyListModel.clear();

        for (String variable : RulesetsManager.getVanilla().getSVariables()) {
            if (!variable.startsWith("$")) continue;
            if (!variable.toLowerCase().contains(filter.toLowerCase())) continue;

            keyListModel.addElement(new TFVContainer(variable, RulesetsManager.getVanilla().getId()));
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            for (String variable : ruleset.getSVariables()) {
                if (!variable.startsWith("$")) continue;
                if (!variable.toLowerCase().contains(filter.toLowerCase())) continue;

                keyListModel.addElement(new TFVContainer(variable, ruleset.getId()));
            }
        }
    }
}
