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
import sj.editor.data.RulesetsManager;

/**
 * Author: SafariJohn
 */
public class Command {
    public enum FieldType {
        NONE("-select-"),
        KEY("$Key"),
        BOOLEAN("True/False"),
        INTEGER("Number"),
        FLOAT("Decimal"),
        STRING("Text"),
        ENUM("Options"),
        COLOR("Color"); // Not implemented yet; treat it as STRING.

        public final String text;

        FieldType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public enum ShowOn {
        BOTH,
        COND,
        SCRIPT;
    }

    private String name;
    private final int rulesetId;
    private final List<CommandField> fields;
    private String notes;

    private ShowOn whichPanels;

    public Command(int rulesetId) {
        name = "default";
        this.rulesetId = rulesetId;
        fields = new ArrayList<>();
        notes = "";

        whichPanels = ShowOn.BOTH;
    }

    public Command(int rulesetId, ShowOn whichPanels) {
        name = "default";
        this.rulesetId = rulesetId;
        fields = new ArrayList<>();
        notes = "";

        this.whichPanels = whichPanels;
    }

    public Command(String name, int rulesetId) {
        this.name = name;
        this.rulesetId = rulesetId;
        fields = new ArrayList<>();
        notes = "";

        whichPanels = ShowOn.BOTH;
    }

    public Command(String name, int rulesetId, ShowOn whichPanels) {
        this.name = name;
        this.rulesetId = rulesetId;
        fields = new ArrayList<>();
        notes = "";

        this.whichPanels = whichPanels;
    }

    public Command(Command com, int rulesetId) {
        name = com.getName();
        this.rulesetId = rulesetId;

        fields = new ArrayList<>();
        for (CommandField field : com.getFields()) {
            fields.add(new CommandField(field));
        }

        notes = com.getNotes();

        if (com.isConditionCommand() && com.isScriptCommand()) {
            whichPanels = ShowOn.BOTH;
        } else if (com.isConditionCommand()) {
            whichPanels = ShowOn.COND;
        } else {
            whichPanels = ShowOn.SCRIPT;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRulesetId() {
        return rulesetId;
    }

    public List<CommandField> getFields() {
        return fields;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setWhichPanelsToShowOn(ShowOn whichPanels) {
        this.whichPanels = whichPanels;
    }

    /**
     * @return Whether this command should show in the Conditions panel
     */
    public boolean isConditionCommand() {
        return whichPanels == ShowOn.BOTH || whichPanels == ShowOn.COND;
    }

    /**
     * @return Whether this command should show in the Script panel
     */
    public boolean isScriptCommand() {
        return whichPanels == ShowOn.BOTH || whichPanels == ShowOn.SCRIPT;
    }

    @Override
    public String toString() {
        String fullDisplayName = name;
        if (rulesetId != RulesetsManager.getVanilla().getId()) {
            fullDisplayName = "(" + RulesetsManager.getRuleset(rulesetId).getName() + ") " + name;
        }

        for (CommandField field : fields) {
            fullDisplayName += " " + field.toString();
        }

        return fullDisplayName;
    }
}
