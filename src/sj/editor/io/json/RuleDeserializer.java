/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.json;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.*;
import sj.editor.data.rules.*;
import static sj.editor.io.json.RuleSerializer.*;

/**
 * Author: SafariJohn
 */
public class RuleDeserializer implements JsonDeserializer<RuleFile> {

    @Override
    public RuleFile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Deserialize RuleBean
        String id = "ERROR";
        String trigger = "";
        String conditions = "";
        String script = "";
        String text = "";
        String options = "";
        String notes = "";

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get(ID) != null) {
            id = jsonObject.get(ID).getAsString();
        }

        if (jsonObject.get(TRIGGER) != null) {
            trigger = jsonObject.get(TRIGGER).getAsString();
        }

        if (jsonObject.get(CONDITIONS) != null) {
            conditions = jsonObject.get(CONDITIONS).getAsString();
        }

        if (jsonObject.get(SCRIPT) != null) {
            script = jsonObject.get(SCRIPT).getAsString();
        }

        if (jsonObject.get(TEXT) != null) {
            text = jsonObject.get(TEXT).getAsString();
        }

        if (jsonObject.get(OPTIONS) != null) {
            options = jsonObject.get(OPTIONS).getAsString();
        }

        if (jsonObject.get(NOTES) != null) {
            notes = jsonObject.get(NOTES).getAsString();
        }

        final RuleBean rule = new RuleBean();
        rule.setId(id);
        rule.setTrigger(trigger);
        rule.setConditions(conditions);
        rule.setScript(script);
        rule.setText(text);
        rule.setOptions(options);
        rule.setNotes(notes);

        // Deserialize black/whitelists
        List<String> fromBlacklist = new ArrayList<>();
        List<String> fromWhitelist = new ArrayList<>();
        List<String> toBlacklist = new ArrayList<>();
        List<String> toWhitelist = new ArrayList<>();


        if (jsonObject.get(FROM_BLOCKED) != null) {
            final JsonArray jsonTriggersArray = jsonObject.get(FROM_BLOCKED).getAsJsonArray();
            for (JsonElement element : jsonTriggersArray) {
                fromBlacklist.add(element.getAsString());
            }
        }

        if (jsonObject.get(FROM_FORCED) != null) {
            final JsonArray jsonTriggersArray = jsonObject.get(FROM_FORCED).getAsJsonArray();
            for (JsonElement element : jsonTriggersArray) {
                fromWhitelist.add(element.getAsString());
            }
        }

        if (jsonObject.get(TO_BLOCKED) != null) {
            final JsonArray jsonTriggersArray = jsonObject.get(TO_BLOCKED).getAsJsonArray();
            for (JsonElement element : jsonTriggersArray) {
                toBlacklist.add(element.getAsString());
            }
        }

        if (jsonObject.get(TO_FORCED) != null) {
            final JsonArray jsonTriggersArray = jsonObject.get(TO_FORCED).getAsJsonArray();
            for (JsonElement element : jsonTriggersArray) {
                toWhitelist.add(element.getAsString());
            }
        }


        final RuleFile file = new RuleFile(rule);

        file.getFromBlacklist().addAll(fromBlacklist);
        file.getFromWhitelist().addAll(fromWhitelist);
        file.getToBlacklist().addAll(toBlacklist);
        file.getToWhitelist().addAll(toWhitelist);

        return file;
    }

}
