/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.commands;

import java.util.ArrayList;
import java.util.List;
import sj.editor.data.commands.Command.FieldType;

/**
 * @author SafariJohn
 */
public class CommandField {
    private String name;
    private FieldType type;
    private boolean hasMin;
    private boolean hasMax;
    private float min;
    private float max;
    private boolean optional;
    private final List<String> options;

    public CommandField(FieldType type) {
        this.name = type.toString();
        this.type = type;
        this.hasMin = false;
        this.hasMax = false;
        this.min = 0;
        this.max = 0;
        this.optional = false;
        this.options = new ArrayList<>();
    }

    public CommandField(String name, FieldType type) {
        this.name = name;
        this.type = type;
        this.hasMin = false;
        this.hasMax = false;
        this.min = 0;
        this.max = 0;
        this.optional = false;
        this.options = new ArrayList<>();
    }

    public CommandField(CommandField field) {
        this.name = field.getName();
        this.type = field.getType();
        this.hasMin = field.hasMin();
        this.hasMax = field.hasMax();
        this.min = field.getMin();
        this.max = field.getMax();
        this.optional = field.isOptional();
        this.options = new ArrayList<>(field.getOptions());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (type == FieldType.KEY && !name.startsWith("$")) {
            name = "$" + name;
        }

        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public boolean hasMin() {
        return hasMin;
    }

    public void setHasMin(boolean hasMin) {
        this.hasMin = hasMin;
    }

    public boolean hasMax() {
        return hasMax;
    }

    public void setHasMax(boolean hasMax) {
        this.hasMax = hasMax;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        hasMin = true;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        hasMax = true;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public List<String> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        if (type == FieldType.ENUM && options.size() == 1) {
            return options.get(0);
        }

        if (optional) {
            return "[" + name + "]";
        } else {
            return "<" + name + ">";
        }
    }
}
