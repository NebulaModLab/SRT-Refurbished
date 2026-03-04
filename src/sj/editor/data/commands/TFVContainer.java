/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.commands;

import sj.editor.data.RulesetsManager;

/**
 * Author: SafariJohn
 */
public class TFVContainer {
    private final String tfv;
    private final int rulesetId;

    public TFVContainer(String tfv, int rulesetId) {
        this.tfv = tfv;
        this.rulesetId = rulesetId;
    }

    public String getTFV() {
        return tfv;
    }

    public int getRulesetId() {
        return rulesetId;
    }

    @Override
    public String toString() {
        if (rulesetId == RulesetsManager.getVanilla().getId()) return tfv;
        else return "(" + RulesetsManager.getRuleset(rulesetId).getName() + ") " + tfv;
    }
}
