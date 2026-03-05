/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import java.util.*;
import sj.editor.data.rules.RuleFile;

/**
 * @author SafariJohn
 */
public class SearchResult {
    public static enum Column {
        ID,
        TRIGGER,
        CONDITIONS,
        SCRIPT,
        TEXT,
        OPTIONS,
        NOTES
    }

    private RuleFile file;
    private Column column;
    private final List<Integer> indexes;

    public SearchResult(RuleFile rule, Column column) {
        this.file = rule;
        this.column = column;
        indexes = new ArrayList<>();
    }

    public SearchResult(SearchResult result) {
        this.file = result.getRuleFile();
        this.column = result.getColumn();
        indexes = new ArrayList<>(result.getIndexes());
    }

    public RuleFile getRuleFile() {
        return file;
    }

    public void setRuleFile(RuleFile file) {
        this.file = file;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public List<Integer> getIndexes() {
        return indexes;
    }
}
