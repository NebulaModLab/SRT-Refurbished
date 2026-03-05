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
import sj.editor.data.ForceBlockContainer;

/**
 * @author SafariJohn
 */
public class ForceBlockDeserializer implements JsonDeserializer<List<ForceBlockContainer>> {
    public static final String FORCEDBLOCKED_RULES = "forcedBlockedRules";
    public static final String ID = "id";
    public static final String FROM_BLOCKED = "fromBlocked";
    public static final String FROM_FORCED = "fromForced";
    public static final String TO_BLOCKED = "toBlocked";
    public static final String TO_FORCED = "toForced";

    @Override
    public List<ForceBlockContainer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        List<ForceBlockContainer> data = new ArrayList<>();

        final JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.get(FORCEDBLOCKED_RULES) != null) {
            for (JsonElement e : jsonObject.get(FORCEDBLOCKED_RULES).getAsJsonArray()) {
                if (e.isJsonObject()) {
                    JsonObject container = e.getAsJsonObject();
                    String name = "ERROR";
                    List<String> fromBlocked = new ArrayList<>();
                    List<String> fromForced = new ArrayList<>();
                    List<String> toBlocked = new ArrayList<>();
                    List<String> toForced = new ArrayList<>();

                    if (container.get(ID) != null) {
                        name = container.get(ID).getAsString();
                    }

                    for (JsonElement element : container.get(FROM_BLOCKED).getAsJsonArray()) {
                        fromBlocked.add(element.getAsString());
                    }

                    for (JsonElement element : container.get(FROM_FORCED).getAsJsonArray()) {
                        fromForced.add(element.getAsString());
                    }

                    for (JsonElement element : container.get(TO_BLOCKED).getAsJsonArray()) {
                        toBlocked.add(element.getAsString());
                    }

                    for (JsonElement element : container.get(TO_FORCED).getAsJsonArray()) {
                        toForced.add(element.getAsString());
                    }

                    data.add(new ForceBlockContainer(name, fromBlocked, fromForced, toBlocked, toForced));
                }
            }
        }

        return data;
    }

}
