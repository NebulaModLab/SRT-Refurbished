/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import java.io.File;
import sj.editor.data.vanilla.VanillaRuleset;
import sj.editor.data.rules.*;
import java.util.*;
import sj.editor.MainWindow;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.ShowOn;

/**
 * @author SafariJohn (original SRT)
 */
public class RulesetsManager {
    public static enum DataType {
        TRIGGER, COMMAND, CFUNCTION, CVARIABLE, SFUNCTION, SVARIABLE, TVARIABLE
    }

    // Concatenate all the rules in the tree into one list.
    // It doesn't matter what ruleset they are from for undo/redo.
    private static final Stack treeUndoStack = new Stack();
    private static final Stack treeRedoStack = new Stack();

    private static final List<Ruleset> rulesets = new ArrayList<Ruleset>() {
//        @Override
//        public boolean isEmpty() {
//            return this.size() == 0 || (this.size() == 1 && this.get(0).getId() == vanillaId);
//        }
    };

    private static final Set<String> idOverlaps = new HashSet<>();

    private static final Map<Integer, RuleFile> backupRules = new HashMap<>();

    private static final Set<Integer> commandsChangedRulesets = new HashSet<>();

    // This is only the triggers, functions, and variables.
    private static final Ruleset vanilla = new VanillaRuleset();

    /**
     * Checks if the specified trigger, function, or variable is vanilla or already in the specified ruleset's data
     * @param tfv
     * @param type
     * @param rulesetId
     * @return
     */
    public static boolean contains(String tfv, DataType type, int rulesetId) {
        switch (type) {
            case TRIGGER:
                if (vanilla.getTriggers().contains(tfv)) return true;
                break;
            case COMMAND:
                for (Command com : vanilla.getCommands()) {
                    if (com.getName().equals(tfv)) return true;
                }
                break;
            case CFUNCTION:
                if (vanilla.getCFunctions().contains(tfv)) return true;

                for (Command com : vanilla.getCommands()) {
                    if (com.getName().equals(tfv)) return true;
                }

                break;
            case CVARIABLE:
                if (vanilla.getCVariables().contains(tfv)) return true;
                break;
            case SFUNCTION:
                if (vanilla.getSFunctions().contains(tfv)) return true;

                for (Command com : vanilla.getCommands()) {
                    if (com.getName().equals(tfv)) return true;
                }

                break;
            case SVARIABLE:
                if (vanilla.getSVariables().contains(tfv)) return true;
                break;
            case TVARIABLE:
                if (vanilla.getTVariables().contains(tfv)) return true;
        }


        for (Ruleset ruleset : rulesets) {
            if (ruleset.getId() != rulesetId) continue;

            switch (type) {
                case TRIGGER:
                    if (ruleset.getTriggers().contains(tfv)) return true;
                    if (ruleset.getTempTriggers().contains(tfv)) return true;
                    break;
                case COMMAND:
                    for (Command com : ruleset.getCommands()) {
                        if (com.getName().equals(tfv)) return true;
                    }
                    break;
                case CFUNCTION:
                    if (ruleset.getCFunctions().contains(tfv)) return true;

                    for (Command com : ruleset.getCommands()) {
                        if (com.getName().equals(tfv)) return true;
                    }

                    break;
                case CVARIABLE:
                    if (ruleset.getCVariables().contains(tfv)) return true;
                    if (ruleset.getTempCVariables().contains(tfv)) return true;
                    break;
                case SFUNCTION:
                    if (ruleset.getSFunctions().contains(tfv)) return true;

                    for (Command com : ruleset.getCommands()) {
                        if (com.getName().equals(tfv)) return true;
                    }

                    break;
                case SVARIABLE:
                    if (ruleset.getSVariables().contains(tfv)) return true;
                    if (ruleset.getTempSVariables().contains(tfv)) return true;
                    break;
                case TVARIABLE:
                    if (ruleset.getTVariables().contains(tfv)) return true;
                    if (ruleset.getTempTVariables().contains(tfv)) return true;
            }
        }

        return false;
    }

    /**
     * Checks if the specified trigger, function, or variable is being used in the given ruleset
     * @param tfv
     * @param type
     * @param rulesetId
     * @return
     */
    public static boolean isInUse(String tfv, DataType type, int rulesetId) {
        for (Ruleset ruleset : rulesets) {
            if (ruleset.getId() != rulesetId) continue;

            DirectoryFile root = ruleset.getRootDirectory();

            switch (type) {
                case TRIGGER: return isTriggerInUse(tfv, root);
                case COMMAND: return isCommandInUse(tfv, root, ShowOn.BOTH);
                case CFUNCTION: return isCommandInUse(tfv, root, ShowOn.COND);
                case CVARIABLE: return isCVariableInUse(tfv, root);
                case SFUNCTION: return isCommandInUse(tfv, root, ShowOn.SCRIPT);
                case SVARIABLE: return isSVariableInUse(tfv, root);
                case TVARIABLE: return isTVariableInUse(tfv, root);
            }
        }

        return false;
    }

    private static boolean isTriggerInUse(String trigger, DirectoryFile dir) {
        for (DirectoryFile branch : dir.getBranches()) {
            isTriggerInUse(trigger, branch);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            if (leaf.getRule().getTrigger().equals(trigger)) return true;
        }

        return false;
    }

    private static boolean isCommandInUse(String command, DirectoryFile dir, ShowOn which) {
        for (DirectoryFile branch : dir.getBranches()) {
            isCommandInUse(command, branch, which);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            if (which == ShowOn.BOTH || which == ShowOn.COND) {
                String[] cLines = leaf.getRule().getConditions().split("\n");

                for (String line : cLines) {
                    for (String word : line.split(" ")) {
                        if (word.equals(command)) return true;
                    }
                }
            }

            if (which == ShowOn.BOTH || which == ShowOn.SCRIPT) {
                String[] cLines = leaf.getRule().getScript().split("\n");

                for (String line : cLines) {
                    for (String word : line.split(" ")) {
                        if (word.equals(command)) return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isCVariableInUse(String variable, DirectoryFile dir) {
        for (DirectoryFile branch : dir.getBranches()) {
            isCVariableInUse(variable, branch);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            String[] lines = leaf.getRule().getConditions().split("\n");

            for (String line : lines) {
                for (String word : line.split(" ")) {
                    if (word.equals(variable)) return true;
                }
            }
        }

        return false;
    }

    private static boolean isSVariableInUse(String variable, DirectoryFile dir) {
        for (DirectoryFile branch : dir.getBranches()) {
            isSVariableInUse(variable, branch);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            String[] lines = leaf.getRule().getScript().split("\n");

            for (String line : lines) {
                for (String word : line.split(" ")) {
                    if (word.equals(variable)) return true;
                }
            }
        }

        return false;
    }

    private static boolean isTVariableInUse(String variable, DirectoryFile dir) {
        for (DirectoryFile branch : dir.getBranches()) {
            isTVariableInUse(variable, branch);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            String[] lines = leaf.getRule().getText().split("\n");

            for (String line : lines) {
                for (String word : line.split(" ")) {
                    if (word.equals(variable)) return true;
                }
            }
        }

        return false;
    }

    public static List<Ruleset> getRulesets() {
        return rulesets;
    }

    public static Ruleset getRuleset(int rulesetID) {
        for (Ruleset ruleset : rulesets) {
            if (ruleset.getId() == rulesetID) return ruleset;
        }

        return null; // Should never happen
    }

    /**
     * @return This ruleset only contains triggers, functions, and variables. No rules.
     */
    public static Ruleset getVanilla() {
        return vanilla;
    }

    public static Set<Integer> getCommandsChangedRulesets() {
        return commandsChangedRulesets;
    }

    public static DirectoryFile getDirectory(int directoryId) {
        for (Ruleset ruleset : rulesets) {
            DirectoryFile target = ruleset.getDirectory(directoryId);

            if (target != null) return target;
        }

        return null;
    }

    public static RuleFile getRule(int ruleId) {
        for (Ruleset ruleset : rulesets) {
            RuleFile target = ruleset.getRule(ruleId);

            if (target != null) return target;
        }

        return null;
    }

//    public static HashMap<String, RuleFile> nameRepository = new HashMap<String, RuleFile>();
//
//
//    public static void checkExistingRuleOverlap(String newId) {
//        idOverlaps.clear();
//        for (Map.Entry<String, RuleFile> entry : nameRepository.entrySet()) {
//            if (!entry.getValue().getRule().getId().equals(newId)) continue;
//            idOverlaps.add(entry.getValue().getRule().getId());
//        }
//    }
//
//    public static boolean checkRuleIdInRepo(String ruleId) {
//        for (Map.Entry<String, RuleFile> entry : nameRepository.entrySet()) {
//            if (entry.getKey().equals(ruleId)) return true;
//        }
//        return false;
//    }
//
//    public static void addRuleToRepo(RuleFile ruleFile) {
//        if (checkRuleIdInRepo(ruleFile.getRule().getId())) return;
//        nameRepository.put(ruleFile.getRule().getId(),ruleFile);
//    }

    public static void updateIdOverlaps() {
        idOverlaps.clear();
        for (Ruleset ruleset : rulesets) {
            updateIdOverlapsRecursive(ruleset.getRootDirectory());
        }
    }

    private static void updateIdOverlapsRecursive(DirectoryFile dir) {
        for (DirectoryFile branch : dir.getBranches()) {
            updateIdOverlapsRecursive(branch);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            boolean overlap = checkNameOverlap(leaf);
            if (overlap) idOverlaps.add(leaf.getRule().getId());
        }
    }

    public static boolean isIdOverlapped(RuleFile file) {
        if (!file.isRule()) return false;

        return idOverlaps.contains(file.getRule().getId());
    }

    public static boolean isIdenticalMatch(RuleFile file) {
        if (!file.isRule()) return false;

        Set<RuleFile> fileMatches = new HashSet<>();
        fileMatches.add(file);

        for (Ruleset ruleset : rulesets) {
            if (!isIdenticalMatches(file, ruleset.getRootDirectory(), fileMatches)) return false;
        }

        return true;
    }

    public static boolean isIdenticalMatches(RuleFile file, DirectoryFile directory, Set<RuleFile> matches) {
        for (DirectoryFile dir : directory.getBranches()) {
            if (!isIdenticalMatches(file, dir, matches)) return false;
        }

        for (RuleFile rule : directory.getLeaves()) {
            if (rule.isComment()) continue;
            if (rule.isSpacer()) continue;

            if (file.getName().equals(rule.getName())) {
                RuleBean a = file.getRule();
                RuleBean b = rule.getRule();

                if (!(a.getConditions().equals(b.getConditions())
                            && a.getScript().equals(b.getScript())
                            && a.getText().equals(b.getText())
                            && a.getTrigger().equals(b.getTrigger())
                            && a.getNotes().equals(b.getNotes()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean checkNameOverlap(RuleFile file) {
        if (!file.isRule()) return false;

        for (Ruleset ruleset : rulesets) {
            if (checkNameOverlapRecursive(file, ruleset.getRootDirectory())) return true;
        }

        return false;
    }

    private static boolean checkNameOverlapRecursive(RuleFile file, DirectoryFile directory) {
        for (DirectoryFile dir : directory.getBranches()) {
            if (checkNameOverlapRecursive(file, dir)) return true;
        }

        for (RuleFile rule : directory.getLeaves()) {
            if (!file.equals(rule) && rule.getName().equals(file.getName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkRulesetNameOverlap(Ruleset ruleset) {
        String name = ruleset.getName();

        // Check against loaded rulesets.
        for (Ruleset set : RulesetsManager.getRulesets()) {
            if (set.equals(ruleset)) continue;
            if (set.getName().equals(name))  {
                return true;
            }
        }

        // Assume this is correct, even though there are edge cases where it may not be.
        if (ruleset.hasOriginalName()) return false;

        // Check against saved rulesets.
        File folder = MainWindow.getInstance().getSettings().getSaveLocation();
        for (File file : folder.listFiles()) {
            if (file.getName().equals(name + ".json")) {
                return true;
            }
        }

        return false;
    }

    // <editor-fold desc="Undo/Redo" defaultstate="collapsed">
//    public static void backupTree() {
//        List<Ruleset> backupData = new ArrayList<>();
//        System.out.println("Backing Up");
//
//        long time = System.currentTimeMillis();
//
//        for (Ruleset ruleset : rulesets) {
//            Ruleset rootData = new Ruleset(ruleset);
//            backupData.add(rootData);
//
//            ruleset.setTimestamp(time);
//        }
//
//        if (backupData.isEmpty()) return;
//
//        treeUndoStack.push(backupData);
//        treeRedoStack.clear();
//    }

    public static void backupTree(RuleFile file) {
        List<RuleFile> list = new ArrayList<>();
        list.add(file);
        backupTree(list);
    }

    public static void backupTree(List<RuleFile> savedFiles) {
        for (RuleFile file : savedFiles) {
            backupRules.put(file.getId(), file);
        }

        List<Ruleset> backupData = new ArrayList<>();
        System.out.println("Backing Up");

        long time = System.currentTimeMillis();

        for (Ruleset ruleset : rulesets) {
            Ruleset backRuleset = new Ruleset(ruleset);
            stripRulesData(backRuleset);
            backupData.add(backRuleset);

            ruleset.setTimestamp(time);
        }

        if (backupData.isEmpty()) return;

        treeUndoStack.push(backupData);
        treeRedoStack.clear();
    }

    public static boolean canUndo() {
        return !treeUndoStack.empty();
    }

    public static void undo() {
        if (!canUndo()) {
            MainWindow.getInstance().refreshAllData();
            return;
        }

//        System.out.println("Undoing");

        for (Integer id : backupRules.keySet()) {
            RuleFile file = backupRules.get(id);
            if (file.isDirectory()) {
                updateRuleData(file, getDirectory(id));
            } else {
                updateRuleData(file, getRule(id));
            }
        }

        List<Ruleset> popRulesets = (List) treeUndoStack.pop();
        List<Ruleset> temp = new ArrayList<>();

        // Need to save added rules and/or update saved rules
        for (Ruleset undoRuleset : popRulesets) {
            for (Ruleset ruleset : rulesets) {
                if (undoRuleset.equals(ruleset)) {
                    Ruleset backRuleset = new Ruleset(ruleset);
                    stripRulesData(backRuleset);
                    temp.add(backRuleset);
                }
            }
        }

        boolean noChange = true;

        for (Ruleset undoRuleset : popRulesets) {
            for (Ruleset ruleset : rulesets) {
                if (undoRuleset.equals(ruleset)) {
                    restoreRulesData(ruleset, undoRuleset.getTreeBackup(), undoRuleset.getTimestamp());
                    noChange = false;
                    break;
                }
            }
        }

        RuleFile activeRule = getRule(RulesManager.getActiveRule().getId());
        if (activeRule == null) { // Can happen if an addition or copy is undone.
            RulesManager.setActiveRule(new RuleFile());
        } else {
            RulesManager.setActiveRule(activeRule);
        }

        treeRedoStack.push(temp);

        // If the rulesetData affected is no longer loaded, we still want to undo something,
        // so we want to keep trying until we succeed (or empty the stack).
        if (noChange) undo();
        else {
            MainWindow.getInstance().setSummaryLock(true);
            MainWindow.getInstance().refreshAllData();
            MainWindow.getInstance().setSummaryLock(false);
        }
    }

    public static boolean canRedo() {
        return !treeRedoStack.empty();
    }

    public static void redo() {
        if (!canRedo())  {
            MainWindow.getInstance().refreshAllData();
            return;
        }

//        System.out.println("Redoing");

        for (Integer id : backupRules.keySet()) {
            RuleFile file = backupRules.get(id);
            if (file.isDirectory()) {
                updateRuleData(file, getDirectory(id));
            } else {
                updateRuleData(file, getRule(id));
            }
        }

        List<Ruleset> popRulesets = (List) treeRedoStack.pop();
        List<Ruleset> temp = new ArrayList<>();

        for (Ruleset redoRuleset : popRulesets) {
            for (Ruleset ruleset : rulesets) {
                if (redoRuleset.equals(ruleset)) {
                    Ruleset backRuleset = new Ruleset(ruleset);
                    stripRulesData(backRuleset);
                    temp.add(backRuleset);
                }
            }
        }

        boolean noChange = true;

        for (Ruleset redoRuleset : popRulesets) {
            for (Ruleset ruleset : rulesets) {
                if (redoRuleset.equals(ruleset)) {
                    restoreRulesData(ruleset, redoRuleset.getTreeBackup(), redoRuleset.getTimestamp());
                    noChange = false;
                    break;
                }
            }
        }

        RuleFile activeRule = getRule(RulesManager.getActiveRule().getId());
        if (activeRule == null) { // Can happen if a deletion is redone.
            RulesManager.setActiveRule(new RuleFile());
        } else {
            RulesManager.setActiveRule(activeRule);
        }

        treeUndoStack.push(temp);

        // If the ruleset affected is no longer loaded, we still want to redo something,
        // so we want to keep trying until we succeed (or empty the stack).
        if (noChange) redo();
        else {
            MainWindow.getInstance().setSummaryLock(true);
            MainWindow.getInstance().refreshAllData();
            MainWindow.getInstance().setSummaryLock(false);
        }
    }

    private static void stripRulesData(Ruleset ruleset) {
        ruleset.getTreeBackup().clear();
        DirectoryFile root = ruleset.getRootDirectory();
        generateTreeBackupRecursive(root, ruleset.getTreeBackup());

        root.getBranches().clear();
        root.getLeaves().clear();
    }

    private static void restoreRulesData(Ruleset ruleset, Map<Integer, Map> restore, Long timestamp) {
        DirectoryFile root = new DirectoryFile(ruleset.getRootDirectory());
        restoreFilesRecursive(root, restore);

        ruleset.setRootDirectory(root);
        ruleset.setTimestamp(timestamp);

    }

    private static void generateTreeBackupRecursive(DirectoryFile dir, Map<Integer, Map> map) {
        for (DirectoryFile branch : dir.getBranches()) {
            Map<Integer, Map> bMap = new LinkedHashMap<>();
            map.put(branch.getId(), bMap);
            generateTreeBackupRecursive(branch, bMap);
        }

        for (RuleFile leaf : dir.getLeaves()) {
            map.put(leaf.getId(), null);
        }
    }

    private static void restoreFilesRecursive(DirectoryFile dir, Map<Integer, Map> map) {
        dir.getBranches().clear();
        dir.getLeaves().clear();

        for (Integer id : map.keySet()) {
            Map<Integer, Map> bMap = map.get(id);
            if (bMap != null) {
                DirectoryFile branch = getDirectory(id);
                if (branch == null) branch = (DirectoryFile) backupRules.get(id);
                branch = new DirectoryFile(branch); // New file so nothing gets broken

                dir.addBranch(branch);
                restoreFilesRecursive(branch, bMap);
            } else {
                RuleFile leaf = getRule(id);
                if (leaf == null) leaf = backupRules.get(id);
                dir.addLeaf(leaf);
            }
        }
    }

    // Still needed
    private static void updateRuleData(RuleFile target, RuleFile source) {
        if (source == null) return;

        target.getFromBlacklist().addAll(source.getFromBlacklist());
        target.getFromWhitelist().addAll(source.getFromWhitelist());
        target.getToBlacklist().addAll(source.getToBlacklist());
        target.getToWhitelist().addAll(source.getToWhitelist());

        RuleBean targetRule = target.getRule();
        RuleBean sourceRule = source.getRule();

        targetRule.setId(sourceRule.getId());
        targetRule.setTrigger(sourceRule.getTrigger());
        targetRule.setConditions(sourceRule.getConditions());
        targetRule.setScript(sourceRule.getScript());
        targetRule.setText(sourceRule.getText());
        targetRule.setOptions(sourceRule.getOptions());
        targetRule.setNotes(sourceRule.getNotes());
    }
    // </editor-fold>
}
