/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.Desktop;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;
import sj.editor.io.file.FileIO_V2;
import sj.editor.ui.dialogs.*;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Refurbished)
 */
public class SRTMenuBar extends JMenuBar implements SRTInterface {
    private static final Logger logger = Logger.getLogger(SRTMenuBar.class.getName());

    private final JMenu fileMenu = new JMenu();
    private final JMenuItem newFileMenuItem = new JMenuItem();
    private final JMenuItem openFileMenuItem = new JMenuItem();
    private final JMenuItem saveAllFileMenuItem = new JMenuItem();
    private final JMenuItem saveFileMenuItem = new JMenuItem();
    private final JMenuItem saveToFileMenuItem = new JMenuItem();
    private final JMenuItem commitFileMenuItem = new JMenuItem();
    private final JMenuItem convertFileMenuItem = new JMenuItem();
    private final JMenuItem closeFileMenuItem = new JMenuItem();
    private final JMenuItem exitFileMenuItem = new JMenuItem();

    private final JMenu editMenu = new JMenu();
    private final JMenuItem undoEditMenuItem = new JMenuItem();
    private final JMenuItem redoEditMenuItem = new JMenuItem();
    private final JMenuItem undoTreeEditMenuItem = new JMenuItem();
    private final JMenuItem redoTreeEditMenuItem = new JMenuItem();
    private final JMenuItem findEditMenuItem = new JMenuItem();
    private final JMenuItem replaceEditMenuItem = new JMenuItem();

    private final JMenu viewMenu = new JMenu();
    private final JMenuItem rulesDocViewMenuItem = new JMenuItem();
    private final JMenuItem aboutViewMenuItem = new JMenuItem();
    private final JMenuItem optionsViewMenuItem = new JMenuItem();

    private final FindReplaceDialog findReplace;

    public SRTMenuBar() {
        logger.log(Level.FINE, "Constructing");
        findReplace = new FindReplaceDialog();


        logger.log(Level.FINER, "Creating File menu.");
        fileMenu.setText("File");

        //<editor-fold defaultstate="collapsed" desc=" File Menu Items ">
//        fileMenu.addSeparator(); // Unnecessary separator? - v1.0.0 - Purple Nebula

        newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        newFileMenuItem.setText("New");
        newFileMenuItem.setToolTipText("Create a new ruleset.");
        newFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { newRuleset(); }
        });
        fileMenu.add(newFileMenuItem);

        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openFileMenuItem.setText("Open");
        openFileMenuItem.setToolTipText("Open a ruleset.");
        openFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { open(); }
        });
        fileMenu.add(openFileMenuItem);

        commitFileMenuItem.setText("Commit");
        commitFileMenuItem.setToolTipText("");
        commitFileMenuItem.setEnabled(false);
        commitFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { commit(); }
        });
        fileMenu.add(commitFileMenuItem);

        saveFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveFileMenuItem.setText("Save");
        saveFileMenuItem.setToolTipText("Save the active ruleset.");
        saveFileMenuItem.setEnabled(false);
        saveFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { save(); }
        });
        fileMenu.add(saveFileMenuItem);

        saveToFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        saveToFileMenuItem.setText("Save To...");
        saveToFileMenuItem.setToolTipText("Save the active ruleset to the chosen location.");
        saveToFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { saveTo(); }
        });
        fileMenu.add(saveToFileMenuItem);

        saveAllFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        saveAllFileMenuItem.setText("Save All");
        saveAllFileMenuItem.setToolTipText("Save all opened rulesets.");
        saveAllFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { saveAll(); }
        });
        fileMenu.add(saveAllFileMenuItem);

        closeFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        closeFileMenuItem.setText("Close");
        closeFileMenuItem.setToolTipText("Close the active ruleset.");
        closeFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { close(); }
        });
        fileMenu.add(closeFileMenuItem);

        fileMenu.addSeparator();

        convertFileMenuItem.setText("Convert");
        convertFileMenuItem.setToolTipText("Convert a pre-2.0 ruleset to SRT 2.0's save format.");
        convertFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { convert(); }
        });
        fileMenu.add(convertFileMenuItem);

        fileMenu.addSeparator();

        exitFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        exitFileMenuItem.setText("Exit");
        exitFileMenuItem.setToolTipText("Close the program.");
        exitFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { exit(); }
        });
        fileMenu.add(exitFileMenuItem);
        //</editor-fold>

        add(fileMenu);


        logger.log(Level.FINER, "Creating Edit menu.");
        editMenu.setText("Edit");

        //<editor-fold defaultstate="collapsed" desc="Edit Menu Items">
        undoEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        undoEditMenuItem.setText("Undo");
        undoEditMenuItem.setToolTipText("Undo changes to this rule.");
        undoEditMenuItem.setEnabled(false);
        undoEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { undo(MainWindow.getInstance().isTreeInFocus()); }
        });
        editMenu.add(undoEditMenuItem);

        redoEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        redoEditMenuItem.setText("Redo");
        redoEditMenuItem.setToolTipText("Redo changes to this rule.");
        redoEditMenuItem.setEnabled(false);
        redoEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { redo(MainWindow.getInstance().isTreeInFocus()); }
        });
        editMenu.add(redoEditMenuItem);

        undoTreeEditMenuItem.setText("Undo (Rule Order)");
        undoTreeEditMenuItem.setToolTipText("Undo rule addition, deletion, copying, and reordering.");
        undoTreeEditMenuItem.setEnabled(false);
        undoTreeEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { undo(true); }
        });
        editMenu.add(undoTreeEditMenuItem);

        redoTreeEditMenuItem.setText("Redo (Rule Order)");
        redoTreeEditMenuItem.setToolTipText("Redo rule addition, deletion, copying, and reordering.");
        redoTreeEditMenuItem.setEnabled(false);
        redoTreeEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { redo(true); }
        });
        editMenu.add(redoTreeEditMenuItem);

        editMenu.addSeparator(); // v1.0.0 - Purple Nebula

        findEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        findEditMenuItem.setText("Find");
//        findEditMenuItem.setToolTipText("");
        findEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { findReplace(FindReplaceDialog.FIND_INDEX); }
        });
        editMenu.add(findEditMenuItem);

        replaceEditMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        replaceEditMenuItem.setText("Replace");
//        replaceEditMenuItem.setToolTipText("");
        replaceEditMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { findReplace(FindReplaceDialog.REPLACE_INDEX); }
        });
        editMenu.add(replaceEditMenuItem);
        //</editor-fold>

        add(editMenu);


        logger.log(Level.FINER, "Creating View menu.");
        viewMenu.setText("View");

        //<editor-fold defaultstate="collapsed" desc="View Menu Items">
        optionsViewMenuItem.setText("Options");
        optionsViewMenuItem.setToolTipText("Change your settings.");
        optionsViewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { openOptionsScreen(); }
        });
        viewMenu.add(optionsViewMenuItem);

        viewMenu.addSeparator();

        rulesDocViewMenuItem.setText("Scripting Documentation");
        rulesDocViewMenuItem.setToolTipText("How-to documentation for rules.csv.");
        rulesDocViewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { openRulesDoc(); }
        });
        viewMenu.add(rulesDocViewMenuItem);

        aboutViewMenuItem.setText("About");
        aboutViewMenuItem.setToolTipText("Changelogs and version information.");
        aboutViewMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { openAboutScreen(); }
        });
        viewMenu.add(aboutViewMenuItem);
    //</editor-fold>

        add(viewMenu);
    }

    private void newRuleset() {
        String rulesetTitle = RulesetNameDialog.showInputDialog();
        // If null, then the user canceled the load.
        if (rulesetTitle == null) return;

        Ruleset newRuleset = new Ruleset(rulesetTitle);

        RulesetsManager.getRulesets().add(newRuleset);
        MainWindow.getInstance().refreshAllData();
    }

    private void open() {
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle("Open rules.csv");
        dialog.setFileFilter(new FileNameExtensionFilter("Comma Seperated Values (.csv)", "csv"));
        dialog.setCurrentDirectory(MainWindow.getInstance().getSettings().getModsLocation());

        int returnValue = dialog.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = dialog.getSelectedFile();

                if (file.getPath().contains(MainWindow.getInstance().getSettings().getSaveLocation().getPath())) {
                    JOptionPane.showMessageDialog(null, "Opening Safe Mode CSVs is forbidden.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean opened = FileIO_V2.loadRulesCSV(file);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }

            MainWindow.getInstance().refreshAllData();
        }
    }

    private void commit() {
        Ruleset ruleset = RulesetsManager.getRuleset(RulesManager.getActiveRule().getRulesetId());
        // Check for ruleset name overlap
        if (RulesetsManager.checkRulesetNameOverlap(ruleset)) {
            int result = JOptionPane.showConfirmDialog(
                        null,
                        "There is more than one ruleset with the name " + ruleset.getName() + ".\n"
                            + "Are you sure you wish to commit?",
                        "Conflict Detected",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.YES_OPTION) return; // Canceled
        }


        File safeCSV;
        if (!ruleset.hasOriginalName()) {
            safeCSV = new File ("ruleset" + File.separator + ruleset.getOriginalName() + ".csv");
            if (safeCSV.exists()) safeCSV.delete();
        }

        safeCSV = new File ("ruleset" + File.separator + ruleset.getName() + ".csv");
        if (safeCSV.exists()) safeCSV.delete();

        if (ruleset.getSaveLocation().getPath().isEmpty()) saveTo(ruleset);
        if (ruleset.getSaveLocation().getPath().isEmpty()) return; // The user canceled in saveTo() or an error occurred.
        FileIO_V2.saveCSV(ruleset, ruleset.getSaveLocation());
    }

    private void save() {
        save(RulesetsManager.getRuleset(RulesManager.getActiveRule().getRulesetId()));
    }

    private void save(Ruleset ruleset) {
        // Check for ruleset name overlap
        if (RulesetsManager.checkRulesetNameOverlap(ruleset)) {
            int result = JOptionPane.showConfirmDialog(
                        null,
                        "There is more than one ruleset with the name " + ruleset.getName() + ".\n"
                            + "Are you sure you wish to save?",
                        "Conflict Detected",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.YES_OPTION) return; // Canceled
        }

        // If no save location stored, call SaveTo() to set one.
        boolean setLocation = false;
        if (ruleset.getSaveLocation().getPath().isEmpty()) {
            saveTo(ruleset);
            setLocation = true;
        }

        if (ruleset.getSaveLocation() == null || ruleset.getSaveLocation().getPath().isEmpty()) {
            return; // The user canceled in saveTo() or an error occurred.
        }

        // Save
        if (MainWindow.getInstance().getSettings().isSafeMode()) {
            if (setLocation) FileIO_V2.saveCSV(ruleset, ruleset.getSaveLocation());
            FileIO_V2.saveCSV(ruleset, new File (MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + ruleset.getName() + ".csv"));

            if (!ruleset.hasOriginalName()) {
                File safeCSV = new File ("ruleset" + File.separator + ruleset.getOriginalName() + ".csv");
                if (safeCSV.exists()) safeCSV.delete();
            }
        } else {
            FileIO_V2.saveCSV(ruleset, ruleset.getSaveLocation());
        }

        MainWindow.getInstance().refreshAllData();
    }

    /**
     * save(Ruleset) calls saveTo(Ruleset) which handles setting the save location.
     */
    private void saveTo() {
        Ruleset ruleset = RulesetsManager.getRuleset(RulesManager.getActiveRule().getRulesetId());
        ruleset.setSaveLocation(null);
        save(ruleset);
    }

    /**
     * This method sets the saveLocation variable of the Ruleset passed to it,
     * and assumes it returns to a method that handles the actual save.
     * @param ruleset
     */
    private void saveTo(Ruleset ruleset) {
        // Ask where to save
        final JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle("Save " + ruleset.getName());
        dialog.setFileFilter(new FileNameExtensionFilter("Comma Seperated Values (.csv)", "csv"));
        dialog.setCurrentDirectory(MainWindow.getInstance().getSettings().getModsLocation());

        // Show save dialog to user
        int saveChoice;
        int overwriteChoice = JOptionPane.YES_OPTION;
        File file;
        do {
            saveChoice = dialog.showSaveDialog(null);
            file = dialog.getSelectedFile();

            if (file.getPath().contains(MainWindow.getInstance().getSettings().getSaveLocation().getPath())) {
                JOptionPane.showMessageDialog(null, "Saving to SRT/rulesets is forbidden.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // If the file exists, ask if overwrite.
            if (saveChoice == JFileChooser.APPROVE_OPTION) {
                if (file.exists()) {
                    overwriteChoice = JOptionPane.showConfirmDialog(null, "Overwrite " + file.getName() + "?");
                }
            } else break;

        } while (overwriteChoice == JOptionPane.NO_OPTION);

        if (overwriteChoice != JOptionPane.YES_OPTION) saveChoice = JFileChooser.CANCEL_OPTION;

        // Set the save location and return to save(ruleset).
        if (saveChoice == JFileChooser.APPROVE_OPTION) {
            if (!file.getPath().endsWith(".csv")) file = new File(file + ".csv");

            ruleset.setSaveLocation(file);
        }
    }

    private void saveAll() {
        // Save all active rulesets
        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            if (ruleset.isSaved()) continue;

            save(ruleset);
        }
    }

    private void convert() {
        // Ask load location
        JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle("Open Pre-2.0 Ruleset Folder");
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setCurrentDirectory(new File("rules"));

        File loadFile;
        int returnValue = dialog.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
             loadFile = dialog.getSelectedFile();
        } else {
            return;
        }

        // Ask where to save
        dialog = new JFileChooser();
        dialog.setDialogTitle("Save " + loadFile.getName());
        dialog.setFileFilter(new FileNameExtensionFilter("Comma Seperated Values (.csv)", "csv"));
        dialog.setCurrentDirectory(MainWindow.getInstance().getSettings().getModsLocation());

        // Show save dialog to user
        int saveChoice;
        int overwriteChoice = JOptionPane.YES_OPTION;
        File saveFile;
        do {
            saveChoice = dialog.showSaveDialog(null);
            saveFile = dialog.getSelectedFile();

            if (saveFile.getPath().contains(MainWindow.getInstance().getSettings().getSaveLocation().getPath())) {
                JOptionPane.showMessageDialog(null, "Saving to SRT/rulesets is forbidden.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // If the file exists, ask if overwrite.
            if (saveChoice == JFileChooser.APPROVE_OPTION) {
                if (saveFile.exists()) {
                    overwriteChoice = JOptionPane.showConfirmDialog(null, "Overwrite " + saveFile.getName() + "?");
                }
            } else break;

        } while (overwriteChoice == JOptionPane.NO_OPTION);

        if (overwriteChoice != JOptionPane.YES_OPTION) saveChoice = JFileChooser.CANCEL_OPTION;

        // Set the save location and return to save(ruleset).
        if (saveChoice == JFileChooser.APPROVE_OPTION) {
            if (!saveFile.getPath().endsWith(".csv")) saveFile = new File(saveFile + ".csv");
        } else {
            return;
        }

        // Convert
        boolean converted = false;
        try {
            converted = FileIO_V2.convertV1Ruleset(loadFile, saveFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
        }

        // Load 2.0 ruleset
        if (converted) {
            try {
                boolean opened = FileIO_V2.loadRulesCSV(saveFile);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }

            MainWindow.getInstance().refreshAllData();
        }
    }

    private void close() {
        Ruleset ruleset = RulesetsManager.getRuleset(RulesManager.getActiveRule().getRulesetId());

        String text = "Do you want to save your changes to " + ruleset.getName() + "?";
        String title = "Save " + ruleset.getName() + "?";
        int result;

        if (!ruleset.isSaved()) result = JOptionPane.showConfirmDialog(null, text, title, JOptionPane.YES_NO_CANCEL_OPTION);
        else result = JOptionPane.NO_OPTION;

        if (result == JOptionPane.CANCEL_OPTION) return;

        if (result == JOptionPane.YES_OPTION) {
            FileIO_V2.saveCSV(ruleset, MainWindow.getInstance().getSettings().getSaveLocation());
        }

        RulesetsManager.getRulesets().remove(ruleset);

        RulesetsManager.updateIdOverlaps();

        RulesManager.setActiveRule(new RuleFile());
        MainWindow.getInstance().refreshAllData();
    }

    private void exit() {
        MainWindow.getInstance().exit();
    }

    private void undo(boolean treeInFocus) {
        // If tree is in focus, undo ruleset
        if (treeInFocus) {
            RulesetsManager.undo();
        } else {
            // Otherwise, undo rule
            RulesManager.undo();
        }

        findReplace.refreshInterface(findReplace.isVisible());
    }

    private void redo(boolean treeInFocus) {
        // If tree is in focus, redo ruleset
        if (treeInFocus) {
            RulesetsManager.redo();
        } else {
            // Otherwise, redo rule
            RulesManager.redo();
        }

        findReplace.refreshInterface(findReplace.isVisible());
    }

    private void findReplace(int tabIndex) {
        findReplace.setVisible(true, tabIndex);
    }

    private void openRulesDoc() {
        File file = new File("StarsectorRuleScripting.pdf");

        try {
            if (!file.exists()) throw new IOException();
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
            String message = "Unable to open the scripting documentation. Find it online at \"http://fractalsoftworks.com/forum/index.php?topic=8355.0\".";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openOptionsScreen() {
        new OptionsDialog().setVisible(true);
    }

    private void openAboutScreen() {
        new AboutDialog().setVisible(true);
    }


    public void updateDialogUI() {
        SwingUtilities.updateComponentTreeUI(findReplace);
    }

    @Override
    public void refreshInterface() {

        RuleFile activeRule = RulesManager.getActiveRule();
        Ruleset activeRuleset = null;
        if (activeRule.getRulesetId() >= 0) {
            activeRuleset = RulesetsManager.getRuleset(activeRule.getRulesetId());
        }

        if (RulesetsManager.getRulesets().isEmpty()) {
            saveFileMenuItem.setEnabled(false);
            saveToFileMenuItem.setEnabled(false);
            saveAllFileMenuItem.setEnabled(false);
            commitFileMenuItem.setEnabled(false);
        } else {
//            saveFileMenuItem.setEnabled(true);
//            saveToFileMenuItem.setEnabled(true);

            // Check if unsaved changes to active rulesets
            List<Ruleset> saveRulesets = new ArrayList<>();
            for (Ruleset data : RulesetsManager.getRulesets()) {
                if (!data.isSaved()) saveRulesets.add(data);
            }

            if (saveRulesets.isEmpty()) {
                saveAllFileMenuItem.setEnabled(false);
            }
            else saveAllFileMenuItem.setEnabled(true);

            if (MainWindow.getInstance().getSettings().isSafeMode()) {
                saveAllFileMenuItem.setEnabled(false);
                commitFileMenuItem.setEnabled(true);
            } else {
                commitFileMenuItem.setEnabled(false);
            }
        }

        if (activeRuleset == null) {
            saveFileMenuItem.setText("Save");
            saveFileMenuItem.setEnabled(false);
            saveToFileMenuItem.setText("Save To...");
            saveToFileMenuItem.setEnabled(false);
            commitFileMenuItem.setText("Commit");
            commitFileMenuItem.setEnabled(false);
        } else {
            saveFileMenuItem.setText("Save " + activeRuleset.getName());
            saveFileMenuItem.setEnabled(true);
            saveToFileMenuItem.setText("Save " + activeRuleset.getName() + " To...");
            saveToFileMenuItem.setEnabled(true);

            if (MainWindow.getInstance().getSettings().isSafeMode()) {
                commitFileMenuItem.setText("Commit " + activeRuleset.getName());
                commitFileMenuItem.setEnabled(true);
            } else {
                commitFileMenuItem.setText("Commit");
                commitFileMenuItem.setEnabled(false);
            }
        }

        if (activeRuleset == null) {
            closeFileMenuItem.setText("Close");
            closeFileMenuItem.setEnabled(false);
        } else {
            closeFileMenuItem.setText("Close " + activeRuleset.getName());
            closeFileMenuItem.setEnabled(true);
        }

        if (RulesManager.canUndo() || RulesetsManager.canUndo()) undoEditMenuItem.setEnabled(true);
        else undoEditMenuItem.setEnabled(false);

        if (RulesManager.canRedo() || RulesetsManager.canRedo()) redoEditMenuItem.setEnabled(true);
        else redoEditMenuItem.setEnabled(false);

        if (RulesetsManager.canUndo()) undoTreeEditMenuItem.setEnabled(true);
        else undoTreeEditMenuItem.setEnabled(false);

        if (RulesetsManager.canRedo()) redoTreeEditMenuItem.setEnabled(true);
        else redoTreeEditMenuItem.setEnabled(false);
    }

    @Override
    public void getPreferences(Preferences prefs) {}

    @Override
    public void setPreferences(Preferences prefs) {}
}
