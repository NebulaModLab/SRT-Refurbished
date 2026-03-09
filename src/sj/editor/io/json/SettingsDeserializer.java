/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.json;

import com.google.gson.*;
import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import sj.editor.data.Settings;

import javax.swing.*;

import static sj.editor.io.json.SettingsSerializer.*;

/**
 * @author SafariJohn (original SRT)
 */
public class SettingsDeserializer implements JsonDeserializer<Settings> {

    @Override
    public Settings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List<String> activeRulesets = new ArrayList<>();
        File saveLocation = new File("rulesets");
        File modsLocation = null;
        boolean safeMode = false;
        boolean resetSizeLoc = false;
        boolean resetDividers = false;
        String language = null;
        String themeName = null;

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get(ACTIVE_RULESETS) != null) {
            for (JsonElement element : jsonObject.get(ACTIVE_RULESETS).getAsJsonArray()) {
                activeRulesets.add(element.getAsString());
            }
        }

//        if (jsonObject.get(SAVE_LOC) != null) {
//            saveLocation = new File(jsonObject.get(SAVE_LOC).getAsString());
//        }

        if (jsonObject.get(MODS_LOC) != null) {
            modsLocation = new File(jsonObject.get(MODS_LOC).getAsString());
        }

        if (jsonObject.get(SAFE_MODE) != null) {
            safeMode = jsonObject.get(SAFE_MODE).getAsBoolean();
        }

        if (jsonObject.get(RESET_SIZE_LOC) != null) {
            resetSizeLoc = jsonObject.get(RESET_SIZE_LOC).getAsBoolean();
        }

        if (jsonObject.get(RESET_DIVIDERS) != null) {
            resetDividers = jsonObject.get(RESET_DIVIDERS).getAsBoolean();
        }

        if (jsonObject.get(LANGUAGE) != null) {
            language = jsonObject.get(LANGUAGE).getAsString();
            if (language.length() != 2) language = Locale.ENGLISH.getLanguage();
        }

        if (jsonObject.get(THEME) != null) {
            themeName = jsonObject.get(THEME).getAsString();
        }
        else {
            themeName = "FlatLaf Dark";
        }


        Settings settings = new Settings();
        settings.getPreviousRulesets().addAll(activeRulesets);
        settings.setSaveLocation(saveLocation);
        settings.setModsLocation(modsLocation);
        settings.setSafeMode(safeMode);
        settings.setResetSizeLocation(resetSizeLoc);
        settings.setResetDividers(resetDividers);
        if (language == null) settings.setLanguage(null);
        else settings.setLanguage(new Locale(language));

        for (LookAndFeel lookAndFeel : settings.getLookAndFeels()) {
            if (!lookAndFeel.getName().equals(themeName)) continue;
            settings.setLookAndFeel(lookAndFeel);
        }

        return settings;
    }

}
