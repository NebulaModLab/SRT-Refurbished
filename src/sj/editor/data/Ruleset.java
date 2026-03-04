/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import java.io.File;
import java.util.*;
import sj.editor.data.commands.Command;
import sj.editor.data.rules.DirectoryFile;
import sj.editor.data.rules.RuleFile;

/**
 * Author: SafariJohn
 */
public class Ruleset extends InternalFile {
    private String oName;

    private DirectoryFile rootDirectory;
    private Map<Integer, Map> treeBackup;
    private File saveLocation;
    private final List<String> triggers;
    private final List<Command> commands;
    private final List<String> cFunctions;
    private final List<String> cVariables;
    private final List<String> sFunctions;
    private final List<String> sVariables;
    private final List<String> tVariables;
    private final List<String> tempTriggers;
    private final List<String> tempCVariables;
    private final List<String> tempSVariables;
    private final List<String> tempTVariables;

    private long saveTime;

    public Ruleset(String name) {
        super();

        this.oName = "";

        this.rootDirectory = new DirectoryFile();
        this.rootDirectory.setRulesetId(getId());
        this.rootDirectory.getRule().setId(name);

        treeBackup = new LinkedHashMap<>();

        this.saveLocation = new File("");

        this.triggers = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.cFunctions = new ArrayList<>();
        this.cVariables = new ArrayList<>();
        this.sFunctions = new ArrayList<>();
        this.sVariables = new ArrayList<>();
        this.tVariables = new ArrayList<>();
        this.tempTriggers = new ArrayList<>();
        this.tempCVariables = new ArrayList<>();
        this.tempSVariables = new ArrayList<>();
        this.tempTVariables = new ArrayList<>();

        saveTime = 0;
    }

    @Deprecated
    public Ruleset(String name, List<String> triggers, List<String> cFunctions, List<String> cVariables,
                List<String> sFunctions, List<String> sVariables, List<String> tVariables) {
        super();

        this.rootDirectory = new DirectoryFile();
        this.rootDirectory.setRulesetId(getId());
        this.rootDirectory.getRule().setId(name);

        treeBackup = new LinkedHashMap<>();

        this.saveLocation = new File("");

        this.triggers = triggers;
        this.commands = new ArrayList<>();
        this.cFunctions = cFunctions;
        this.cVariables = cVariables;
        this.sFunctions = sFunctions;
        this.sVariables = sVariables;
        this.tVariables = tVariables;
        this.tempTriggers = new ArrayList<>();
        this.tempCVariables = new ArrayList<>();
        this.tempSVariables = new ArrayList<>();
        this.tempTVariables = new ArrayList<>();

        saveTime = 0;
    }

    public Ruleset(Ruleset ruleset) {
        super(ruleset);

        this.oName = ruleset.getOriginalName();

        this.rootDirectory = new DirectoryFile(ruleset.getRootDirectory());
        this.rootDirectory.setRulesetId(getId());

        treeBackup = new LinkedHashMap<>(ruleset.getTreeBackup());

        this.saveLocation = ruleset.getSaveLocation();

        this.triggers = new ArrayList<>();
        this.triggers.addAll(ruleset.getTriggers());

        this.commands = new ArrayList<>();
        for (Command com : ruleset.getCommands()) {
            this.commands.add(new Command(com, getId()));
        }

        this.cFunctions = new ArrayList<>();
        this.cFunctions.addAll(ruleset.getCFunctions());

        this.cVariables = new ArrayList<>();
        this.cVariables.addAll(ruleset.getCVariables());

        this.sFunctions = new ArrayList<>();
        this.sFunctions.addAll(ruleset.getSFunctions());

        this.sVariables = new ArrayList<>();
        this.sVariables.addAll(ruleset.getSVariables());

        this.tVariables = new ArrayList<>();
        this.tVariables.addAll(ruleset.getTVariables());

        this.tempTriggers = new ArrayList<>();
        this.tempTriggers.addAll(ruleset.getTempTriggers());

        this.tempCVariables = new ArrayList<>();
        this.tempCVariables.addAll(ruleset.getTempCVariables());

        this.tempSVariables = new ArrayList<>();
        this.tempSVariables.addAll(ruleset.getTempSVariables());

        this.tempTVariables = new ArrayList<>();
        this.tempTVariables.addAll(ruleset.getTempTVariables());

        saveTime = ruleset.getSaveTime();
    }

    public String getOriginalName() {
        return oName;
    }

    public void setOriginalName(String name) {
        this.oName = name;
    }

    public boolean hasOriginalName() {
        return oName.equals(rootDirectory.getName());
    }

    public boolean isSaved() {
        if (saveTime != getTimestamp()) return false;

        if (RulesetsManager.getCommandsChangedRulesets().contains(getId())) return false;
        if (hasTempTriggersVariables()) return false;

        return rootDirectory.isSaved(saveTime);
    }

    private boolean hasTempTriggersVariables() {
        boolean empty = tempTriggers.isEmpty();
        empty &= tempCVariables.isEmpty();
        empty &= tempSVariables.isEmpty();
        empty &= tempTVariables.isEmpty();

        return !empty;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public DirectoryFile getRootDirectory() {
        return rootDirectory;
    }

    public Map<Integer, Map> getTreeBackup() {
        return treeBackup;
    }

//    public void setTreeBackup(Map<Integer, Map> treeBackup) {
//        this.treeBackup = treeBackup;
//    }

    public File getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(File saveLocation) {
        if (saveLocation == null) this.saveLocation = new File("");
        else this.saveLocation = saveLocation;
    }

    public void setRootDirectory(DirectoryFile rootDirectory) {
        this.rootDirectory = rootDirectory;
        rootDirectory.setRulesetId(getId());
    }

    public DirectoryFile getDirectory(int directoryId) {
        if (rootDirectory.getId() == directoryId) return rootDirectory;

        for (DirectoryFile dir : rootDirectory.getBranches()) {
            if (dir.getId() == directoryId) return dir;
        }

        DirectoryFile target;

        for (DirectoryFile dir : rootDirectory.getBranches()) {
            target = searchForDirectory(dir, directoryId);

            if (target != null) return target;
        }

        return null;
    }

    private DirectoryFile searchForDirectory(DirectoryFile directory, int targetDirectoryId) {
        for (DirectoryFile dir : directory.getBranches()) {
            if (dir.getId() == targetDirectoryId) return dir;
        }

        DirectoryFile target;

        for (DirectoryFile searchDir : directory.getBranches()) {
            target = searchForDirectory(searchDir, targetDirectoryId);

            if (target != null) return target;
        }

        return null;
    }

    public RuleFile getRule(int ruleId) {
        for (RuleFile file : rootDirectory.getLeaves()) {
            if (file.getId() == ruleId) return file;
        }

        RuleFile target;

        for (DirectoryFile dir : rootDirectory.getBranches()) {
            target = searchForRule(dir, ruleId);

            if (target != null) return target;
        }

        // Can't find a rule? Check if it is a directory.
        return getDirectory(ruleId);
    }

    private RuleFile searchForRule(DirectoryFile directory, int ruleId) {

        for (RuleFile file : directory.getLeaves()) {
            if (file.getId() == ruleId) return file;
        }

        RuleFile target;

        for (DirectoryFile searchDir : directory.getBranches()) {
            target = searchForRule(searchDir, ruleId);

            if (target != null) return target;
        }

        return null;
    }

    /**
     * @return The root node's ID.
     */
    public String getName() {
        return rootDirectory.getName();
    }

    /**
     * Sets the root node's ID to name.
     * @param name
     */
    public void setName(String name) {
        rootDirectory.getRule().setId(name);
    }

    public List<String> getTriggers() {
        return triggers;
    }

    public List<Command> getCommands() {
        return commands;
    }

    @Deprecated
    public List<String> getCFunctions() {
        return cFunctions;
    }

    public List<String> getCVariables() {
        return cVariables;
    }

    @Deprecated
    public List<String> getSFunctions() {
        return sFunctions;
    }

    public List<String> getSVariables() {
        return sVariables;
    }

    public List<String> getTVariables() {
        return tVariables;
    }

    public List<String> getTempTriggers() {
        return tempTriggers;
    }

    public List<String> getTempCVariables() {
        return tempCVariables;
    }

    public List<String> getTempSVariables() {
        return tempSVariables;
    }

    public List<String> getTempTVariables() {
        return tempTVariables;
    }

    @Override
    public String toString() {
        return rootDirectory.getName();
    }
}
