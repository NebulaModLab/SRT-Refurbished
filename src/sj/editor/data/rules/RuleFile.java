/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.rules;

import java.util.*;
import sj.editor.data.InternalFile;

/**
 * @author SafariJohn
 */
public class RuleFile extends InternalFile {
    private final RuleBean rule;

    private final List<String> fromBlacklist;
    private final List<String> fromWhitelist;
    private final List<String> toBlacklist;
    private final List<String> toWhitelist;

    private int rulesetId;
    private int parentId;

    public RuleFile() {
        super();
        rule = new RuleBean();

        fromBlacklist = new ArrayList<>();
        fromWhitelist = new ArrayList<>();
        toBlacklist = new ArrayList<>();
        toWhitelist = new ArrayList<>();

        rulesetId = -1;
        parentId = -1;
    }

    public RuleFile(RuleBean rule) {
        super();
        this.rule = new RuleBean(rule);

        fromBlacklist = new ArrayList<>();
        fromWhitelist = new ArrayList<>();
        toBlacklist = new ArrayList<>();
        toWhitelist = new ArrayList<>();

        rulesetId = -1;
        parentId = -1;
    }

    public RuleFile(RuleFile file) {
        this(file, true);
    }

    public RuleFile(RuleFile file, boolean copyId) {
        super(file, copyId);
        rule = new RuleBean(file.getRule());

        fromBlacklist = new ArrayList<>();
        for (String fileId : file.fromBlacklist) {
            fromBlacklist.add(fileId);
        }

        fromWhitelist = new ArrayList<>();
        for (String fileId : file.fromWhitelist) {
            fromWhitelist.add(fileId);
        }

        toBlacklist = new ArrayList<>();
        for (String fileId : file.toBlacklist) {
            toBlacklist.add(fileId);
        }

        toWhitelist = new ArrayList<>();
        for (String fileId : file.toWhitelist) {
            toWhitelist.add(fileId);
        }

        rulesetId = file.getRulesetId();
        if (copyId) parentId = file.getParentId();
        else parentId = -1;
    }

    public String getName() {
        if (isComment()) return "COMMENT";
        if (isSpacer()) return "SPACER";

        return rule.getId();
    }

    public RuleBean getRule() {
        return rule;
    }

    public int getRulesetId() {
        return rulesetId;
    }

    public void setRulesetId(int rulesetId) {
        this.rulesetId = rulesetId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<String> getFromBlacklist() {
        return fromBlacklist;
    }

    public List<String> getFromWhitelist() {
        return fromWhitelist;
    }

    public List<String> getToBlacklist() {
        return toBlacklist;
    }

    public List<String> getToWhitelist() {
        return toWhitelist;
    }

    public boolean isComment() {
        return rule.isComment();
    }

    public boolean isDirectory() {
        return false;
    }

    public boolean isRule() {
        return !(isDirectory() || isComment() || isSpacer());
    }

    public boolean isSpacer() {
        return rule.isSpacer();
    }

    @Override
    public String toString() {
        return getName();
    }
}
