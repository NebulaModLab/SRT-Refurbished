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
import sj.editor.data.rules.*;

/**
 * @author SafariJohn
 */
public class RuleSerializer implements JsonSerializer<RuleFile> {
    // RuleBean keys
    public static final String ID = "id";
    public static final String TRIGGER = "trigger";
    public static final String CONDITIONS = "conditions";
    public static final String SCRIPT = "script";
    public static final String TEXT = "text";
    public static final String OPTIONS = "options";
    public static final String NOTES = "notes";

    // Black/whitelist keys
    public static final String FROM_BLOCKED = "fromBlocked";
    public static final String FROM_FORCED = "fromForced";
    public static final String TO_BLOCKED = "toBlocked";
    public static final String TO_FORCED = "toForced";

    @Override
    public JsonElement serialize(final RuleFile file, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        RuleBean rule = file.getRule();

        // Serialize RuleBean
        jsonObject.addProperty(ID, rule.getId());
        jsonObject.addProperty(TRIGGER, rule.getTrigger());
        jsonObject.addProperty(CONDITIONS, rule.getConditions());
        jsonObject.addProperty(SCRIPT, rule.getScript());
        jsonObject.addProperty(TEXT, rule.getText());
        jsonObject.addProperty(OPTIONS, rule.getOptions());
        jsonObject.addProperty(NOTES, rule.getNotes());

        // Serialize black/whitelists
        final JsonElement jsonFromBL = context.serialize(file.getFromBlacklist().toArray());
        jsonObject.add(FROM_BLOCKED, jsonFromBL);
        final JsonElement jsonFromWL = context.serialize(file.getFromWhitelist().toArray());
        jsonObject.add(FROM_FORCED, jsonFromWL);
        final JsonElement jsonToBL = context.serialize(file.getToBlacklist().toArray());
        jsonObject.add(TO_BLOCKED, jsonToBL);
        final JsonElement jsonToWL = context.serialize(file.getToWhitelist().toArray());
        jsonObject.add(TO_FORCED, jsonToWL);


        return jsonObject;
    }
}