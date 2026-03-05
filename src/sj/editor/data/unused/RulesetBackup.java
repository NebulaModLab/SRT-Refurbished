/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.unused;

import java.util.*;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;

/**
 * @author SafariJohn
 */
public class RulesetBackup {
    private final List<Ruleset> rulesets;
    private final RulesetsManager.DataType changeType;

    private long timestamp;

    /**
     * This class guarantees all {@link sj.editor.data.Ruleset Ruleset}
     * variables passed to it are deep copied.
     *
     * @param rulesets The stored rulesets.
     * @param changeType The type of change that caused this backup.
     */
    public RulesetBackup(List<Ruleset> rulesets, RulesetsManager.DataType changeType) {
        this.rulesets = new ArrayList<>();
        for (Ruleset ruleset : rulesets) {
            this.rulesets.add(new Ruleset(ruleset));
        }

        this.changeType = changeType;

        timestamp = System.currentTimeMillis();
    }

    public RulesetBackup(Ruleset ruleset, RulesetsManager.DataType changeType) {
        this.rulesets = new ArrayList<>();
        this.rulesets.add(new Ruleset(ruleset));

        this.changeType = changeType;

        timestamp = System.currentTimeMillis();
    }

    public List<Ruleset> getRulesets() {
        return rulesets;
    }

    public RulesetsManager.DataType getChangeType() {
        return changeType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
