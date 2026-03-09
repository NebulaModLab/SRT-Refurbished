/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.commands;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import sj.editor.data.commands.CommandField;

/**
 * @author SafariJohn (original SRT)
 */
public class OptionsDialog extends JDialog {

    private final JLabel addOptionLabel = new JLabel("Add option:");
    private final JTextField addOptionField = new JTextField();
    private final JButton addButton = new JButton();

    private final JLabel optionsLabel = new JLabel("Options:");
    private final JList optionsList = new JList();
    private final JScrollPane optionsScrollPane = new JScrollPane();
    private final DefaultListModel optionsListModel = new DefaultListModel();

    private final JButton okButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final CommandField field;

    public OptionsDialog(CommandField field) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Set Options for " + field.getName());
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(400, 350));
        setModal(true);

        this.field = field;

        addOptionField.setToolTipText("Add an option by typing it here and clicking the \"Add\" button.");
//        idField.setTransferHandler(new TransferHandler() {
//            @Override
//            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
//        });

        addButton.setText("Add");
        addButton.setToolTipText("Add the given option to the list.");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { addOption(); }
        });


        optionsList.setDragEnabled(true);
        optionsList.setDropMode(DropMode.INSERT);
        optionsList.setToolTipText("Options for this field.");
        optionsList.setModel(optionsListModel);
        optionsScrollPane.setViewportView(optionsList);
        optionsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) { removeOptions(); }
            }
        });


        okButton.setText("OK");
        okButton.setToolTipText("Commit changes.");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { okAction(); }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { cancelAction(); }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addOptionLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(addOptionField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(addButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(optionsScrollPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(optionsLabel)))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton)))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(addOptionLabel)
                        .addComponent(addOptionField)
                        .addComponent(addButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(optionsLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(optionsScrollPane, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap())
        );
        //</editor-fold>
        getContentPane().setLayout(layout);

        for (String option : field.getOptions()) {
            optionsListModel.addElement(option);
        }
    }

    private void addOption() {
        String option = addOptionField.getText();
        if (option.isEmpty() || option.replaceAll("[ ]+", "").isEmpty()) return;

        if (!optionsListModel.contains(option)) optionsListModel.addElement(option);

        addOptionField.setText("");
    }

    private void removeOptions() {
        List<String> selected = optionsList.getSelectedValuesList();
        for (String id : selected) {
            optionsListModel.removeElement(id);
        }
    }

    private void okAction() {
        List<String> newOptionsList = new ArrayList<>();
        for (Object s : optionsListModel.toArray()) {
            newOptionsList.add((String) s);
        }

        field.getOptions().clear();
        field.getOptions().addAll(newOptionsList);

        setVisible(false);
    }

    private void cancelAction() {
        setVisible(false);
    }
}
