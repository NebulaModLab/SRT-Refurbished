/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import java.util.List;

/**
 * @author SafariJohn (original SRT)
 */
public class ForceBlockContainer {
    private final String ruleId;

    private final List<String> fromBlacklist;
    private final List<String> fromWhitelist;
    private final List<String> toBlacklist;
    private final List<String> toWhitelist;

    public ForceBlockContainer(String ruleId, List<String> fromBlacklist,
                List<String> fromWhitelist, List<String> toBlacklist,
                List<String> toWhitelist) {

        this.ruleId = ruleId;
        
        this.fromBlacklist = fromBlacklist;
        this.fromWhitelist = fromWhitelist;
        this.toBlacklist = toBlacklist;
        this.toWhitelist = toWhitelist;
    }

    public String getRuleId() {
        return ruleId;
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
}
