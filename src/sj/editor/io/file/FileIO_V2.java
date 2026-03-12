/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.file;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.RulesetsManager.DataType;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.CommandField;
import sj.editor.data.rules.*;
import sj.editor.data.vanilla.VanillaRuleset;
import sj.editor.io.json.*;
import sj.editor.io.json.CommandSerializer.CommandFieldSerializer;
import sj.editor.ui.dialogs.RulesetNameDialog;

/**
 * @author SafariJohn (original SRT)
 */
public class FileIO_V2 {
    private static final Logger logger = Logger.getLogger(FileIO_V2.class.getName());

    private static final String BLANK_LINE = ",,,,,,\n";

    //<editor-fold defaultstate="collapsed" desc="Load CSV Methods">
    public static boolean loadRulesCSV(File rulesCSVFile) throws IOException {
        logger.log(Level.INFO, "Loading {0}", rulesCSVFile.getAbsolutePath());

        if (!rulesCSVFile.exists()) {
            JOptionPane.showMessageDialog(null, rulesCSVFile.getName() + " does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        List<RuleFile> rules = new ArrayList<>();

        try (CsvBeanReader csvReader = new CsvBeanReader(new InputStreamReader(new FileInputStream(rulesCSVFile), StandardCharsets.UTF_8), CsvPreference.STANDARD_PREFERENCE)) {

            // the header elements are used to map the values to the bean (names must match)
            csvReader.getHeader(true);

            final String[] header = new String[] { "id", "trigger", "conditions", "script",
                "text", "options", "notes" };

            final CellProcessor[] processors = new CellProcessor[] {
                new Optional(), // id
                new Optional(), // trigger
                new Optional(), // conditions
                new Optional(), // script
                new Optional(), // text
                new Optional(), // options
                new Optional(), // notes
            };

            RuleBean rule = csvReader.read(RuleBean.class, header, processors);
            while (rule != null) {
                rule.setId(rule.getId().trim());
//                rule.setScript(unEscapeNewlines(rule.getScript()));

                if (rule.getId().startsWith("#RULESET_NAME ") || rule.getId().startsWith("#END ")) {
                    rules.add(new RuleFile(rule));
                }
                else if (rule.isDirectory()) {
                    String dirId = rule.getId();
                    dirId = dirId.replaceFirst("#", "");
                    rule.setId(dirId.trim());

                    rules.add(new DirectoryFile(rule));
                }
                else rules.add(new RuleFile(rule));

                rule = csvReader.read(RuleBean.class, header, processors);
            }
        } catch (IllegalArgumentException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);

            String message = "<html>The file " + rulesCSVFile.getName() + " failed to load. This is usually caused by a rule with extra columns.<br><br>"
                        + ex + "</html>";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);

            String message = "The file " + rulesCSVFile.getName() + " failed to load.";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (rules.isEmpty()) {
            logger.log(Level.WARNING, "Fail to load {0}, no rules detected.", rulesCSVFile.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "No rules detected.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String rootNodeTitle = null;
        RuleFile first = rules.get(0);
        boolean formatted = false;
        // Load ruleset name from CSV
        if (first.getRule().getId().startsWith("#RULESET_NAME ")) {
            rootNodeTitle = first.getRule().getId().replace("#RULESET_NAME ", "");
            rootNodeTitle = rootNodeTitle.trim();

            logger.log(Level.INFO, "Detected ruleset name: {0}", rootNodeTitle);

            rules.remove(first);
            formatted = true;
        }

        RuleBean rootBean = new RuleBean();
        if (rootNodeTitle != null) rootBean.setId(rootNodeTitle);
        else rootBean.setId("TEMP");
        DirectoryFile rootDirectory = new DirectoryFile(rootBean);

        formatRules(rules, rootDirectory, formatted);

        Ruleset ruleset = loadRulesetData(rules, rootDirectory);
        if (ruleset == null) {
            logger.log(Level.SEVERE, "Failed to read {0}.json!", rootNodeTitle);
            return false;
        }

        // If we found a name, check for overwrite.
        if (rootNodeTitle != null) {
            boolean safeMode = MainWindow.getInstance().getSettings().isSafeMode();
            File safeCSV = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + rootNodeTitle + ".csv");
            File saveLoc = ruleset.getSaveLocation();
            if (saveLoc.getPath().isEmpty()) {
                saveLoc = rulesCSVFile;
                ruleset.setSaveLocation(saveLoc);
            }

            // Normal Safe Mode redirect.
            if (safeMode && safeCSV.exists() && !safeCSV.equals(rulesCSVFile)
                        && saveLoc.equals(rulesCSVFile)) {
                logger.log(Level.INFO, "Safe Mode redirect to {0}.", safeCSV.getAbsolutePath());

                String m = "Loading pending Safe Mode changes for " + rootNodeTitle + ".";
                JOptionPane.showMessageDialog(null, m, "Safe Mode", JOptionPane.INFORMATION_MESSAGE);
                return loadRulesCSV(safeCSV);
            }


            boolean doOverwrite = false;
            String text = "Do you wish to overwrite " + rootNodeTitle + "?";

            if (!safeMode && safeCSV.exists() && !safeCSV.equals(rulesCSVFile)) {
                doOverwrite = true;
                text += "\nThis will delete your pending Safe Mode changes.";
            }

            if (!safeCSV.equals(rulesCSVFile) && !saveLoc.equals(rulesCSVFile)) {
//                System.out.println(saveLoc);
//                System.out.println(rulesCSVFile);
                doOverwrite = true;
                text += "\nThis will change your save location.";
            }

            Ruleset replace = null;
            // Check if ruleset is already loaded.
            for (Ruleset set : RulesetsManager.getRulesets()) {
                if (set.getName().equals(rootNodeTitle))  {
                    doOverwrite = true;
                    replace = set;
                    break;
                }
            }

            if (doOverwrite) {
                int overwrite = JOptionPane.showConfirmDialog(
                            null,
                            text,
                            "Overwrite Ruleset?",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                );
                if (overwrite == JOptionPane.YES_OPTION) {
                    ruleset.setSaveLocation(rulesCSVFile);
                    boolean saved = saveDataJSON(ruleset);
                    if (!saved) return false;

                    if (replace != null) RulesetsManager.getRulesets().remove(replace);
                    if (safeMode && safeCSV.exists() && !safeCSV.equals(rulesCSVFile)) {
                        logger.log(Level.INFO, "Safe Mode redirect to {0}.", safeCSV.getAbsolutePath());

                        String m = "Loading pending Safe Mode changes for " + rootNodeTitle + ".";
                        JOptionPane.showMessageDialog(null, m, "Safe Mode", JOptionPane.INFORMATION_MESSAGE);
                        return loadRulesCSV(safeCSV);
                    }
                    if (safeCSV.exists() && !safeCSV.equals(rulesCSVFile)) safeCSV.delete();
                }
                else if (overwrite == JOptionPane.NO_OPTION) rootNodeTitle = null;
                else return false; // Canceled
            }
        }

        // If null, ask the user for a name.
        if (rootNodeTitle == null || rootNodeTitle.isEmpty()) {
            rootNodeTitle = RulesetNameDialog.showInputDialog();
        }

        // If still null, then the user canceled the load.
        if (rootNodeTitle == null) return false;

        rootDirectory.setRulesetId(ruleset.getId());
        // The ruleset matches the CSV when loaded, of course.
        long timestamp = System.currentTimeMillis();
        ruleset.setSaveTime(timestamp);
        ruleset.setTimestamp(timestamp);
        ruleset.getRootDirectory().setTimestampCascade(timestamp);


        // SaveLocation check time
        if (ruleset.getSaveLocation().getPath().isEmpty()) ruleset.setSaveLocation(rulesCSVFile);
        ruleset.setName(rootNodeTitle);
        ruleset.setOriginalName(rootNodeTitle);

        // Remove overwritten ruleset.
        for (Ruleset set : RulesetsManager.getRulesets()) {
            if (set.getName().equals(rootNodeTitle))  {
                RulesetsManager.getRulesets().remove(set);
                RulesManager.setActiveRule(new RuleFile());
                break;
            }
        }

        // Add ruleset to RulesetManager
        RulesetsManager.getRulesets().add(ruleset);
        RulesetsManager.updateIdOverlaps();

        // CONTINUE - v1.0.2 - register all rules upon loading CSVs - Purple Nebula
        for (RuleFile rule : rules) {
            if (rule instanceof DirectoryFile) continue;
//            if (RulesetsManager.nameRepository.containsKey())
            RulesetsManager.nameRepository.put(rule.getRule().getId(),rule); // rule.getId()
        }

        return true;

    }

    private static String unEscapeNewlines(String s) {
        StringBuilder sb = new StringBuilder();
        boolean prevSlash = false;
        for (int i = 0; i < s.length(); i++)
            switch (s.charAt(i)){
                case '\\':
                    prevSlash = true;
                    break;
                case'n':
                    if (prevSlash) {
                        sb.append("\n");
                        prevSlash = false;
                        break;
                    }
                default:
                    if (prevSlash) {
                        sb.append("\\");
                        prevSlash = false;
                    }

                    sb.append(s.charAt(i));
            }
        return sb.toString();
    }

    //<editor-fold defaultstate="collapsed" desc="Formating">
    private static void formatRules(List<RuleFile> rules, DirectoryFile rootDirectory, boolean formatted) {
        List<RuleFile> consumedList = new ArrayList<>();

        consumedList.addAll(rules);

        // Add root RuleFile
        rules.add(0, rootDirectory);


        // Load any saved forced/blocked data.
        List<ForceBlockContainer> forceBlockData = new ArrayList<>();

        File jsonFile = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + rootDirectory.getName() + ".json");
        if (jsonFile.exists()) {
            // Deserialize ruleset
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(List.class, new ForceBlockDeserializer());
            Gson gson = gsonBuilder.create();

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                forceBlockData = gson.fromJson(reader, List.class);
            } catch (IOException | JsonSyntaxException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        // Initiate recursive formating
        while (!consumedList.isEmpty()) {
            format(consumedList, rules, rootDirectory, forceBlockData, formatted);
        }
    }

    // Recursive formating
    private static int format(List<RuleFile> consumedList, List<RuleFile> rules, DirectoryFile directory,
                List<ForceBlockContainer> forceBlockData, boolean formatted) {
        int trailingSpacers = 0; // Returns how many branch layers to close

        // Delete leading spacers
        List<RuleFile> leadingSpacers = new ArrayList<>();

        for (RuleFile file : consumedList) {
            if (file.isSpacer()) leadingSpacers.add(file);
            else break;
        }

        consumedList.removeAll(leadingSpacers);
        rules.removeAll(leadingSpacers);

        // Check for branches and collect local rules
        boolean branch = false;
        List<RuleFile> spacers = new ArrayList<>();
        List<RuleFile> localRules = new ArrayList<>();
        List<RuleFile> removed = new ArrayList<>();

        for (RuleFile file : consumedList) {
            if (formatted) {
                // Need to detect end-lines
                if (file.getRule().getId().startsWith("#END ")) {
                    removed.add(file);
                    removed.addAll(spacers);
                    spacers.clear();

                    trailingSpacers++;
                    continue;
                }

                // Need to remove extra spacers and spacer before folder-line
                if (file.isSpacer() && trailingSpacers > 0) {
                    removed.add(file);
                    // Out of place rules just get piled
                    // into whatever folder we are in.
                } else if (file.isSpacer()) {
                    spacers.add(file);
                } else if (file.isDirectory()) {
                    if (trailingSpacers == 0) branch = true;

                    break;
                } else {
                    localRules.addAll(spacers);
                    localRules.add(file);

                    spacers.clear();
                }
            } else {
                // Need to get end-folder lines out of the way.
                if (file.getRule().getId().startsWith("#END ")) {
                    removed.add(file);
                    continue;
                }

                // Count trailing spacers and deal with everything else.
                if (file.isSpacer()) {
                    spacers.add(file);
                } else if (file.isDirectory()) {
                    if (spacers.isEmpty()) branch = true;

                    trailingSpacers = spacers.size();
                    break;
                } else {
                    localRules.addAll(spacers);
                    localRules.add(file);

                    spacers.clear();
                }
            }
        }

        consumedList.removeAll(localRules);
        consumedList.removeAll(spacers);
        consumedList.removeAll(removed);

        rules.removeAll(spacers); // Remove trailing spacers.
        rules.removeAll(removed);

        // Use recursion to import branches
        if (branch) {
            while (trailingSpacers == 0 && !consumedList.isEmpty()) {
                DirectoryFile newDirectory = createDirectory(consumedList);
                directory.addBranch(newDirectory);
                trailingSpacers = format(consumedList, rules, newDirectory, forceBlockData, formatted);
            }
        }

        // Add local rules
        for (RuleFile rule : localRules) {
            if (rule.getRule().getId().startsWith("#END ")) continue; // Removes end-folder line.

            // Attach any saved force/block data.
            for (ForceBlockContainer container : forceBlockData) {
                if (container.getRuleId().equals(rule.getRule().getId())) {
                    rule.getFromBlacklist().addAll(container.getFromBlacklist());
                    rule.getFromWhitelist().addAll(container.getFromWhitelist());
                    rule.getToBlacklist().addAll(container.getToBlacklist());
                    rule.getToWhitelist().addAll(container.getToWhitelist());
                    break;
                }
            }

            directory.addLeaf(rule);
        }


        trailingSpacers--;

        return trailingSpacers;
    }
    //</editor-fold>

    private static DirectoryFile createDirectory(List<RuleFile> consumedList) {
        DirectoryFile directoryRule = (DirectoryFile) consumedList.get(0);
        consumedList.remove(directoryRule);

        String newDirectoryName = directoryRule.getName();

        // Split off extra lines in the ID field and put them in the notes field
        if (newDirectoryName.contains("\n")) {
            String extraLines = newDirectoryName.replaceFirst(".*\\n", "");
            newDirectoryName = newDirectoryName.replaceAll("\\n+.*", "");

            directoryRule.getRule().setId(directoryRule.getName().replaceAll("\\n+.*", ""));

            if (directoryRule.getRule().getNotes().equals("")) {
                directoryRule.getRule().setNotes(extraLines);
            } else {
                directoryRule.getRule().setNotes(directoryRule.getRule().getNotes() + "\n" + extraLines);
            }
        }

        directoryRule.getRule().setId(newDirectoryName);

        return directoryRule;
    }

    private static Ruleset loadRulesetData(List<RuleFile> rules, DirectoryFile rootDirectory) {

        Ruleset ruleset;

        // Check if the ruleset already has data saved and load it.
        File jsonFile = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + rootDirectory.getName() + ".json");
        if (jsonFile.exists()) {
            // Deserialize ruleset
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetDeserializer());
            gsonBuilder.registerTypeAdapter(Command.class, new CommandDeserializer());
//            gsonBuilder.registerTypeAdapter(CommandField.class, // Handled in CommandDeserializer
//                        new CommandFieldDeserializer());
            Gson gson = gsonBuilder.create();

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                ruleset = gson.fromJson(reader, Ruleset.class);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Convert functions to commands
            ruleset.getCommands().addAll(convertFunctions(ruleset.getId(),
                        ruleset.getCFunctions(), ruleset.getSFunctions()));

            ruleset.setRootDirectory((DirectoryFile) rules.get(0));

            return ruleset;
        } else {
            // Otherwise just create a new one.
            ruleset = new Ruleset(rootDirectory.getName());
        }

        ruleset.setRootDirectory(rootDirectory);
        int id = ruleset.getId();

        List<String> triggers = new ArrayList<>();
        List<String> cFunctions = new ArrayList<>();
        List<String> cVariables = new ArrayList<>();
        List<String> sFunctions = new ArrayList<>();
        List<String> sVariables = new ArrayList<>();
        List<String> tVariables = new ArrayList<>();

        for (RuleFile file : rules) {
            RuleBean rule = file.getRule();

            if (!file.isRule()) continue;

            // Check trigger
            String trigger = rule.getTrigger();

            boolean triggerKnown = triggers.contains(rule.getTrigger());
            triggerKnown |= trigger.startsWith("#") || trigger.isEmpty();
            triggerKnown |= RulesetsManager.contains(trigger,
                        RulesetsManager.DataType.TRIGGER, id);
            triggerKnown |= ruleset.getTriggers().contains(trigger);

            if (!triggerKnown) triggers.add(trigger);


            // Check conditions
            OUTER:
            for (String s : rule.getConditions().split("\n")) {
                if (s.isEmpty() || s.startsWith("#")) continue;

                if (s.startsWith("!")) s = s.replaceFirst("!", "");

                if (s.startsWith("$")) {
                    String variable = s.replaceAll("[ <>=!].+", "");

                    if (RulesetsManager.contains(variable, RulesetsManager.DataType.CVARIABLE, id)
                                || ruleset.getCVariables().contains(variable)
                                || cVariables.contains(variable)) continue;

                    cVariables.add(variable);
                } else {
                    String function = s.replaceAll(" .+", "");

                    for (Command com : ruleset.getCommands()) {
                        if (com.getName().equals(function)) continue OUTER;
                    }

                    if (RulesetsManager.contains(function, RulesetsManager.DataType.CFUNCTION, id)
                                || ruleset.getCFunctions().contains(function)
                                || cFunctions.contains(function)) continue;

                    cFunctions.add(function);
                }
            }


            // Check script
            OUTER:
            for (String s : rule.getScript().split("\n")) {
                if (s.isEmpty() || s.startsWith("#")) continue;

                if (s.startsWith("$")) {
                    String variable = s.replaceAll("[ <>=!].+", "");

                    if (RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, id)
                                || ruleset.getSVariables().contains(variable)
                                || sVariables.contains(variable)) continue;

                    sVariables.add(variable);
                } else {
                    String function = s.replaceAll(" .+", "");

                    for (Command com : ruleset.getCommands()) {
                        if (com.getName().equals(function)) continue OUTER;
                    }

                    if (RulesetsManager.contains(function, RulesetsManager.DataType.SFUNCTION, id)
                                || ruleset.getSFunctions().contains(function)
                                || sFunctions.contains(function)) continue;

                    sFunctions.add(function);
                }
            }


            // Check text
            for (String s : rule.getText().split("([ \\n])")) {
                String variable;
                if (s.startsWith("$")) variable = s.replaceFirst("[^$\\w].*", "");
                else continue;

                if (RulesetsManager.contains(variable, RulesetsManager.DataType.TVARIABLE, id)
                            || ruleset.getTVariables().contains(variable)
                            || tVariables.contains(variable)) continue;

                tVariables.add(variable);
            }


            // Check options - these are added to the conditions and script sections
            for (String variable : rule.getOptions().split("\n")) {
                if (variable.isEmpty()) continue;

                for (int i = -10; i < 1000; i++) {
                    if (variable.startsWith(i + ":")) {
                        variable = variable.replaceFirst(i + ":", "");
                        break;
                    }
                }

                variable = variable.replaceFirst(":.+", "");
//                variable = variable.concat(" (Dialog Option)");

                if (RulesetsManager.contains(variable, RulesetsManager.DataType.CVARIABLE, id)
                            || cVariables.contains(variable) || ruleset.getCVariables().contains(variable)) { }
                else cVariables.add(variable);

                if (RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, id)
                            || sVariables.contains(variable) || ruleset.getSVariables().contains(variable)) { }
                else sVariables.add(variable);

            }

        }

        // Convert functions to commands
        List<Command> commands = convertFunctions(id, cFunctions, sFunctions);

        // Save ruleset data
        ruleset.setRootDirectory((DirectoryFile) rules.get(0));
        ruleset.getTriggers().addAll(triggers);
        ruleset.getCommands().addAll(commands);
        ruleset.getCVariables().addAll(cVariables);
        ruleset.getSVariables().addAll(sVariables);
        ruleset.getTVariables().addAll(tVariables);

        return ruleset;
    }

    private static List<Command> convertFunctions(int id, List<String> cFunctions, List<String> sFunctions) {
        List<Command> commands = new ArrayList<>();
        for (String function : cFunctions) {
            Command com = new Command(function, id);

            if (sFunctions.contains(function)) {
                com.setWhichPanelsToShowOn(Command.ShowOn.BOTH);
            } else {
                com.setWhichPanelsToShowOn(Command.ShowOn.COND);
            }

            commands.add(com);
        }
        for (String function : sFunctions) {
            if (cFunctions.contains(function)) continue;

            Command com = new Command(function, id);
            com.setWhichPanelsToShowOn(Command.ShowOn.SCRIPT);
            commands.add(com);
        }

        return commands;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Save CSV Methods">
    public static boolean saveCSV(Ruleset ruleset, File file) {
        String[] inserts = { ruleset.getName(), file.getAbsolutePath() };
        logger.log(Level.INFO, "Saving {0} to {1}", inserts);

        DirectoryFile root = ruleset.getRootDirectory();
        StringBuilder sb = new StringBuilder();

        sb.append("id,trigger,conditions,script,text,options,notes\n");
        sb.append("\"#RULESET_NAME ").append(ruleset.getName()).append("\"" + BLANK_LINE);

        for (RuleFile leaf : root.getLeaves()) {
            RuleBean rule = leaf.getRule();
            String id = convertEscapedQuotes(rule.getId());
            String trigger = convertEscapedQuotes(rule.getTrigger());
            String conditions = convertEscapedQuotes(rule.getConditions());
            String script = convertEscapedQuotes(rule.getScript());
            String text = convertEscapedQuotes(rule.getText());
            String options = convertEscapedQuotes(rule.getOptions());
            String notes = convertEscapedQuotes(rule.getNotes());

            if (id.contains(",")) id = "\"" + id + "\"";
            if (trigger.contains(",")) trigger = "\"" + trigger + "\"";
            if (!conditions.isEmpty()) conditions = "\"" + conditions + "\"";
            if (!script.isEmpty()) script = "\"" + script + "\"";
            if (!text.isEmpty()) text = "\"" + text + "\"";
            if (!options.isEmpty()) options = "\"" + options + "\"";
            if (!notes.isEmpty()) notes = "\"" + notes + "\"";

            sb.append(id).append(",")
                .append(trigger).append(",")
                .append(conditions).append(",")
                .append(script).append(",")
                .append(text).append(",")
                .append(options).append(",")
                .append(notes).append("\n");
        }

        for (DirectoryFile branch : root.getBranches()) {
            sb.append(BLANK_LINE); // Empty line before folder title.
            sb.append(directoryToString(branch));
        }

        final String csv = sb.toString();

        try (PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true)) {
//            System.out.println(json);
            output.println(csv);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        boolean saved = saveDataJSON(ruleset);
        if (!saved) return false;

        long timestamp = System.currentTimeMillis();
        ruleset.setSaveTime(timestamp);
        ruleset.setTimestamp(timestamp);
        ruleset.getRootDirectory().setTimestampCascade(timestamp);

        return true;
    }

    private static boolean saveDataJSON(Ruleset ruleset) {
        File file;

        // Delete old ruleset data file.
        if (!ruleset.hasOriginalName()) {
            file = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + ruleset.getOriginalName() + ".json");
            if (file.exists()) {
                logger.log(Level.INFO, "Deleting {0}", file.getAbsolutePath());
                file.delete();
            }

            // Update original name
            ruleset.setOriginalName(ruleset.getName());
        }

        // Save auto-read TFVs that are being used
        for (String tfv : ruleset.getTempTriggers()) {
            if (RulesetsManager.isInUse(tfv, DataType.TRIGGER,
                        ruleset.getId())) {
                ruleset.getTriggers().add(tfv);
            }
        }
        for (String tfv : ruleset.getTempCVariables()) {
            if (RulesetsManager.isInUse(tfv, DataType.CVARIABLE,
                        ruleset.getId())) {
                ruleset.getCVariables().add(tfv);
            }
        }
        for (String tfv : ruleset.getTempSVariables()) {
            if (RulesetsManager.isInUse(tfv, DataType.SVARIABLE,
                        ruleset.getId())) {
                ruleset.getSVariables().add(tfv);
            }
        }
        for (String tfv : ruleset.getTempTVariables()) {
            if (RulesetsManager.isInUse(tfv, DataType.TVARIABLE,
                        ruleset.getId())) {
                ruleset.getTVariables().add(tfv);
            }
        }

        // Discard the unused auto-read TFVs
        ruleset.getTempTriggers().clear();
        ruleset.getTempCVariables().clear();
        ruleset.getTempSVariables().clear();
        ruleset.getTempTVariables().clear();

        // Create ruleset data file.
        file = new File(MainWindow.getInstance().getSettings().getSaveLocation() + File.separator + ruleset.getName() + ".json");

        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetSerializer());
        gsonBuilder.registerTypeAdapter(VanillaRuleset.class, new RulesetSerializer());
        gsonBuilder.registerTypeAdapter(Command.class, new CommandSerializer());
        gsonBuilder.registerTypeAdapter(CommandField.class, new CommandFieldSerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.disableHtmlEscaping().create();

        final String json = gson.toJson(ruleset);

        try (
            // Create a file
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
        ) {
            output.println(json);
        } catch (FileNotFoundException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
            JOptionPane.showMessageDialog(null, ex, "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        RulesetsManager.getCommandsChangedRulesets().remove(ruleset.getId());

        return true;
    }

    // Recursive
    private static String directoryToString(DirectoryFile dir) {
        StringBuilder sb = new StringBuilder();
        String dirId = convertEscapedQuotes(dir.getName());
        String dirNotes = convertEscapedQuotes(dir.getRule().getNotes());

        dirId = "# " + dirId;

        if (dirId.contains(",")) dirId = "\"" + dirId + "\"";
        if (!dirNotes.isEmpty()) dirNotes = "\"" + dirNotes + "\"";

        sb.append(dirId).append(",")
            .append(",").append(",").append(",").append(",").append(",")
            .append(dirNotes).append("\n");

        for (RuleFile leaf : dir.getLeaves()) {
            RuleBean rule = leaf.getRule();
            String id = convertEscapedQuotes(rule.getId());
            String trigger = convertEscapedQuotes(rule.getTrigger());
            String conditions = convertEscapedQuotes(rule.getConditions());
            String script = convertEscapedQuotes(rule.getScript());
            String text = convertEscapedQuotes(rule.getText());
            String options = convertEscapedQuotes(rule.getOptions());
            String notes = convertEscapedQuotes(rule.getNotes());

            if (id.contains(",")) id = "\"" + id + "\"";
            if (trigger.contains(",")) trigger = "\"" + trigger + "\"";
            if (!conditions.isEmpty()) conditions = "\"" + conditions + "\"";
            if (!script.isEmpty()) script = "\"" + script + "\"";
            if (!text.isEmpty()) text = "\"" + text + "\"";
            if (!options.isEmpty()) options = "\"" + options + "\"";
            if (!notes.isEmpty()) notes = "\"" + notes + "\"";

            sb.append(id).append(",")
                .append(trigger).append(",")
                .append(conditions).append(",")
                .append(script).append(",")
                .append(text).append(",")
                .append(options).append(",")
                .append(notes).append("\n");
        }

        for (DirectoryFile branch : dir.getBranches()) {
            sb.append(BLANK_LINE); // Empty line before folder title.
            sb.append(directoryToString(branch));
        }

        sb.append("\"#END ").append(dir.getName()).append("\"" + BLANK_LINE); // End of folder line.
//        sb.append("BLANK_LINE"); // Spacer

        return sb.toString();
    }

    private static String convertEscapedQuotes(String s){
        return convertEscapedQuotes(s, false);
    }

    /**
     * Converts the String's escaped quotes (\") to CSV's escape format (""),
     * which is \"\" in a String. If isScript is true, it tries to escape newlines
     * inside quotations (since each command must be on one line), but a stray
     * quotation mark will cause it to mess up.
     *
     * A better solution will have to wait for proper command support, which
     * could be a long time.
     *
     * @param s to convert.
     * @param isScript Whether this is the script column.
     * @return the converted String.
     */
    private static String convertEscapedQuotes(String s, boolean isScript){
        StringBuilder sb = new StringBuilder();
        boolean insideString = false;
        for (int i = 0; i < s.length(); i++)
            switch (s.charAt(i)){
                case '\"':
                    sb.append("\"\"");
                    insideString = !insideString;
                    break;
                case'\n':
                    if (isScript && insideString) {
                        sb.append("\\n");
                        break;
                    }
                default: sb.append(s.charAt(i));
            }
        return sb.toString();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="2.0 Conversion Methods">
    public static boolean convertV1Ruleset(File rootDirectory, File csvFile) throws IOException {
        String[] inserts = { rootDirectory.getAbsolutePath() };
        logger.log(Level.INFO, "Converting {0}.", inserts);

        Ruleset ruleset = openFolder(rootDirectory);
        if (ruleset == null) return false;

        if (!csvFile.getPath().endsWith(".csv")) csvFile = new File(csvFile + ".csv");

        ruleset.setSaveLocation(csvFile);
        return saveCSV(ruleset, csvFile);
    }

    private static Ruleset openFolder(File rootDirectory) throws IOException {
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(rootDirectory.listFiles()));
        Collections.sort(files);
//        System.out.println(files);

        // Check if data.json exists
        File dataJson = new File(rootDirectory.getAbsolutePath() + File.separator + "data.json");
        Ruleset ruleset;

        if (files.contains(dataJson)) {
            // Load data
            dataJson = files.get(files.indexOf(dataJson));
            files.remove(dataJson);

            // Deserialize
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetDeserializer());
            Gson gson = gsonBuilder.create();

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(dataJson), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                ruleset = gson.fromJson(reader, Ruleset.class);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }


            // Add data to manager
            if (ruleset == null) {
                logger.log(Level.SEVERE, "Failed to load data.json {0}.", rootDirectory.getName());
                // Show error window
                JOptionPane.showMessageDialog(null, "Failed to load data.json for " + rootDirectory.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } else {
            logger.log(Level.SEVERE, "No data.json file in {0}.", rootDirectory.getName());
            // Show error window
            JOptionPane.showMessageDialog(null, "No data.json file in " + rootDirectory.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        DirectoryFile rootDir = ruleset.getRootDirectory();

        // Recursive load through subfolders
        for (File file : files) {
            if (!file.isDirectory()) continue;

            // Create directory RuleFile
            DirectoryFile subDirectory = new DirectoryFile();
            subDirectory.setRulesetId(ruleset.getId());

            rootDir.addBranch(subDirectory);

            openFolderRecursive(file, subDirectory);
        }

        for (File file : files) {
            if (!file.getPath().endsWith(".json")) continue;

            // Add rule
            loadRule(file, rootDir);
        }

        return ruleset;
    }

    private static void openFolderRecursive(File fileDirectory, DirectoryFile directory) throws IOException {
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(fileDirectory.listFiles()));
        Collections.sort(files);

        // Need to unite directory and comment
        File dirData = new File(fileDirectory.getAbsoluteFile() + File.separator + "data.json");

        if (files.contains(dirData)) {
            // Load data
            dirData = files.get(files.indexOf(dirData));
            files.remove(dirData);

            // Deserialize
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(RuleFile.class, new RuleDeserializer());
            Gson gson = gsonBuilder.create();

            RuleFile temp = null;

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(dirData), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                temp = gson.fromJson(reader, RuleFile.class);
            } catch (JsonSyntaxException ex) {
                logger.log(Level.WARNING, ex.toString(), ex);
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Add data to directory's RuleFile
            if (temp != null) {
                directory.getRule().setId(temp.getName());
                directory.getRule().setNotes(temp.getRule().getNotes());
            }
        }

        // Recursive load through subfolders
        for (File file : files) {
            if (!file.isDirectory()) continue;

            // Create directory RuleFile
            DirectoryFile subDirectory = new DirectoryFile();
            subDirectory.setRulesetId(directory.getRulesetId());

            directory.addBranch(subDirectory);

            // recurse
            openFolderRecursive(file, subDirectory);
        }

        // Load rules
        for (File file : files) {
            if (!file.getPath().endsWith(".json")) continue;

            // Add rule
            loadRule(file, directory);
        }
    }

    private static void loadRule(File file, DirectoryFile directory) throws IOException {
        // Deserialize
        // Configure Gson
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RuleFile.class, new RuleDeserializer());
        Gson gson = gsonBuilder.create();

        RuleFile newRule = null;

        // The JSON data
        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)){
            // Parse JSON to Java
            newRule = gson.fromJson(reader, RuleFile.class);
        } catch (JsonSyntaxException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add rule
        RuleFile rule = new RuleFile();
        if (newRule != null) rule = newRule;
        rule.setRulesetId(directory.getRulesetId());

        directory.addLeaf(rule);
    }

    //</editor-fold>
}
