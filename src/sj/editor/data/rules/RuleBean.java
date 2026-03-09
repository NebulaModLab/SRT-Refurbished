/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.rules;

/**
 * @author SafariJohn (original SRT)
 */
public class RuleBean {
    private String id;
    private String trigger;
    private String conditions;
    private String script;
    private String text;
    private String options;
    private String notes;

    public RuleBean() {
        id = "";
        trigger = "";
        conditions = "";
        script = "";
        text = "";
        options = "";
        notes = "";
    }

    public RuleBean(RuleBean rule) {
        this.id = rule.getId();
        this.trigger = rule.getTrigger();
        this.conditions = rule.getConditions();
        this.script = rule.getScript();
        this.text = rule.getText();
        this.options = rule.getOptions();
        this.notes = rule.getNotes();
    }

    public boolean isComment() {
        return id.isEmpty() && trigger.isEmpty() && conditions.isEmpty()
                    && script.isEmpty() && text.isEmpty() && options.isEmpty()
                    && !notes.isEmpty();
    }

    public boolean isDirectory() {
        return id.startsWith("#") && trigger.isEmpty() && conditions.isEmpty()
                    && script.isEmpty() && text.isEmpty() && options.isEmpty();
    }

    public boolean isSpacer() {
        return id.isEmpty() && trigger.isEmpty() && conditions.isEmpty()
                    && script.isEmpty() && text.isEmpty() && options.isEmpty()
                    && notes.isEmpty();
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
//</editor-fold>

}
