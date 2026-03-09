/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.util.prefs.Preferences;

/**
 * @author SafariJohn (original SRT)
 */
public interface SRTInterface {
    public void getPreferences(Preferences prefs);
    public void setPreferences(Preferences prefs);

    public void refreshInterface();
}
