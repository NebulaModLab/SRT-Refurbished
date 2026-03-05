/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import sj.editor.MainWindow;
import sj.editor.data.SearchManager;
import sj.editor.data.Settings;
import sj.editor.ui.SpellcheckManager;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Revised)
 */
public class OptionsDialog extends JDialog {
    private final JLabel saveLocLabel = new JLabel();
    private final JTextField saveLocField = new JTextField();
    private final JButton saveLocButton = new JButton();

    private final JLabel modsLocLabel = new JLabel();
    private final JTextField modsLocField = new JTextField();
    private final JButton modsLocButton = new JButton();

    private final JCheckBox safeModeCheckBox = new JCheckBox();

    private final JCheckBox enableSpellcheckBox = new JCheckBox();

    private final JLabel languageLabel = new JLabel();
    private final DefaultComboBoxModel languageComboBoxModel = new DefaultComboBoxModel();
    private final JComboBox languageComboBox = new JComboBox(languageComboBoxModel);

    private final JLabel lookFeelLabel = new JLabel();
    private final DefaultComboBoxModel lookFeelComboBoxModel = new DefaultComboBoxModel();
    private final JComboBox lookFeelComboBox = new JComboBox(lookFeelComboBoxModel);

    private final JButton okButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JButton applyButton = new JButton();

    private final Settings settings;

    public OptionsDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300)); // 250
        setLocationRelativeTo(null); // v3.0.0 - Centers dialog when opened - Purple Nebula
        setTitle("Options");
        setAlwaysOnTop(true);
        setResizable(false);
        setModal(true);

        // Window listener to reread available spellchecking languages?

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent evt) { readAvailableLanguages(evt); }
            @Override
            public void windowClosing(WindowEvent evt) {}
        });

        settings = MainWindow.getInstance().getSettings();

//        saveLocLabel.setText("Ruleset Data Path:");
//        saveLocField.setText(settings.getSaveLocation().getPath());
//        saveLocButton.setText("...");
//        saveLocButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) { selectSaveLoc(); }
//        });

        modsLocLabel.setText("Mod Folder Path:");
        modsLocField.setText(settings.getModsLocation().getPath());
        modsLocField.setEditable(false);
        modsLocButton.setText("...");
        modsLocButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { selectModsLoc(); }
        });

        safeModeCheckBox.setText("Safe Mode");
        safeModeCheckBox.setToolTipText("Safe mode requires changes to be \"committed\" to affect the original CSV.");
        safeModeCheckBox.setSelected(settings.isSafeMode());

        enableSpellcheckBox.setText("Enable Spellchecking");
        enableSpellcheckBox.setSelected(settings.isSpellchecking());
        enableSpellcheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enableSpellcheckBox.isSelected()) {
                    languageComboBox.setEnabled(true);
                } else {
                    languageComboBox.setEnabled(false);
                }
            }
        });

        languageLabel.setText("Spellchecking Language:");
        languageComboBox.setEditable(false);

        for (Locale l : settings.getAvailableLanguages()) {
            languageComboBoxModel.addElement(l);
        }
        if (settings.isSpellchecking()) languageComboBox.setSelectedItem(settings.getLanguage());
        else languageComboBox.setEnabled(false);

        languageComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                languageComboBox.setSelectedItem(settings.getLanguage());
            }
        });

        // v3.0.0 - Purple Nebula
        lookFeelLabel.setText("Theme:");
        lookFeelComboBox.setEditable(false);

        lookFeelComboBoxModel.addElement(settings.getLookAndFeel().getName());
        for (LookAndFeel lookAndFeel : settings.getLookAndFeels()) {
            if (!lookAndFeel.getName().equals(lookFeelComboBox.getItemAt(0))) lookFeelComboBoxModel.addElement(lookAndFeel.getName());
        }
        lookFeelComboBox.setSelectedItem(settings.getLookAndFeel());
        lookFeelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lookFeelComboBox.setSelectedItem(settings.getLookAndFeel());
            }
        });
        // ======================

        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { ok(); }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { cancel(); }
        });

        applyButton.setText("Apply");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) { apply(); }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(modsLocLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(modsLocField)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(modsLocButton))
                        .addComponent(safeModeCheckBox)
                        .addComponent(enableSpellcheckBox)
                        .addComponent(languageLabel)
                        .addComponent(languageComboBox)
                        .addComponent(lookFeelLabel)
                        .addComponent(lookFeelComboBox)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton)
//                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                            .addComponent(applyButton)
                        ))
                    .addContainerGap())
        );

        int modsLocFieldHeight = modsLocField.getPreferredSize().height;
        int languageComboBoxHeight = languageComboBox.getPreferredSize().height;
        int lookFeelComboBoxHeight = lookFeelComboBox.getPreferredSize().height;

        layout.linkSize(SwingConstants.VERTICAL, modsLocField, modsLocButton, languageComboBox);

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(modsLocLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(modsLocField, modsLocFieldHeight, modsLocFieldHeight, modsLocFieldHeight)
                        .addComponent(modsLocButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(safeModeCheckBox)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(enableSpellcheckBox)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(languageLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(languageComboBox, languageComboBoxHeight, languageComboBoxHeight, languageComboBoxHeight)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lookFeelLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lookFeelComboBox, lookFeelComboBoxHeight, lookFeelComboBoxHeight, lookFeelComboBoxHeight)
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(okButton)
                        .addComponent(cancelButton)
//                        .addComponent(applyButton)
                    )
                    .addContainerGap())
        );
        //</editor-fold>
        getContentPane().setLayout(layout);
    }

    private void readAvailableLanguages(WindowEvent evt) {
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(new File(SearchManager.DICTIONARY_PATH).listFiles()));
        Collections.sort(files);

        settings.getAvailableLanguages().clear();
        for (File file : files) {
            String name = file.getName();
            if (name.matches("dictionary_..\\.ortho")) {
                name = name.replaceFirst("dictionary_", "");
                name = name.replaceFirst("\\.ortho", "");
                if (name.length() == 2) settings.getAvailableLanguages().add(new Locale(name));
            }
        }
    }

    private void selectSaveLoc() {
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle("Select Ruleset Data Folder");
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = dialog.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = dialog.getSelectedFile();
            saveLocField.setText(file.getPath());
        }
    }

    private void selectModsLoc() {
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle("Select Mods Folder");
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = dialog.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = dialog.getSelectedFile();
            modsLocField.setText(file.getPath());
        }
    }

    private void ok() {
        apply();
        dispose();
    }

    private void cancel() {
        dispose();
    }

    private void apply() {
//        settings.setSaveLocation(new File(saveLocField.getText()));
        settings.setModsLocation(new File(modsLocField.getText()));
        settings.setSafeMode(safeModeCheckBox.isSelected());

        if (enableSpellcheckBox.isSelected()) {
            settings.setLanguage((Locale) languageComboBox.getSelectedItem());
            SpellcheckManager.updateSpellchecker();
        } else {
            settings.setLanguage(null);
        }
        SpellcheckManager.enableSpellchecking(enableSpellcheckBox.isSelected());

        // v3.0.0 - Purple Nebula
        if (UIManager.getLookAndFeel() != settings.getLookAndFeel()) {
            for (LookAndFeel lookAndFeel : settings.getLookAndFeels()) {
                if (!lookAndFeel.getName().equals(lookFeelComboBox.getSelectedItem())) continue;
                JOptionPane.showMessageDialog(this, "Restart SRT for theme changes to take effect!", "Attention", JOptionPane.INFORMATION_MESSAGE);
                settings.setLookAndFeel(lookAndFeel);
            }
        }
        // ======================

        MainWindow.getInstance().refreshAllData();
    }
}
