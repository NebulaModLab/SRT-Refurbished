/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.event.*;

/**
 * Description: Used to add or edit a condition/script function.
 * Author: SafariJohn
 */
public class FunctionDialog extends JDialog {
    private final JLabel nameLabel = new JLabel("Name:");
    private final JTextField nameField = new JTextField();
    private final JLabel inputsLabel = new JLabel("Inputs (Optional):");
//    private final List<FieldPanel> fields = new ArrayList<>(); // Just display 7 for now
    private final FieldPanel panel1 = new FieldPanel(1);
    private final FieldPanel panel2 = new FieldPanel(2);
    private final FieldPanel panel3 = new FieldPanel(3);
    private final FieldPanel panel4 = new FieldPanel(4);
    private final FieldPanel panel5 = new FieldPanel(5);
    private final FieldPanel panel6 = new FieldPanel(6);
    private final FieldPanel panel7 = new FieldPanel(7);

    private final JButton confirmButton = new JButton("Confirm");
    private final JButton cancelButton = new JButton("Cancel");

    private String result;
    private boolean cancel;

    public FunctionDialog() {
        this("");
    }

    public FunctionDialog(String function) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        String name = (function.isEmpty()) ? "Add" : "Edit";
        setTitle(name + " Command");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(400, 100));
        setResizable(false);
        setModal(true);

        result = "";
        cancel = true;

        // Split the function string and assign segments to textfields
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(function.split(" ")));

        while (list.size() < 8) {
            list.add(list.size(), "");
        }

        nameField.setText(list.get(0));
        panel1.setText(list.get(1));
        panel2.setText(list.get(2));
        panel3.setText(list.get(3));
        panel4.setText(list.get(4));
        panel5.setText(list.get(5));
        panel6.setText(list.get(6));
        panel7.setText(list.get(7));

        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == ' ') e.consume();
            }
        });

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { confirm(evt); }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { cancel(evt); }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//                    .addComponent(inputsLabel)
//                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                    .addComponent(panel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addComponent(confirmButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                .addComponent(inputsLabel)
//                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//                .addComponent(panel7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(confirmButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );
        getContentPane().setLayout(layout);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

    }

    public String getResult() {
        return result;
    }

    private void confirm(ActionEvent evt) {
        cancel = false;
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        dispose();
    }

    private void cancel(ActionEvent evt) {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        dispose();
    }

    private void formWindowClosing(WindowEvent evt) {
        if (cancel) return;

        // Must reference a function
        if (nameField.getText().isEmpty()) return;

        // Concatenate results
        result = nameField.getText();

        if (!panel1.getText().isEmpty()) result += " " + panel1.getText();
        if (!panel2.getText().isEmpty()) result += " " + panel2.getText();
        if (!panel3.getText().isEmpty()) result += " " + panel3.getText();
        if (!panel4.getText().isEmpty()) result += " " + panel4.getText();
        if (!panel5.getText().isEmpty()) result += " " + panel5.getText();
        if (!panel6.getText().isEmpty()) result += " " + panel6.getText();
        if (!panel7.getText().isEmpty()) result += " " + panel7.getText();
    }

    private class FieldPanel extends JPanel {
        private final JLabel descriptorLabel = new JLabel();
        private final JTextField descriptorField = new JTextField();

        public FieldPanel(int index) {
            descriptorLabel.setText("Input " + index + ":");

            GroupLayout layout = new GroupLayout(this);
            layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(descriptorLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(descriptorField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(descriptorLabel)
                        .addComponent(descriptorField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE))
                    .addContainerGap())
            );
            setLayout(layout);
        }

        public String getText() {
            return descriptorField.getText();
        }

        public void setText(String input) {
            descriptorField.setText(input);
        }
    }
}
