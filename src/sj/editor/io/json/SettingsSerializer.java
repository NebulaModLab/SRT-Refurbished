/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import sj.editor.data.Settings;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Refurbished)
 */
public class SettingsSerializer implements JsonSerializer<Settings> {

    public final static String ACTIVE_RULESETS = "activeRulesets";
    public final static String SAVE_LOC = "saveLocation";
    public final static String MODS_LOC = "modsLocation";
    public final static String SAFE_MODE = "safeMode";
    public final static String RESET_SIZE_LOC = "resetSizeLoc";
    public final static String RESET_DIVIDERS = "resetDividers";
    public final static String LANGUAGE = "language";
    public final static String THEME = "theme";

    @Override
    public JsonElement serialize(final Settings settings, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        final JsonElement jsonActiveRulesets = context.serialize(settings.getPreviousRulesets().toArray());
        jsonObject.add(ACTIVE_RULESETS, jsonActiveRulesets);

//        jsonObject.addProperty(SAVE_LOC, settings.getSaveLocation().toString());
        jsonObject.addProperty(MODS_LOC, settings.getModsLocation().toString());

        jsonObject.addProperty(SAFE_MODE, settings.isSafeMode());

        jsonObject.addProperty(RESET_SIZE_LOC, settings.doResetSizeLocation());

        jsonObject.addProperty(RESET_DIVIDERS, settings.doResetDividers());

        boolean spellcheckEnabled = settings.getLanguage() != null;
        if (spellcheckEnabled) jsonObject.addProperty(LANGUAGE, settings.getLanguage().getLanguage());

        // v3.0.0 - Adds theme property to the settings file - Purple Nebula
        if (settings.getLookAndFeel() != null) {
            jsonObject.addProperty(THEME, settings.getLookAndFeel().getName());
        }
        else {
            jsonObject.addProperty(THEME, "FlatLaf Dark");
        }

        return jsonObject;
    }
}