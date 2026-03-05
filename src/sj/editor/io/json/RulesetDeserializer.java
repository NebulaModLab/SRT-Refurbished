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
import sj.editor.data.Ruleset;
import sj.editor.data.commands.Command;
import static sj.editor.io.json.RulesetSerializer.*;

/**
 * @author SafariJohn
 */
public class RulesetDeserializer implements JsonDeserializer<Ruleset> {

    @Override
    public Ruleset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String name = "ERROR";
        String savePath = "";
        List<String> triggers = new ArrayList<>();
        List<Command> commands = new ArrayList<>();
        List<String> cFunctions = new ArrayList<>();
        List<String> cVariables = new ArrayList<>();
        List<String> sFunctions = new ArrayList<>();
        List<String> sVariables = new ArrayList<>();
        List<String> tVariables = new ArrayList<>();

        final JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.get(NAME) != null) {
            name = jsonObject.get(NAME).getAsString();
        }

        Ruleset ruleset = new Ruleset(name);

        if (jsonObject.get(SAVE_LOC) != null) {
            savePath = jsonObject.get(SAVE_LOC).getAsString();
        }

        if (jsonObject.get(TRIGGERS) != null) {
            final JsonArray jsonTriggersArray = jsonObject.get(TRIGGERS).getAsJsonArray();
            for (JsonElement element : jsonTriggersArray) {
                triggers.add(element.getAsString());
            }
        }

        if (jsonObject.get(COMMANDS) != null) {
            final JsonArray jsonCommandArray = jsonObject.get(COMMANDS).getAsJsonArray();
            try {
                for (JsonElement element : jsonCommandArray) {
                    Command com = new Command((Command) context.deserialize(element, Command.class), ruleset.getId());
                    commands.add(com);
                }
            } catch (NullPointerException ex) {
                throw ex;
            }
        }

        if (jsonObject.get(CFUNCTIONS) != null) {
            final JsonArray jsonCFunctionsArray = jsonObject.get(CFUNCTIONS).getAsJsonArray();
            for (JsonElement element : jsonCFunctionsArray) {
                cFunctions.add(element.getAsString());
            }
        }

        if (jsonObject.get(CVARIABLES) != null) {
            final JsonArray jsonCVariablesArray = jsonObject.get(CVARIABLES).getAsJsonArray();
            for (JsonElement element : jsonCVariablesArray) {
                cVariables.add(element.getAsString());
            }
        }

        if (jsonObject.get(SFUNCTIONS) != null) {
            final JsonArray jsonSFunctionsArray = jsonObject.get(SFUNCTIONS).getAsJsonArray();
            for (JsonElement element : jsonSFunctionsArray) {
                sFunctions.add(element.getAsString());
            }
        }

        if (jsonObject.get(SVARIABLES) != null) {
            final JsonArray jsonSVariablesArray = jsonObject.get(SVARIABLES).getAsJsonArray();
            for (JsonElement element : jsonSVariablesArray) {
                sVariables.add(element.getAsString());
            }
        }

        if (jsonObject.get(TVARIABLES) != null) {
            final JsonArray jsonTVariablesArray = jsonObject.get(TVARIABLES).getAsJsonArray();
            for (JsonElement element : jsonTVariablesArray) {
                tVariables.add(element.getAsString());
            }
        }

        // Convert function strings to Commands
        OUTER:
        for (String function : cFunctions) {
            for (Command com : commands) {
                if (function.equals(com.getName())) continue OUTER;
            }

            Command com = new Command(function, ruleset.getId());

            if (sFunctions.contains(function)) {
                com.setWhichPanelsToShowOn(Command.ShowOn.BOTH);
            } else {
                com.setWhichPanelsToShowOn(Command.ShowOn.COND);
            }

            commands.add(com);
        }
        OUTER:
        for (String function : sFunctions) {
            if (cFunctions.contains(function)) continue;

            for (Command com : commands) {
                if (function.equals(com.getName())) continue OUTER;
            }

            Command com = new Command(function, ruleset.getId());
            com.setWhichPanelsToShowOn(Command.ShowOn.SCRIPT);
            commands.add(com);
        }

        ruleset.setSaveLocation(new File(savePath));
        ruleset.getTriggers().addAll(triggers);
        ruleset.getCommands().addAll(commands);
        ruleset.getCVariables().addAll(cVariables);
        ruleset.getSVariables().addAll(sVariables);
        ruleset.getTVariables().addAll(tVariables);

        return ruleset;
    }

}
