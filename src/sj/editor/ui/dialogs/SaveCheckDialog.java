/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import sj.editor.data.Ruleset;
import sj.editor.io.file.FileIO_V2;

/**
 * @author SafariJohn (original SRT)
 */
public class SaveCheckDialog extends JDialog {
    private final JLabel rulesetsLabel = new JLabel();
    private final JPanel rulesetPanel = new JPanel();
    private final JButton saveButton = new JButton();
    private final JButton exitButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final List<Ruleset> rulesets;
    private final List<JCheckBox> checkRulesets;

    private boolean cancel;

    public SaveCheckDialog(List<Ruleset> rulesets) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Save Changes?");
        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);

        this.rulesets = rulesets;
        checkRulesets = new ArrayList<>();
        cancel = true;

        rulesetsLabel.setText("Save the selected rulesets?");

        rulesetPanel.setLayout(new GridBagLayout());
        int gridy = 1;
        for (Ruleset ruleset : rulesets) {
            GridBagConstraints c2 = new GridBagConstraints();
            c2.gridy = gridy++;
            c2.anchor = GridBagConstraints.LINE_START;

            JCheckBox cBox = new JCheckBox(ruleset.getName());
            cBox.setSelected(true);

            rulesetPanel.add(cBox, c2);
            checkRulesets.add(cBox);
        }

        saveButton.setText("Save");
        saveButton.setToolTipText("Save selected rulesets and exit.");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { saveAction(); }
        });

        exitButton.setText("Don't Save");
        exitButton.setToolTipText("Exit without saving.");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { exitAction(); }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { setVisible(false); }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(rulesetsLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(rulesetPanel)
                            .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(saveButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(exitButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton)))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(rulesetsLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(rulesetPanel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(saveButton)
                        .addComponent(exitButton)
                        .addComponent(cancelButton))
                    .addContainerGap())
        );
        //</editor-fold>
        getContentPane().setLayout(layout);

        pack();
        setMinimumSize(getPreferredSize());
    }

    private void saveAction() {
        // Save selected rulesets
        for (JCheckBox cBox : checkRulesets) {
            for (Ruleset ruleset : rulesets) {
                if (cBox.isSelected() && cBox.getText().equals(ruleset.getName())) {
                    FileIO_V2.saveCSV(ruleset, ruleset.getSaveLocation());
                    break;
                }
            }
        }

        cancel = false;
        setVisible(false);
    }

    private void exitAction() {
        cancel = false;
        setVisible(false);
    }

    public boolean exitCanceled() {
        return cancel;
    }
}
