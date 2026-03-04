/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import sj.editor.MainWindow;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;


/**
 * Author: SafariJohn
 */
public class RulesetNameDialog {

    public static String showInputDialog() {
        return showInputDialog("");
    }

    public static String showInputDialog(String text) {
        InputPanel panel = new InputPanel();

        panel.setText(text);

        while (true) {
            int result = JOptionPane.showConfirmDialog(
                        null,
                        panel,
                        "Name Ruleset",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
            );

            // If canceled or closed
            if (result != JOptionPane.OK_OPTION) return null;

            String name = panel.getRulesetName().trim();

            if (result == JOptionPane.OK_OPTION) {
                // Can't return an empty string.
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "A ruleset must have a name.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Check if user wishes to overwrite.
                if (panel.nameExists()) {
                    String oText = "Do you wish to overwrite " + name + "?";
                    File safeCSV = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + panel.getRulesetName() + ".csv");

                    if (safeCSV.exists()) oText += "\nThis will delete your pending Safe Mode changes.";

                    int overwrite = JOptionPane.showConfirmDialog(
                                null,
                                oText,
                                "Overwrite Ruleset?",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE
                    );
                    if (overwrite == JOptionPane.YES_OPTION)  {
                        if (safeCSV.exists()) safeCSV.delete();
                        return name;
                    }
                    else if (overwrite == JOptionPane.NO_OPTION) continue;
                    else return null; // Canceled
                }

                // If there's no other objections, we can return the name.
                return name;
            }
        }
    }

    private static class InputPanel extends JPanel {
        private final JLabel nameLabel = new JLabel("Name:");
        private final JTextField nameField = new JTextField();
        private final JLabel existsLabel = new JLabel();

        private final Icon icon = UIManager.getIcon("OptionPane.warningIcon");

        private boolean exists = false;

        private InputPanel() {
            nameField.getDocument().addDocumentListener(new DocumentListener() {
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
                    // Check against loaded rulesets.
                    for (Ruleset ruleset : RulesetsManager.getRulesets()) {
                        if (ruleset.getName().equals(nameField.getText()))  {
                            exists = true;
                            existsLabel.setIcon(icon);
                            existsLabel.setToolTipText("A ruleset with this name is already loaded!");
                            return;
                        }
                    }

                    // If nothing loaded, check against saved rulesets.
                    File folder = MainWindow.getInstance().getSettings().getSaveLocation();
                    for (File file : folder.listFiles()) {
                        if (file.getName().equals(nameField.getText() + ".json")) {
                            exists = true;
                            existsLabel.setIcon(icon);
                            existsLabel.setToolTipText("A ruleset with this name exists!");
                            return;
                        }
                    }

                    // If nothing has been found
                    exists = false;
                    existsLabel.setIcon(null);
                    existsLabel.setToolTipText(null);
                }
                //</editor-fold>
            });

            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateGaps(true);
            layout.setAutoCreateContainerGaps(true);

            //<editor-fold defaultstate="collapsed" desc="Layout Code">
            layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(nameLabel))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(nameField)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(existsLabel, icon.getIconWidth(), icon.getIconWidth(), icon.getIconWidth())))
            );

            int prefHeight = nameField.getPreferredSize().height;

            layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(nameLabel)
                        .addComponent(nameField, prefHeight, prefHeight, prefHeight)
                        .addComponent(existsLabel, icon.getIconHeight(), icon.getIconHeight(), icon.getIconHeight()))
            );
            //</editor-fold>
            setLayout(layout);
        }

        private String getRulesetName() {
            return nameField.getText();
        }

        public void setText(String text) {
            if (text == null) text = "";
            nameField.setText(text);
        }

        public boolean nameExists() {
            return exists;
        }
    }
}
