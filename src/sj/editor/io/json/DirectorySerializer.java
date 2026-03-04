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
 * Author: SafariJohn
 */
public class DirectorySerializer implements JsonSerializer<DirectoryFile> {

  @Override
  public JsonElement serialize(final DirectoryFile file, final Type typeOfSrc, final JsonSerializationContext context) {
    final JsonObject jsonObject = new JsonObject();
    RuleBean rule = file.getRule();

    // Serialize RuleBean
    jsonObject.addProperty(RuleSerializer.ID, rule.getId());
    jsonObject.addProperty(RuleSerializer.TRIGGER, rule.getTrigger());
    jsonObject.addProperty(RuleSerializer.CONDITIONS, rule.getConditions());
    jsonObject.addProperty(RuleSerializer.SCRIPT, rule.getScript());
    jsonObject.addProperty(RuleSerializer.TEXT, rule.getText());
    jsonObject.addProperty(RuleSerializer.OPTIONS, rule.getOptions());
    jsonObject.addProperty(RuleSerializer.NOTES, rule.getNotes());

    // Serialize black/whitelists
    final JsonElement jsonFromBL = context.serialize(file.getFromBlacklist().toArray());
    jsonObject.add(RuleSerializer.FROM_BLOCKED, jsonFromBL);
    final JsonElement jsonFromWL = context.serialize(file.getFromWhitelist().toArray());
    jsonObject.add(RuleSerializer.FROM_FORCED, jsonFromWL);
    final JsonElement jsonToBL = context.serialize(file.getToBlacklist().toArray());
    jsonObject.add(RuleSerializer.TO_BLOCKED, jsonToBL);
    final JsonElement jsonToWL = context.serialize(file.getToWhitelist().toArray());
    jsonObject.add(RuleSerializer.TO_FORCED, jsonToWL);


    return jsonObject;
  }
}