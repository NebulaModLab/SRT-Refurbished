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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.FieldType;
import sj.editor.data.commands.CommandField;
import static sj.editor.io.json.CommandSerializer.*;
import static sj.editor.io.json.CommandSerializer.CommandFieldSerializer.*;

/**
 * @author SafariJohn
 */
public class CommandDeserializer implements JsonDeserializer<Command> {
    private static final Logger logger = Logger.getLogger(CommandDeserializer.class.getName());

    @Override
    public Command deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String name = "ERROR";
        List<CommandField> fields = new ArrayList<>();
        String notes = "";

        final JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.get(NAME) != null) {
            name = jsonObject.get(NAME).getAsString();
        }

        if (jsonObject.get(INPUT_FIELDS) != null) {
            final JsonArray jsonInputFieldsArray = jsonObject.get(INPUT_FIELDS).getAsJsonArray();
            for (JsonElement element : jsonInputFieldsArray) {
                fields.add(deserializeCommandField((JsonObject) element, name));
            }
        }


        if (jsonObject.get(NOTES) != null) {
            notes = jsonObject.get(NOTES).getAsString();
        }

        Command com = new Command(-1);
        com.setName(name);
        com.getFields().addAll(fields);
        com.setNotes(notes);

        return com;
    }

    private CommandField deserializeCommandField(final JsonObject jsonObject, String commandName) {
        String name = "ERROR";
        FieldType type = Command.FieldType.NONE;
        boolean hasMin = false;
        float fieldMin = 0;
        boolean hasMax = false;
        float fieldMax = 10;
        boolean optional = false;

        if (jsonObject.get(FIELD_NAME) != null) {
            name = jsonObject.get(FIELD_NAME).getAsString();
        }

        if (jsonObject.get(FIELD_TYPE) != null) {
            String t = jsonObject.get(FIELD_TYPE).getAsString();
            switch (t) {
                case "$Key": type = FieldType.KEY; break;
                case "True/False": type = FieldType.BOOLEAN; break;
                case "Number": type = FieldType.INTEGER; break;
                case "Decimal": type = FieldType.FLOAT; break;
                case "Text": type = FieldType.STRING; break;
                case "Options": type = FieldType.ENUM; break;
                case "Color": type = FieldType.COLOR; break;
                default: logger.log(Level.SEVERE, "Erroneous field type {0} for {1} of {3}", new Object[]{t, name, commandName});
            }
        }

        if (jsonObject.get(FIELD_MIN) != null) {
            fieldMin = jsonObject.get(FIELD_MIN).getAsFloat();
            hasMin = true;
        }


        if (jsonObject.get(FIELD_MAX) != null) {
            fieldMax = jsonObject.get(FIELD_MAX).getAsFloat();
            hasMax = true;
        }


        if (jsonObject.get(FIELD_OPTIONAL) != null) {
            optional = jsonObject.get(FIELD_OPTIONAL).getAsBoolean();
        }


        List<String> options = new ArrayList<>();

        if (type == FieldType.ENUM) {
            final JsonArray jsonOptionsArray = jsonObject.get(FIELD_OPTIONS).getAsJsonArray();
            for (JsonElement element : jsonOptionsArray) {
                options.add(element.getAsString());
            }
        }


        CommandField field = new CommandField(name, type);
        field.setHasMin(hasMin);
        field.setMin(fieldMin);
        field.setHasMax(hasMax);
        field.setMin(fieldMax);
        field.setOptional(optional);
        field.getOptions().addAll(options);

        return field;
    }
}
