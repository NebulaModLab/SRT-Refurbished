/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.file;

import sj.editor.data.rules.*;
import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import javax.swing.JOptionPane;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.io.json.*;
import sj.misc.Misc;

/**
 * This class has been deprecated by FileIO_V2.
 *
 * @author SafariJohn (original SRT)
 * Contributers: Armithaig
 */
@Deprecated
public class FileIO {
    /**
     * Remember to adjust vanilla's rules.csv so it imports safely.
     * Clear the command line output before importing for easy copy-paste.
     * Once imported, copy-paste all the rules and fix any errors.
     * - Replace \\" with \".
     * - "special" and "Continue" need \" instead of ".
     * Then divide rules into separate packs to avoid class size overrun.
     * And don't forget updating functions and variables.
     * Finally, save the JSON tree.
     */
    public static final boolean VANILLA_UPDATE = false;

    //<editor-fold defaultstate="collapsed" desc="Open Methods">
    public static boolean openFolder(File rootDirectory) throws IOException {
        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(rootDirectory.listFiles()));
        Collections.sort(files);
//        System.out.println(files);

        // Check if data.json exists
        File dataJson = new File(rootDirectory.getAbsolutePath() + File.separator + "data.json");
        int rulesetID;

        if (files.contains(dataJson)) {
            // Load data
            dataJson = files.get(files.indexOf(dataJson));
            files.remove(dataJson);

            // Deserialize
            // Configure Gson
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetDeserializer());
            Gson gson = gsonBuilder.create();

            Ruleset ruleset;

            // The JSON data
            try (Reader reader = new InputStreamReader(new FileInputStream(dataJson), StandardCharsets.UTF_8)){
                // Parse JSON to Java
                ruleset = gson.fromJson(reader, Ruleset.class);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }


            // Add data to manager
            if (ruleset == null) {
                // Show error window
                JOptionPane.showMessageDialog(null, "Failed to load data.json for " + rootDirectory.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                // Check if already exists
                if (RulesetsManager.getRulesets().contains(ruleset)) {
                    JOptionPane.showMessageDialog(null, rootDirectory.getName() + " is already open.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                RulesetsManager.getRulesets().add(ruleset);
                rulesetID = ruleset.getId();
            }

        } else {
            // Show error window
            JOptionPane.showMessageDialog(null, "No data.json file in " + rootDirectory.getName() + ".", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

//        if (rulesetID.isEmpty()) return false;
        Ruleset ruleset = RulesetsManager.getRuleset(rulesetID);
        DirectoryFile rootDir = RulesetsManager.getRuleset(rulesetID).getRootDirectory();

        // Recursive load through subfolders
        for (File file : files) {
            if (!file.isDirectory()) continue;

            // Create directory RuleFile
            DirectoryFile subDirectory = new DirectoryFile();
            subDirectory.setRulesetId(rulesetID);

            rootDir.addBranch(subDirectory);

            openFolderRecursive(file, subDirectory);
        }

        if (VANILLA_UPDATE) generateCode(null); // For vanilla hardcoding

        for (File file : files) {
            if (!file.getPath().endsWith(".json")) continue;

            // Add rule
            loadRule(file, rootDir);
        }

        layer = 0; // For vanilla hardcoding

        long timestamp = System.currentTimeMillis();
        ruleset.setTimestamp(timestamp);
        ruleset.setSaveTime(timestamp);
        rootDir.setTimestampCascade(timestamp);

        return true;
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
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Add data to directory's RuleFile
            if (temp != null) {
                directory.getRule().setId(temp.getName());
                directory.getRule().setNotes(temp.getRule().getNotes());
            }
        }

        if (VANILLA_UPDATE) generateCode(directory); // For vanilla hardcoding

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

        if (VANILLA_UPDATE) generateCode(null); // For vanilla hardcoding
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
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add rule
        RuleFile rule = new RuleFile();
        if (newRule != null) rule = newRule;
        rule.setRulesetId(directory.getRulesetId());

        directory.addLeaf(rule);

        if (VANILLA_UPDATE) generateCode(rule); // For vanilla hardcoding
    }

    //<editor-fold defaultstate="collapsed" desc="Vanilla Hardcode Generation Code">
    private static int layer = 0;

    private static void generateCode(RuleFile file) {
        if (file == null) {
            layer--;
            System.out.println("//</editor-fold>");
            System.out.println("");
            return;
        }

        if (file.isDirectory()) {
            System.out.println("rule = new RuleBean();");
            System.out.println("rule.setId(\"" + file.getName() + "\");");
            String notes = unEscapeString(file.getRule().getNotes());
            System.out.println("rule.setNotes(\"" + notes + "\");");
            System.out.println("");
            System.out.println("directory" + layer + " = new DirectoryFile(rule);");
            System.out.println("directory" + layer + ".setRulesetId(rootDirectory.getRulesetId());");
            if (layer == 0) {
                System.out.println("rootDirectory" + ".addBranch(directory" + layer + ");");
            }
            else {
                System.out.println("directory" + (layer - 1) + ".addBranch(directory" + layer + ");");
            }
            System.out.println("");
            System.out.println("//<editor-fold defaultstate=\"collapsed\" desc=\"\"" + file.getName() + "\" Folder\">");

            layer++;
        } else {
            System.out.println("rule = new RuleBean();");
            System.out.println("rule.setId(\"" + file.getName() + "\");");
            System.out.println("rule.setTrigger(\"" + file.getRule().getTrigger() + "\");");

            String conditions = unEscapeString(file.getRule().getConditions());
            System.out.println("rule.setConditions(\"" + conditions + "\");");

            String script = unEscapeString(file.getRule().getScript());
            System.out.println("rule.setScript(\"" + script + "\");");

            String text = unEscapeString(file.getRule().getText());
            System.out.println("rule.setText(\"" + text + "\");");

            String options = unEscapeString(file.getRule().getOptions());
            System.out.println("rule.setOptions(\"" + options + "\");");

            String notes = unEscapeString(file.getRule().getNotes());
            System.out.println("rule.setNotes(\"" + notes + "\");");
            System.out.println("");
            System.out.println("file = new RuleFile(rule);");
            System.out.println("file.setRulesetId(rootDirectory.getRulesetId());");
            if (layer == 0) {
                System.out.println("rootDirectory.addLeaf(file);");
            }
            else {
                System.out.println("directory" + (layer - 1) + ".addLeaf(file);");
            }
            System.out.println("");
        }

    }

    private static String unEscapeString(String s){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<s.length(); i++)
            switch (s.charAt(i)){
                case '\n': sb.append("\\n"); break;
                case '\t': sb.append("\\t"); break;
                case '\"': sb.append("\\\""); break;
                case '\'': sb.append("\\\'"); break;
                default: sb.append(s.charAt(i));
            }
        return sb.toString();
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Save Methods">
    private static int index = 1;
    public static boolean saveRulesets(List<Ruleset> rulesets, File target) {
        // If only one ruleset and ruleset's name and target's name are same, overwrite target
        if (rulesets.size() == 1 && rulesets.get(0).getName().equals(target.getName())) {
            Ruleset ruleset = rulesets.get(0);

            // If target doesn't exist, we're good
            if (target.mkdirs()) {
                index = 1;

                createDataJSON(ruleset, target);
                save(ruleset.getRootDirectory(), target);

                long timestamp = System.currentTimeMillis();
                ruleset.setSaveTime(timestamp);
                ruleset.setTimestamp(timestamp);
                ruleset.getRootDirectory().setTimestampCascade(timestamp);
            } // Otherwise it needs to be deleted
            else if (delete(target)) {
                index = 1;

                target.mkdir();
                createDataJSON(ruleset, target);
                save(ruleset.getRootDirectory(), target);

                long timestamp = System.currentTimeMillis();
                ruleset.setSaveTime(timestamp);
                ruleset.setTimestamp(timestamp);
                ruleset.getRootDirectory().setTimestampCascade(timestamp);
            } // If it can't be deleted, try to save again with target + 1
            else {
                if (index == 20) {
                    JOptionPane.showMessageDialog(null, "Failed to overwrite " + target.getPath() + ".", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                JOptionPane.showMessageDialog(null, "Can't overwrite " + target.getPath() + ". Trying " + target.getPath() + index + ".", "Error", JOptionPane.ERROR_MESSAGE);
                File newTarget = new File(target.getPath() + "" + index++);
                saveRulesets(rulesets, newTarget);
            }

        } // Else, save rulesets in target
        else {
            for (Ruleset ruleset : rulesets) {
                index = 1;

                List<Ruleset> single = new ArrayList<>();
                single.add(ruleset);

                File newTarget = new File(target + File.separator + ruleset.getName());
                saveRulesets(single, newTarget);
            }
        }

        return true;
    }

    private static void createDataJSON(Ruleset ruleset, File directory) {
        // Create data.json
        File file = new File(directory.getPath() + File.separator + "data.json");

        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetSerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.disableHtmlEscaping().create();

        final String json = gson.toJson(ruleset);

        try (
            // Create a file
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
        ) {
            output.println(json);
        } catch (FileNotFoundException ex) {}
    }

    private static boolean delete(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (Files.isSymbolicLink(f.toPath())) {
                    f.delete();
                } else {
                    delete(f);
                }
            }
        }

        return file.delete();
    }

    private static void save(DirectoryFile dir, File dirFile) {
        for (DirectoryFile branch : dir.getBranches()) {
            File branchFile = createDirectory(branch, dirFile, dir.getBranches().indexOf(branch));
            save(branch, branchFile);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            addRule(leaf, dirFile, dir.getLeaves().indexOf(leaf));
        }
    }

    private static File createDirectory(DirectoryFile directory, File file, int index) {
        String newDirectoryName = directory.getName();

        if (newDirectoryName.length() > 20) newDirectoryName = newDirectoryName.substring(0, 20);

        if (newDirectoryName.contains("/") || newDirectoryName.contains("\\") || newDirectoryName.contains(File.separator)) {
            newDirectoryName = newDirectoryName.replaceAll("/", "-");
            newDirectoryName = newDirectoryName.replaceAll("\\\\", "-");
//            newDirectoryName = newDirectoryName.replaceAll(File.separator, "-");
        }

        newDirectoryName = newDirectoryName.trim();

        File newDirectory = new File(file.getPath() + File.separator + Misc.threeDigitFormat(index) + " " + newDirectoryName);
        newDirectory.mkdir();

        addRule(directory, newDirectory, -1);

        return newDirectory;
    }

    private static void addRule(RuleFile rule, File directory, int index) {
        String fileName;

        // Select name based on spacer/comment/rule
        if (rule.isSpacer()) {
            fileName = "SPACER";
        } else if (rule.isDirectory()) {
            fileName = "data";
        } else if (rule.isComment()) {
            fileName = "COMMENT";
        } else {
            fileName = rule.getName();
            if (fileName.length() > 30) fileName = fileName.substring(0, 30);
        }

        File file;

        if (index < 0) file = new File(directory.getPath() + File.separator + fileName + ".json");
        else file = new File(directory.getPath() + File.separator + Misc.threeDigitFormat(index) + " " + fileName + ".json");

        // Create JSON structure
        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RuleFile.class, new RuleSerializer());
        gsonBuilder.registerTypeAdapter(DirectoryFile.class, new DirectorySerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.disableHtmlEscaping().create();

        final String json = gson.toJson(rule);

        try (
                // Create a file
                PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
            ) {
//            System.out.println(json);
            output.println(json);
        } catch (FileNotFoundException ex) {
//            System.out.println(ex);
            JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
//            Logger.getLogger(MainWindowOld.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Import Methods">
    private static int rootIndex = 0;

    public static boolean importRulesCSV(File rulesCSVFile) throws IOException {
        if (!rulesCSVFile.exists()) {
            javax.swing.JOptionPane.showMessageDialog(null, "File does not exist.", "ERROR", javax.swing.JOptionPane.ERROR_MESSAGE);
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
                if (rule.getId().startsWith("#END ")) { // Exception for end-section comments (particularly Nexerlin's rules.csv)
                    if (rule.getNotes().isEmpty()) rule.setNotes("Assumed this is a marker comment for the end of a section.");
                    else rule.setNotes(rule.getNotes() + "\nAssumed this is a marker comment for the end of a section.");

                    rules.add(new RuleFile(rule));
                } else if (rule.isDirectory()) {
                    String dirId = rule.getId();
                    dirId = dirId.replaceFirst("#", "");
                    if (dirId.startsWith(" ")) dirId = dirId.replaceFirst(" +", "");
                    rule.setId(dirId);

                    rules.add(new DirectoryFile(rule));
                }
                else rules.add(new RuleFile(rule));

                rule = csvReader.read(RuleBean.class, header, processors);
            }
        } catch (IllegalArgumentException ex) {
            String message = "<html>The file " + rulesCSVFile.getName() + " failed to import. See \"Import Failure (Illegal Arguments)\" in the Troubleshooting section of the readme.<br><br>"
                        + ex + "</html>";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            String message = "The file " + rulesCSVFile.getName() + " failed to import.";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (rules.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No rules detected. Import failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check for deep nesting
        int count = 0;
        for (RuleFile rule : rules) {
            if (rule.isDirectory()) count++;
            else count = 0;

            if (count > 8) {
                JOptionPane.showMessageDialog(null, "Import failed. See \"Import Failure (Nesting)\" in the Troubleshooting section of the readme.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }


        // Ask user for name of root folder
        String rootNodeTitle = JOptionPane.showInputDialog("Name import:");
        // Should create a custom dialog and block illegal input

        if (rootNodeTitle == null || rootNodeTitle.isEmpty()) return false;

        File rootDirectory = new File(MainWindow.getInstance().getSettings().getSaveLocation().getPath() + File.separator + rootNodeTitle);
        rootDirectory.mkdir();

        formatRulesForJSON(rules, rootDirectory);

        readTriggersFunctionsVariables(rules, rootDirectory);

        try {
            openFolder(rootDirectory.getAbsoluteFile());
        } catch (IOException ex) {
//            Logger.getLogger(FileIO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    //<editor-fold defaultstate="collapsed" desc="Formating">
    private static void formatRulesForJSON(List<RuleFile> rules, File rootDirectory) {
        List<RuleFile> consumedList = new ArrayList<>();

        consumedList.addAll(rules);

        // Add root RuleFile
        RuleBean rootBean = new RuleBean();
        rootBean.setId(rootDirectory.getName());
        rules.add(0, new DirectoryFile(rootBean));

        // Initiate recursive formating
        while (!consumedList.isEmpty()) {
            format(consumedList, rules, rootDirectory, true);
        }
    }

    // Recursive formating
    private static int format(List<RuleFile> consumedList, List<RuleFile> rules, File directory) {
        return format(consumedList, rules, directory, false);
    }

    private static int format(List<RuleFile> consumedList, List<RuleFile> rules, File directory, boolean root) {
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

        for (RuleFile file : consumedList) {
            if (file.isSpacer()) {
                spacers.add(file);
            } else if (file.isDirectory()) {
                if (spacers.isEmpty()) { branch = true; }

                trailingSpacers = spacers.size();
                break;
            } else {
                localRules.addAll(spacers);
                localRules.add(file);

                spacers.clear();
            }
        }

        consumedList.removeAll(localRules);
        consumedList.removeAll(spacers);

        rules.removeAll(spacers);

        int fileIndex = (root) ? rootIndex : 0;

        // Use recursion to import branches
        if (branch) {
            while (trailingSpacers == 0) {
                File newDirectory = createDirectory(consumedList, directory, fileIndex);
                trailingSpacers = format(consumedList, rules, newDirectory);

                fileIndex++;
                if (root) {
                    rootIndex++;
                    fileIndex = rootIndex;
                }
            }
        }

        // Add local rules
        for (RuleFile rule : localRules) {
            addRule(rule, directory, fileIndex);

            fileIndex++;
        }


        trailingSpacers--;

        return trailingSpacers;
    }
    //</editor-fold>

    private static File createDirectory(List<RuleFile> consumedList, File directory, int index) {
        RuleFile directoryRule = consumedList.get(0);
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

        if (newDirectoryName.length() > 20) newDirectoryName = newDirectoryName.substring(0, 20);

        if (newDirectoryName.contains("/") || newDirectoryName.contains("\\") || newDirectoryName.contains(File.separator)) {
            newDirectoryName = newDirectoryName.replaceAll("/", "-");
            newDirectoryName = newDirectoryName.replaceAll("\\\\", "-");
//            newDirectoryName = newDirectoryName.replaceAll(File.separator, "-");
        }

        newDirectoryName = newDirectoryName.trim();

        File newDirectory = new File(directory.getPath() + File.separator + Misc.threeDigitFormat(index) + " " + newDirectoryName);
        newDirectory.mkdir();

        addRule(directoryRule, newDirectory, -1);

        return newDirectory;
    }

    private static void readTriggersFunctionsVariables(List<RuleFile> rules, File rootDirectory) {
        Ruleset ruleset = new Ruleset(rootDirectory.getName());
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

            if (RulesetsManager.contains(trigger, RulesetsManager.DataType.TRIGGER, id)
                        || triggers.contains(rule.getTrigger()) || trigger.startsWith("#")
                        || trigger.isEmpty())
            {}
            else {
                triggers.add(trigger);
            }


            // Check conditions
            for (String s : rule.getConditions().split("\n")) {
                if (s.isEmpty() || s.startsWith("#")) continue;

                if (s.startsWith("!")) s = s.replaceFirst("!", "");

                if (s.startsWith("$")) {
                    String variable = s.replaceAll("[ <>=!].+", "");

                    if (RulesetsManager.contains(variable, RulesetsManager.DataType.CVARIABLE, id)
                                || cVariables.contains(variable)) continue;

                    cVariables.add(variable);
                } else {
                    String function = s.replaceAll(" .+", "");

                    if (RulesetsManager.contains(function, RulesetsManager.DataType.CFUNCTION, id)
                                || cFunctions.contains(function)) continue;

                    cFunctions.add(function);
                }
            }


            // Check script
            for (String s : rule.getScript().split("\n")) {
                if (s.isEmpty() || s.startsWith("#")) continue;

                if (s.startsWith("$")) {
                    String variable = s.replaceAll("[ <>=!].+", "");

                    if (RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, id)
                                || sVariables.contains(variable)) continue;

                    sVariables.add(variable);
                } else {
                    String function = s.replaceAll(" .+", "");

                    if (RulesetsManager.contains(function, RulesetsManager.DataType.SFUNCTION, id)
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

                if (cVariables.contains(variable) || RulesetsManager.contains(variable, RulesetsManager.DataType.CVARIABLE, id)) { }
                else cVariables.add(variable);

                if (sVariables.contains(variable) || RulesetsManager.contains(variable, RulesetsManager.DataType.SVARIABLE, id)) { }
                else sVariables.add(variable);

            }

        }

        // Save ruleset data
        ruleset.setRootDirectory((DirectoryFile) rules.get(0));
        ruleset.getTriggers().addAll(triggers);
        ruleset.getCFunctions().addAll(cFunctions);
        ruleset.getCVariables().addAll(cVariables);
        ruleset.getSFunctions().addAll(sFunctions);
        ruleset.getSVariables().addAll(sVariables);
        ruleset.getTVariables().addAll(tVariables);

        // To root directory
        File file = new File(rootDirectory.getPath() + File.separator + "data.json");

        // Configure GSON
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Ruleset.class, new RulesetSerializer());
        gsonBuilder.setPrettyPrinting();
        final Gson gson = gsonBuilder.disableHtmlEscaping().create();

        final String json = gson.toJson(ruleset);

        try (
            // Create a file
            PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
        ) {
//            System.out.println(json);
            output.println(json);
        } catch (FileNotFoundException ex) {
//            System.out.println(ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Export Methods">
    public static boolean exportCSV(Ruleset ruleset, File file) {
        DirectoryFile root = ruleset.getRootDirectory();
        StringBuilder sb = new StringBuilder();

        sb.append("id,trigger,conditions,script,text,options,notes\n");

        for (RuleFile leaf : root.getLeaves()) {
            RuleBean rule = leaf.getRule();
            String id = unEscapeQuotes(rule.getId());
            String trigger = unEscapeQuotes(rule.getTrigger());
            String conditions = unEscapeQuotes(rule.getConditions());
            String script = unEscapeQuotes(rule.getScript());
            String text = unEscapeQuotes(rule.getText());
            String options = unEscapeQuotes(rule.getOptions());
            String notes = unEscapeQuotes(rule.getNotes());

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
            sb.append(directoryToString(branch));
        }

        final String csv = sb.toString();

        try (
                // Create a file
                PrintWriter output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
            ) {
//            System.out.println(json);
            output.println(csv);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
//            Logger.getLogger(MainWindowOld.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    private static String directoryToString(DirectoryFile dir) {
        StringBuilder sb = new StringBuilder();
        String dirId = unEscapeQuotes(dir.getName());
        String dirNotes = unEscapeQuotes(dir.getRule().getNotes());

        if (dirId.contains(",")) dirId = "\"" + dirId + "\"";
        if (!dirNotes.isEmpty()) dirNotes = "\"" + dirNotes + "\"";

        sb.append("# ").append(dirId).append(",")
            .append(",").append(",").append(",").append(",").append(",")
            .append(dirNotes).append("\n");

        for (RuleFile leaf : dir.getLeaves()) {
            RuleBean rule = leaf.getRule();
            String id = unEscapeQuotes(rule.getId());
            String trigger = unEscapeQuotes(rule.getTrigger());
            String conditions = unEscapeQuotes(rule.getConditions());
            String script = unEscapeQuotes(rule.getScript());
            String text = unEscapeQuotes(rule.getText());
            String options = unEscapeQuotes(rule.getOptions());
            String notes = unEscapeQuotes(rule.getNotes());

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
            sb.append(directoryToString(branch));
        }

        sb.append(",,,,,,\n"); // Spacer

        return sb.toString();
    }

    private static String unEscapeQuotes(String s){
        StringBuilder sb = new StringBuilder();
        boolean insideString = false;
        for (int i=0; i<s.length(); i++)
            switch (s.charAt(i)){
                case '\"':
                    sb.append("\"\"");
                    insideString = !insideString;
                    break;
                case'\n':
                    if (insideString) {
                        sb.append("\\n");
                        break;
                    }
                default: sb.append(s.charAt(i));
            }
        return sb.toString();
    }
    //</editor-fold>
}
