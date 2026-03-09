/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.io.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import sj.editor.data.Ruleset;
import sj.editor.data.rules.DirectoryFile;
import sj.editor.data.rules.RuleFile;

/**
 * @author SafariJohn (original SRT)
 */
public class RulesetSerializer implements JsonSerializer<Ruleset> {
    // Keys
    public static final String NAME = "name";
    public static final String SAVE_LOC = "saveLocation";
    public static final String TRIGGERS = "triggers";
    public static final String COMMANDS = "commands";
    public static final String CFUNCTIONS = "cFunctions";
    public static final String CVARIABLES = "cVariables";
    public static final String SFUNCTIONS = "sFunctions";
    public static final String SVARIABLES = "sVariables";
    public static final String TVARIABLES = "tVariables";

    @Override
    public JsonElement serialize(final Ruleset ruleset, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(NAME, ruleset.getName());

        jsonObject.addProperty(SAVE_LOC, ruleset.getSaveLocation().getPath());

        final JsonElement jsonTriggers = context.serialize(ruleset.getTriggers().toArray());
        jsonObject.add(TRIGGERS, jsonTriggers);

        final JsonElement jsonCom = context.serialize(ruleset.getCommands().toArray());
        jsonObject.add(COMMANDS, jsonCom);

//        final JsonElement jsonCF = context.serialize(ruleset.getCFunctions().toArray());
//        jsonObject.add(CFUNCTIONS, jsonCF);

        final JsonElement jsonCV = context.serialize(ruleset.getCVariables().toArray());
        jsonObject.add(CVARIABLES, jsonCV);

//        final JsonElement jsonSF = context.serialize(ruleset.getSFunctions().toArray());
//        jsonObject.add(SFUNCTIONS, jsonSF);

        final JsonElement jsonSV = context.serialize(ruleset.getSVariables().toArray());
        jsonObject.add(SVARIABLES, jsonSV);

        final JsonElement jsonTV = context.serialize(ruleset.getTVariables().toArray());
        jsonObject.add(TVARIABLES, jsonTV);

        // Need to serialize any force/block data
        final JsonArray jsonForceBlock = new JsonArray();
        serializeForceBlockData(ruleset.getRootDirectory(), jsonForceBlock, context); // Recursion, ho!
        jsonObject.add(ForceBlockDeserializer.FORCEDBLOCKED_RULES, jsonForceBlock);

        return jsonObject;
    }

    private void serializeForceBlockData(DirectoryFile directory, JsonArray jsonForceBlock, JsonSerializationContext context) {
        for (DirectoryFile branch : directory.getBranches()) {
            serializeForceBlockData(branch, jsonForceBlock, context);
        }

        for (RuleFile leaf : directory.getLeaves()) {
            if (!leaf.getFromBlacklist().isEmpty() || !leaf.getFromWhitelist().isEmpty()
                        || !leaf.getToBlacklist().isEmpty()
                        || !leaf.getToWhitelist().isEmpty()) {
                JsonObject jsonContainer = new JsonObject();

                jsonContainer.addProperty(ForceBlockDeserializer.ID, leaf.getRule().getId());

                final JsonElement fromBlocked = context.serialize(leaf.getFromBlacklist().toArray());
                jsonContainer.add(ForceBlockDeserializer.FROM_BLOCKED, fromBlocked);

                final JsonElement fromForced = context.serialize(leaf.getFromWhitelist().toArray());
                jsonContainer.add(ForceBlockDeserializer.FROM_FORCED, fromForced);

                final JsonElement toBlocked = context.serialize(leaf.getToBlacklist().toArray());
                jsonContainer.add(ForceBlockDeserializer.TO_BLOCKED, toBlocked);

                final JsonElement toForced = context.serialize(leaf.getToWhitelist().toArray());
                jsonContainer.add(ForceBlockDeserializer.TO_FORCED, toForced);

                jsonForceBlock.add(jsonContainer);
            }
        }
    }
}