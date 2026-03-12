/**
 * SafariJohn's Rules Tool (SRT) Refurbished is an interface for editing rules.csv files.
 *
 * Copyright (C) 2026 Purple Nebula
 *
 * SafariJohn's Rules Tool (SRT) Refurbished is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SafariJohn's Rules Tool (SRT) Refurbished is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SafariJohn's Rules Tool (SRT) Refurbished.  If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor;

import com.formdev.flatlaf.FlatDarkLaf;
import sj.editor.data.rules.RuleFile;
import sj.editor.ui.dialogs.SaveCheckDialog;
import sj.editor.data.rules.RulesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.logging.*;
import java.util.prefs.Preferences;
import javax.swing.*;
import sj.editor.data.*;
import sj.editor.io.file.FileIO_V2;
import sj.editor.io.json.*;
import sj.editor.ui.*;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Refurbished)
 */
public class MainWindow extends JFrame {
    private static int NEXT_FILE_ID = 1;
    private static MainWindow INSTANCE;
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    private static final String version = "1.0.2";

    private final SRTMenuBar menuBar;
    private final EditorSplitPane editorPanes;

    private boolean treeInFocus = false;
    private boolean linkChange = false;
    private boolean summaryLock = false;
    private boolean skipGCDuringOpen = true;

    private static Settings settings = new Settings();

    private static HashMap<String, RuleFile> nameRepository = new HashMap<>();

    private MainWindow() {
        logger.log(Level.FINE, "Constructing");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // Handled by exit()
        setTitle("SRT Refurbished"); // formerly SafariJohn's Rules Tool
        setMinimumSize(new java.awt.Dimension(700, 500));
        setIconImage(new ImageIcon("icon.png").getImage());

        menuBar = new SRTMenuBar();
        editorPanes = new EditorSplitPane();

        // Set component fonts // NOI18N
        logger.log(Level.FINE, "Setting component fonts.");
        UIManager.put("Label.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("List.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("TextArea.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("TextField.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("Tree.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("TabbedPane.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("CheckBox.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("Button.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("RadioButton.font", new Font("Dialog", Font.PLAIN, 12));
        UIManager.put("Table.font", new Font("Dialog", Font.PLAIN, 12));

        logger.log(Level.FINER, "Adding window listener.");
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent evt) { editorWindowOpened(evt); }
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) { exit(); }
        });

        logger.log(Level.FINEST, "Setting menu bar - {0}", menuBar);
        setJMenuBar(menuBar);

        logger.log(Level.FINEST, "Adding editor panes - {0}", editorPanes);
        add(editorPanes);

        logger.log(Level.FINEST, "Packing window");
        pack();

        // Get window preferences
        logger.log(Level.FINE, "Loading window settings from system storage.");
        Preferences prefs = Preferences.userRoot().node("sj/editor/SRT/preferences");

        // Dimensions
        logger.log(Level.FINEST, "Getting MainWindow dimensions.");
        int width = prefs.getInt("mainWidth", 1000);
        int height = prefs.getInt("mainHeight", 700);
        if (settings.doResetSizeLocation()) {
            width = 1400;
            height = 850;
        }
        logger.log(Level.FINER, "Setting MainWindow width to {0}, height to {1}", new Integer[]{width, height});
        setSize(width, height);

        // XY location
        logger.log(Level.FINEST, "Getting MainWindow location.");
        int x = prefs.getInt("mainXLoc", 0);
        int y = prefs.getInt("mainYLoc", 0);
        if (settings.doResetSizeLocation()) {
            logger.log(Level.FINER, "Setting MainWindow location to screen center");
            setLocationRelativeTo(null); // v1.0.0 - Centers dialog if setting is enabled - Purple Nebula
//            setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().x, GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint().y);
        }
        else {
            logger.log(Level.FINER, "Setting MainWindow location to {0}, {1}", new Integer[]{x, y});
            setLocation(x, y);
        }

        logger.log(Level.FINEST, "Getting whether MainWindow maximized.");
        boolean maxH = prefs.getBoolean("mainMaxH", false);
        boolean maxV = prefs.getBoolean("mainMaxV", false);

        logger.log(Level.FINER, "Setting MainWindow maximized: {0}", maxH && maxV);
        if (maxH && maxV) setExtendedState(JFrame.MAXIMIZED_BOTH);
//        else if (maxH) setExtendedState(JFrame.MAXIMIZED_HORIZ);
//        else if (maxV) setExtendedState(JFrame.MAXIMIZED_VERT);
        else setExtendedState(JFrame.NORMAL);

        // Propagate Preferences object
        menuBar.getPreferences(prefs);
        editorPanes.getPreferences(prefs);

    }

    public static void main(String[] args) {
        Logger l0 = Logger.getLogger("");
        l0.setLevel(Level.INFO);
        l0.removeHandler(l0.getHandlers()[0]);
        try {
//            int limit = 50000 * 1024;
//            int count = 10;
            Handler h0 = new FileHandler("SRT-Refurbished.log", true);
            h0.setFormatter(new SimpleFormatter() {

                @Override
                public synchronized String format(LogRecord record) {
                    String name = record.getLoggerName();
                    String message = formatMessage(record);
                    return record.getLevel() + ": [" + name + "] " + message + "\n";
                }

            });

            l0.addHandler(h0);
        } catch (IOException | SecurityException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        logger.log(Level.INFO, "-------------------------------------------");
        logger.log(Level.INFO, "Initializing SafariJohn's Rules Tool (SRT) Refurbished v" + version);

        // Load settings
        File settingsFile = new File("settings.json");
        if (settingsFile.exists()) {
            logger.log(Level.INFO, "Loading settings.json.");

            // Deserialize
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Settings.class, new SettingsDeserializer());
            Gson gson = gsonBuilder.create();

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                settings = gson.fromJson(reader, Settings.class);
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        /* Set the look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code ">
        /* If Windows is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {

            if (settings.getLookAndFeel() != null) {
                UIManager.setLookAndFeel(settings.getLookAndFeel());
            }
            else {
                boolean found = false;
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equals(info.getName())) {
                        logger.log(Level.INFO, "Setting the window look and feel to {0}.", info.getClassName());
                        UIManager.setLookAndFeel(info.getClassName());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    logger.log(Level.INFO, "Setting the window look and feel to {0}, the system setting.", UIManager.getSystemLookAndFeelClassName());
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            return;
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (getInstance() == null) {
                    logger.log(Level.SEVERE, "MainWindow failed to construct!");
                    return;
                }

                logger.log(Level.FINEST, "Updating window component tree.");
                SwingUtilities.updateComponentTreeUI(getInstance());
                logger.log(Level.FINE, "Showing the main window.");
                getInstance().setVisible(true);
            }
        });
    }

    public static MainWindow getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new MainWindow();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to create UI! " + ex.toString(), ex);
                JOptionPane.showMessageDialog(null, "Failed to create UI! " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }
        return INSTANCE;
    }

    public static int getNextId() {
        return NEXT_FILE_ID++;
    }

    public String getVersion() {
        return version;
    }

    public boolean isTreeInFocus() {
        return treeInFocus;
    }

    public void setTreeInFocus(boolean treeInFocus) {
        this.treeInFocus = treeInFocus;
    }

    public boolean catchLinkChange() {
        boolean canCatch = linkChange;
        linkChange = false;
        return canCatch;
    }

    public void throwLinkChange() {
        this.linkChange = true;
    }

    public boolean isSummaryLock() {
        return summaryLock;
    }

    /**
     * Prevents all data tabs from reading commands/variables,
     * so they don't pick up partials.
     * @param locked
     */
    public void setSummaryLock(boolean locked) {
        this.summaryLock = locked;
    }

    public static Settings getSettings() {
        return settings;
    }

    private void editorWindowOpened(java.awt.event.WindowEvent evt) {
        menuBar.updateDialogUI();

        // Load settings
        File settingsFile = new File("settings.json");
        if (settingsFile.exists()) {
            logger.log(Level.INFO, "Loading settings.json.");

            // Deserialize
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Settings.class, new SettingsDeserializer());
            Gson gson = gsonBuilder.create();

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(settingsFile), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                settings = gson.fromJson(reader, Settings.class);
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Get Starsector folder
        // If null, ask user to point to it
        // Cancelling is ok, set to program folder
        if (settings.getModsLocation() == null || !settings.getModsLocation().exists()) {
            logger.log(Level.FINE, "Requesting mods directory.");
            final JFileChooser dialog = new JFileChooser();
            dialog.setDialogTitle("Select Starsector's \"mods\" Directory");
            dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            // Show save dialog to user
            int choice;
            File file;
            choice = dialog.showDialog(null, "Accept");
            file = dialog.getSelectedFile();

            if (choice == JFileChooser.APPROVE_OPTION) {
                logger.log(Level.FINE, "Setting mods folder to {0}.", file.getAbsolutePath());
                settings.setModsLocation(file);
            } else {
                logger.log(Level.INFO, "Setting mods folder to user's directory.");
                settings.setModsLocation(new File(System.getProperty("user.dir")));
            }
        }

        SpellcheckManager.enableSpellchecking(settings.getLanguage() != null);
        SpellcheckManager.updateSpellchecker();

        // If settings file doesn't exist (on the first run, for example), create it
        if (!settingsFile.exists()) {
            logger.log(Level.INFO, "Failed to load settings.json. Creating it.");

            // Configure GSON
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Settings.class, new SettingsSerializer());
            gsonBuilder.setPrettyPrinting();
            final Gson gson = gsonBuilder.create();

            final String json = gson.toJson(settings);

            try (
                // Create a file
                PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(settingsFile), StandardCharsets.UTF_8), true);
                ) {
    //            System.out.println(json);
                output.println(json);
            } catch (FileNotFoundException ex) {
                logger.log(Level.WARNING, ex.toString(), ex);
            }
        }

        // Get save folder and ensure it exists
        File saveLocation = settings.getSaveLocation();
        if (!saveLocation.exists()) {
            logger.log(Level.FINE, "Creating saves folder.");
            if (!saveLocation.mkdir()) {
                saveLocation = new File("rulesets");
                saveLocation.mkdir();
                settings.setSaveLocation(saveLocation);
            }
        }

        // Open previously active rulesets
        logger.log(Level.FINE, "Loading previous rulesets.");
        for (String csvLocation : settings.getPreviousRulesets()) {
            try {
                FileIO_V2.loadRulesCSV(new File(csvLocation));
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }
        }

//        if (settings.isShowVanilla()) {
//            RulesetsManager.showVanillaData(true);
//        } else {
//            RulesetsManager.showVanillaData(false);
//        }

        skipGCDuringOpen = false;
        refreshAllData();
    }

    public void exit() {

        // v1.0.0 - Update theme when changed in the settings - Purple Nebula
        if (UIManager.getLookAndFeel() != settings.getLookAndFeel()) {
            try {
                UIManager.setLookAndFeel(settings.getLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
        }

        // Save settings
        // Check if unsaved changes to active rulesets
        List<Ruleset> saveRulesets = new ArrayList<>();
        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            if (!ruleset.isSaved()) saveRulesets.add(ruleset);
        }

        // Show save check dialog
        SaveCheckDialog dialog = null;
        if (!saveRulesets.isEmpty()) {
            dialog = new SaveCheckDialog(saveRulesets);
            dialog.setVisible(true);
        }
        if (dialog != null && dialog.exitCanceled()) return;

        // Save which rulesets are active
        settings.getPreviousRulesets().clear();
        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            if (!new File(settings.getSaveLocation() + File.separator + ruleset.getName() + ".json").exists()) continue; // Never saved.
            if (!ruleset.isSaved()) revertRootToSavedState(ruleset);

            settings.getPreviousRulesets().add(ruleset.getSaveLocation().getPath());
        }

        // Save settings to settings.json
        File file = new File("settings.json");

        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Settings.class, new SettingsSerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.create();

        final String json = gson.toJson(settings);

        try (
            // Create a file
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
            ) {
//            System.out.println(json);
            output.println(json);
        } catch (FileNotFoundException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }


        // Save window preferences
        Preferences prefs = Preferences.userRoot().node("sj/editor/SRT/preferences");

        // Maximized state
        switch (getExtendedState()) {
            case JFrame.MAXIMIZED_BOTH:
                prefs.putBoolean("mainMaxH", true);
                prefs.putBoolean("mainMaxV", true);
                break;
            default:
                prefs.putBoolean("mainMaxH", false);
                prefs.putBoolean("mainMaxV", false);
        }

        setExtendedState(JFrame.NORMAL);
        setVisible(false);

        // XY location
        prefs.putInt("mainXLoc", getX());
        prefs.putInt("mainYLoc", getY());

        // Width
        prefs.putInt("mainWidth", getWidth());
        // Height
        prefs.putInt("mainHeight", getHeight());

        // Propagate Preferences object
        menuBar.setPreferences(prefs);
        editorPanes.setPreferences(prefs);

        System.exit(0);
    }

    private void revertRootToSavedState(Ruleset ruleset) {
        sj.editor.data.rules.RuleFile root = ruleset.getRootDirectory();
        RulesManager.setActiveRule(root);

        while (RulesManager.canRedo()) {
            RulesManager.redo();
            root = RulesManager.getActiveRule();

            if (root.getTimestamp() == ruleset.getSaveTime()) return;
        }

        while (RulesManager.canUndo()) {
            RulesManager.undo();
            root = RulesManager.getActiveRule();

            if (root.getTimestamp() == ruleset.getSaveTime()) return;
        }
    }

    public void refreshAllData() {
        logger.log(Level.FINEST, "Refreshing all data.");
        menuBar.refreshInterface();
        editorPanes.refreshInterface();

        SpellcheckManager.updateSpellchecker();

        if (!skipGCDuringOpen) System.gc();
    }
}
