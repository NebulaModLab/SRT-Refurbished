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
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.FieldType;
import sj.editor.data.commands.CommandField;

/**
 * @author SafariJohn
 */
public class CommandSerializer implements JsonSerializer<Command> {
    // Command keys
    public static final String NAME = "name";
    public static final String INPUT_FIELDS = "inputFields";
    public static final String NOTES = "notes";

    @Override
    public JsonElement serialize(Command cmd, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(NAME, cmd.getName());

        if (!cmd.getNotes().isEmpty()) jsonObject.addProperty(RuleSerializer.NOTES, cmd.getNotes());

        if (!cmd.getFields().isEmpty()) {
            final JsonElement fieldsJson = context.serialize(cmd.getFields().toArray());
            jsonObject.add(INPUT_FIELDS, fieldsJson);
        }

        return jsonObject;
    }

    public static class CommandFieldSerializer implements JsonSerializer<CommandField> {
    // CommandField keys
    public static final String FIELD_NAME = "fieldName";
    public static final String FIELD_TYPE = "fieldType";
    public static final String FIELD_OPTIONAL = "fieldOptional";
    public static final String FIELD_MIN = "fieldMin";
    public static final String FIELD_MAX = "fieldMax";
    public static final String FIELD_OPTIONS = "fieldOptions";

        @Override
        public JsonElement serialize(CommandField field, Type typeOfSrc, JsonSerializationContext context) {
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty(FIELD_NAME, field.getName());
            jsonObject.addProperty(FIELD_TYPE, field.getType().toString());

            if (field.isOptional()) jsonObject.addProperty(FIELD_OPTIONAL, field.isOptional());
            if (field.getType() == FieldType.FLOAT || field.getType() == FieldType.INTEGER) {
                if (field.hasMin()) jsonObject.addProperty(FIELD_MIN, field.getMin());
                if (field.hasMax()) jsonObject.addProperty(FIELD_MAX, field.getMax());
            }

            if (field.getType() == FieldType.ENUM) {
                final JsonElement optionsJson = context.serialize(field.getOptions().toArray());
                jsonObject.add(FIELD_OPTIONS, optionsJson);
            }

            return jsonObject;
        }

    }

}
