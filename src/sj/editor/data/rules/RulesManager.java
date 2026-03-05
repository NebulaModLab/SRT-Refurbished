/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.rules;

import java.util.ArrayList;
import sj.editor.MainWindow;
import sj.editor.data.FileStack;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;

/**
 * @author SafariJohn
 */
public class RulesManager {
    private static final ArrayList<FileStack> undoArray = new ArrayList();
    private static final ArrayList<FileStack> redoArray = new ArrayList();

    private static RuleFile activeRule = new RuleFile();

    private static int backupId = -1;
    private static long changeTime = 0;

    public static RuleFile getActiveRule() {
        return activeRule;
    }

    public static void setActiveRule(RuleFile rule) {
        if (rule == null) activeRule = new RuleFile();
        else activeRule = rule;
    }

    // <editor-fold desc="Undo/Redo" defaultstate="collapsed">
    private static void createUndoRedoStacks(RuleFile file) {
        if (file.getRulesetId() < 0) return;

        boolean found = false;
        for (FileStack stack : undoArray) {
            if (stack.getFileId() == file.getId()) {
                found = true;
                break;
            }
        }

        if (!found) {
            undoArray.add(new FileStack(file.getId()));
            redoArray.add(new FileStack(file.getId()));
        }
    }

    public static boolean doBackup(int fileId) {
        if (backupId != fileId) {
            backupId = fileId;
            return true;
        }

        boolean backup;

        backup = System.currentTimeMillis() > changeTime;
        changeTime = System.currentTimeMillis() + 1000;

        return backup;
    }

    public static void backupActiveFile() {
        if (activeRule.getRulesetId() < 0) return;

        createUndoRedoStacks(activeRule);

        RuleFile backup;
        if (activeRule.isDirectory()) backup = new DirectoryFile((DirectoryFile) activeRule);
        else backup = new RuleFile(activeRule);

        activeRule.setTimestamp(System.currentTimeMillis());

        // Trim branches and leaves from backup
        if (backup.isDirectory()) {
            DirectoryFile backupDir = (DirectoryFile) backup;

            backupDir.getBranches().clear();
            backupDir.getLeaves().clear();
        }

        for (FileStack stack : undoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                System.out.println("Backing Up");
                stack.push(backup);
                break;
            }
        }

        for (FileStack stack : redoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                stack.clear();
                break;
            }
        }
    }

    public static boolean canUndo() {
        for (FileStack stack : undoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                return !stack.isEmpty();
            }
        }

        return false;
    }

    public static void undo() {
        if (activeRule.getRulesetId() < 0) return;
        if (!canUndo()) return;

        RuleFile temp = activeRule;
        for (FileStack stack : undoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                if (stack.isEmpty()) return;
//                System.out.println("Undoing");

                activeRule = (RuleFile) stack.pop();
                break;
            }
        }

        // Add branches and leaves to pop, then trim them from temp
        if (activeRule.isDirectory()) {
            DirectoryFile activeDir = (DirectoryFile) activeRule;
            DirectoryFile tempDir = (DirectoryFile) temp;

            activeDir.getBranches().addAll(tempDir.getBranches());
            activeDir.getLeaves().addAll(tempDir.getLeaves());

            tempDir.getBranches().clear();
            tempDir.getLeaves().clear();
        }

        for (FileStack stack : redoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                stack.push(temp);
                break;
            }
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            RuleFile file = ruleset.getRule(activeRule.getId());
            if (file == null) continue;
            if (file.getParentId() < 0) {
                ruleset.setRootDirectory((DirectoryFile) activeRule);
                break;
            }

            DirectoryFile directory = ruleset.getDirectory(file.getParentId());

            if (file.isDirectory()) {
                int i = directory.getBranches().indexOf(file);
                directory.removeBranch((DirectoryFile) file);
                directory.addBranch((DirectoryFile) activeRule, i);
            } else {
                int i = directory.getLeaves().indexOf(file);
                directory.removeLeaf(file);
                directory.addLeaf(activeRule, i);
            }

            break;
        }

        if (temp.isRule() && !temp.getName().equals(activeRule.getName())) RulesetsManager.updateIdOverlaps();

        MainWindow.getInstance().refreshAllData();
    }

    public static boolean canRedo() {
        for (FileStack stack : redoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                return !stack.isEmpty();
            }
        }

        return false;
    }

    public static void redo() {
        if (activeRule.getRulesetId() < 0) return;

        RuleFile temp = activeRule;
        for (FileStack stack : redoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                if (stack.isEmpty()) return;
//                System.out.println("Redoing");

                activeRule = (RuleFile) stack.pop();
                break;
            }
        }

        // Add branches and leaves to pop, then trim them from temp
        if (activeRule.isDirectory()) {
            DirectoryFile activeDir = (DirectoryFile) activeRule;
            DirectoryFile tempDir = (DirectoryFile) temp;

            activeDir.getBranches().addAll(tempDir.getBranches());
            activeDir.getLeaves().addAll(tempDir.getLeaves());

            tempDir.getBranches().clear();
            tempDir.getLeaves().clear();
        }

        for (FileStack stack : undoArray) {
            if (stack.getFileId() == activeRule.getId()) {
                stack.push(temp);
                break;
            }
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            RuleFile file = ruleset.getRule(activeRule.getId());
            if (file == null) continue;
            if (file.getParentId() < 0) {
                ruleset.setRootDirectory((DirectoryFile) activeRule);
                break;
            }

            DirectoryFile directory = ruleset.getDirectory(file.getParentId());

            if (file.isDirectory()) {
                int i = directory.getBranches().indexOf(file);
                directory.removeBranch((DirectoryFile) file);
                directory.addBranch((DirectoryFile) activeRule, i);
            } else {
                int i = directory.getLeaves().indexOf(file);
                directory.removeLeaf(file);
                directory.addLeaf(activeRule, i);
            }

            break;
        }

        if (temp.isRule() && !temp.getName().equals(activeRule.getName())) RulesetsManager.updateIdOverlaps();

        MainWindow.getInstance().refreshAllData();
    }
    // </editor-fold>
}
