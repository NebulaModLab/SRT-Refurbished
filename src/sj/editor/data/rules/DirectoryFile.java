/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.rules;

import java.util.*;
import sj.editor.data.RulesetsManager;

/**
 * @author SafariJohn
 */
public class DirectoryFile extends RuleFile {
    private final List<DirectoryFile> branches;
    private final List<RuleFile> leaves;

    public DirectoryFile() {
        super();

        this.branches = new ArrayList<>();
        this.leaves = new ArrayList<>();
    }

    public DirectoryFile(RuleBean rule) {
        super(rule);

        this.branches = new ArrayList<>();
        this.leaves = new ArrayList<>();
    }

    /**
     * Deep copies the directory, all its rules, and (cascading) all its subdirectories to new objects.
     * The new objects have the same file IDs as the old ones.
     * @param file The directory to copy.
     */
    public DirectoryFile(DirectoryFile file) {
        this(file, true);
    }

    /**
     * Deep copies the directory, all its rules, and (cascading) all its subdirectories to new objects.
     * @param file The directory to copy.
     * @param copyId Whether to copy the file ID or get a new one.
     */
    public DirectoryFile(DirectoryFile file, boolean copyId) {
        super(file, copyId);

        branches = new ArrayList<>();
        for (DirectoryFile dir : file.getBranches()) {
            DirectoryFile newDir = new DirectoryFile(dir, copyId);
            newDir.setParentId(getId());

            branches.add(newDir);
        }

        leaves = new ArrayList<>();
        for (RuleFile rule : file.getLeaves()) {
            RuleFile newRule = new RuleFile(rule, copyId);
            if (!copyId) newRule.setParentId(getId());

            leaves.add(newRule);
        }
    }

    @Override
    public void setRulesetId(int rulesetId) {
        super.setRulesetId(rulesetId);

        for (DirectoryFile dir : branches) {
            dir.setRulesetId(rulesetId);
        }

        for (RuleFile rule : leaves) {
            rule.setRulesetId(rulesetId);
        }
    }

    public boolean isSaved(long saveTime) {
        if (getTimestamp() != saveTime) return false;

        for (DirectoryFile dir : branches) {
            if (!dir.isSaved(saveTime)) return false;
        }

        for (RuleFile rule : leaves) {
            if (rule.getTimestamp() != saveTime) return false;
        }

        return true;
    }

    public void setTimestampCascade(long timestamp) {
        super.setTimestamp(timestamp);

        for (DirectoryFile dir : branches) {
            dir.setTimestampCascade(timestamp);
        }

        for (RuleFile rule : leaves) {
            rule.setTimestamp(timestamp);
        }
    }

    public List<DirectoryFile> getBranches() {
        return branches;
    }

    public void addBranch(DirectoryFile branch) {
        addBranch(branch, branches.size());
    }

    public void addBranch(DirectoryFile branch, int position) {
        if (branch.getParentId() >= 0) {
            DirectoryFile parent = RulesetsManager.getRuleset(branch.getRulesetId())
                        .getDirectory(branch.getParentId());
            if (parent != null) parent.removeBranch(branch);
        }

        branch.setParentId(getId());
        branch.setRulesetId(getRulesetId());
        branches.add(position, branch);
    }

    public void removeBranch(DirectoryFile branch) {
        branch.setParentId(-1);
        branches.remove(branch);
    }

    public List<RuleFile> getLeaves() {
        return leaves;
    }

    public void addLeaf(RuleFile leaf) {
        addLeaf(leaf, leaves.size());
    }

    public void addLeaf(RuleFile leaf, int position) {
        if (leaf.getParentId() >= 0) {
            DirectoryFile parent = RulesetsManager.getRuleset(leaf.getRulesetId())
                        .getDirectory(leaf.getParentId());
            if (parent != null) parent.removeLeaf(leaf);
        }

        leaf.setParentId(getId());
        leaf.setRulesetId(getRulesetId());
        leaves.add(position, leaf);
    }

    public void removeLeaf(RuleFile leaf) {
        leaf.setParentId(-1);
        leaves.remove(leaf);
    }

    @Override
    public boolean isComment() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isSpacer() {
        return false;
    }
}
