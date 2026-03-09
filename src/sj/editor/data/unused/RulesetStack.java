/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.unused;

import java.util.*;
import sj.editor.data.FileStack;
import sj.editor.data.RulesetsManager;

/**
 * @author SafariJohn (original SRT)
 */
public class RulesetStack extends FileStack {

    public RulesetStack(int fileId) {
        super(fileId);
    }

    public void clear(RulesetsManager.DataType type) {
        List<RulesetBackup> toRemove = new ArrayList<>();

        for (int i = 0; i < size(); i++) {
            RulesetBackup backup = (RulesetBackup) get(i);
            if (backup.getChangeType() == type) toRemove.add(backup);
        }

        removeAll(toRemove);
    }
}
