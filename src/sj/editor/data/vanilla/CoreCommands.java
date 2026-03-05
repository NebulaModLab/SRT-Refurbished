/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data.vanilla;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.ShowOn;
import sj.editor.data.commands.CommandField;

/**
 * @author SafariJohn
 */
public class CoreCommands {
    protected static List<Command> getCommands(int rulesetId) {
        List<Command> commands = new ArrayList<>();

        Command cmd = new Command("BarCMD", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        CommandField field = new CommandField(Command.FieldType.ENUM);
        field.setName("showOptions");
        field.getOptions().add("showOptions");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BaseSalvageSpecial", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("Wait", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$handle");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("duration");
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$finished");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$interrupted");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$inProgress");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AbortWait", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$handle");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NPCWantsComms", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("AnyNearbyFleetsHostileAndAware", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ArePatrolsNearby", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FactionFleetNearbyAndAware", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("HostileFleetNearbyAndAware", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("GiveOtherFleetAssignment", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("assignment");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("duration");
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("action_text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("GiveOtherFleetAssignment", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("assignment");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("duration");
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("action_text");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("target_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeHostileWhileTOff", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeHostileWhileTOff", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setOptional(true);
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAggressive", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAggressive", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAggressiveOnce", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAggressiveOnce", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAllowDisengage", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAllowDisengage", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetHostile", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetHostile", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNoRepImpact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNoRepImpact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetLowRepImpact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetLowRepImpact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNonAggressive", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNonAggressive", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNonHostile", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetNonHostile", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetPreventDisengage", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetPreventDisengage", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ActivateAbility", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("fleet_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("ability_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("DeactivateAbility", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("fleet_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("ability_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddCredits", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("credits");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddCommodity", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("commodity_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("quantity");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("with_text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddRemoveCommodity", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("commodity_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("quantity");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("with_text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddPotentialContact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddShip", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$fleetMember");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Takes a FleetMemberAPI passed using a variable.");

        cmd = new Command("AddShipToOtherFleet", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$fleetMember");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Takes a FleetMemberAPI passed using a variable.");

        cmd = new Command("AddStoryPoints", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("amount");
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Use commands like SetStoryOption to remove Story Points.");

        cmd = new Command("AddXP", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("amount");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddOption", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("order");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddSelector", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("order");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.KEY);
        field.setName("result_variable");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.COLOR);
        field.setName("color");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("min");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("max");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddText", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.COLOR);
        field.setName("color");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddTextSmall", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.COLOR);
        field.setName("color");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AdjustRep", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("limit");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("delta");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AdjustRep", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("RepActions_action");
        cmd.getFields().add(field);
        field.getOptions().add("COMBAT_NORMAL");
        field.getOptions().add("COMBAT_AGGRESSIVE");
        field.getOptions().add("COMBAT_NORMAL_TOFF");
        field.getOptions().add("COMBAT_AGGRESSIVE_TOFF");
        field.getOptions().add("COMBAT_HELP_MINOR");
        field.getOptions().add("COMBAT_HELP_MAJOR");
        field.getOptions().add("COMBAT_HELP_CRITICAL");
        field.getOptions().add("COMBAT_FRIENDLY_FIRE");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_FAST");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_NORMAL");
        field.getOptions().add("SYSTEM_BOUNTY_REWARD");
        field.getOptions().add("PERSON_BOUNTY_REWARD");
        field.getOptions().add("COMBAT_WITH_ENEMY");
        field.getOptions().add("TRADE_EFFECT");
        field.getOptions().add("SMUGGLING_EFFECT");
        field.getOptions().add("TRADE_WITH_ENEMY");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MINOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MAJOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_CRITICAL");
        field.getOptions().add("SMUGGLING_INVESTIGATION_GUILTY");
        field.getOptions().add("COMM_SNIFFER_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY_MAJOR");
        field.getOptions().add("CAUGHT_INSTALLING_SNIFFER");
        field.getOptions().add("CUSTOMS_NOTICED_EVADING");
        field.getOptions().add("CUSTOMS_CAUGHT_SMUGGLING");
        field.getOptions().add("CUSTOMS_REFUSED_TOLL");
        field.getOptions().add("CUSTOMS_REFUSED_FINE");
        field.getOptions().add("CUSTOMS_COULD_NOT_AFFORD");
        field.getOptions().add("CUSTOMS_PAID");
        field.getOptions().add("REP_DECAY_POSITIVE");
        field.getOptions().add("TRANSPONDER_OFF");
        field.getOptions().add("TRANSPONDER_OFF_REFUSE");
        field.getOptions().add("CARGO_SCAN_REFUSE");
        field.getOptions().add("MISSION_SUCCESS");
        field.getOptions().add("MISSION_FAILURE");
        field.getOptions().add("MAKE_SUSPICOUS_AT_WORST");
        field.getOptions().add("MAKE_HOSTILE_AT_BEST");
        field.getOptions().add("COMMISSION_ACCEPT");
        field.getOptions().add("COMMISSION_BOUNTY_REWARD");
        field.getOptions().add("COMMISSION_NEUTRAL_BATTLE_PENALTY");
        field.getOptions().add("COMMISSION_PENALTY_HOSTILE_TO_NON_ENEMY");
        field.getOptions().add("SHRINE_OFFERING");
        field.getOptions().add("INTERDICTED");
        field.getOptions().add("CUSTOM");
        //</editor-fold>
        cmd.setNotes("Adjusts the player's reputation with the "
                    + "faction based on the given action. "
                    + "See: com.fs.starfarer.api.impl.campaign."
                    + "CoreReputationPlugin.RepActions");

        cmd = new Command("AdjustRepActivePerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("limit");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("delta");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AdjustRepActivePerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("RepActions_action");
        cmd.getFields().add(field);
        field.getOptions().add("COMBAT_NORMAL");
        field.getOptions().add("COMBAT_AGGRESSIVE");
        field.getOptions().add("COMBAT_NORMAL_TOFF");
        field.getOptions().add("COMBAT_AGGRESSIVE_TOFF");
        field.getOptions().add("COMBAT_HELP_MINOR");
        field.getOptions().add("COMBAT_HELP_MAJOR");
        field.getOptions().add("COMBAT_HELP_CRITICAL");
        field.getOptions().add("COMBAT_FRIENDLY_FIRE");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_FAST");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_NORMAL");
        field.getOptions().add("SYSTEM_BOUNTY_REWARD");
        field.getOptions().add("PERSON_BOUNTY_REWARD");
        field.getOptions().add("COMBAT_WITH_ENEMY");
        field.getOptions().add("TRADE_EFFECT");
        field.getOptions().add("SMUGGLING_EFFECT");
        field.getOptions().add("TRADE_WITH_ENEMY");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MINOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MAJOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_CRITICAL");
        field.getOptions().add("SMUGGLING_INVESTIGATION_GUILTY");
        field.getOptions().add("COMM_SNIFFER_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY_MAJOR");
        field.getOptions().add("CAUGHT_INSTALLING_SNIFFER");
        field.getOptions().add("CUSTOMS_NOTICED_EVADING");
        field.getOptions().add("CUSTOMS_CAUGHT_SMUGGLING");
        field.getOptions().add("CUSTOMS_REFUSED_TOLL");
        field.getOptions().add("CUSTOMS_REFUSED_FINE");
        field.getOptions().add("CUSTOMS_COULD_NOT_AFFORD");
        field.getOptions().add("CUSTOMS_PAID");
        field.getOptions().add("REP_DECAY_POSITIVE");
        field.getOptions().add("TRANSPONDER_OFF");
        field.getOptions().add("TRANSPONDER_OFF_REFUSE");
        field.getOptions().add("CARGO_SCAN_REFUSE");
        field.getOptions().add("MISSION_SUCCESS");
        field.getOptions().add("MISSION_FAILURE");
        field.getOptions().add("MAKE_SUSPICOUS_AT_WORST");
        field.getOptions().add("MAKE_HOSTILE_AT_BEST");
        field.getOptions().add("COMMISSION_ACCEPT");
        field.getOptions().add("COMMISSION_BOUNTY_REWARD");
        field.getOptions().add("COMMISSION_NEUTRAL_BATTLE_PENALTY");
        field.getOptions().add("COMMISSION_PENALTY_HOSTILE_TO_NON_ENEMY");
        field.getOptions().add("SHRINE_OFFERING");
        field.getOptions().add("INTERDICTED");
        field.getOptions().add("CUSTOM");
        //</editor-fold>
        cmd.setNotes("Adjusts the player's reputation with the "
                    + "active person based on the given action. "
                    + "See: com.fs.starfarer.api.impl.campaign."
                    + "CoreReputationPlugin.RepActions");

        cmd = new Command("AdjustRepPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$person");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("limit");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("delta");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AdjustRepPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("importantPersonID");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("limit");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("delta");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AdjustRepPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$person");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("RepActions_action");
        cmd.getFields().add(field);
        field.getOptions().add("COMBAT_NORMAL");
        field.getOptions().add("COMBAT_AGGRESSIVE");
        field.getOptions().add("COMBAT_NORMAL_TOFF");
        field.getOptions().add("COMBAT_AGGRESSIVE_TOFF");
        field.getOptions().add("COMBAT_HELP_MINOR");
        field.getOptions().add("COMBAT_HELP_MAJOR");
        field.getOptions().add("COMBAT_HELP_CRITICAL");
        field.getOptions().add("COMBAT_FRIENDLY_FIRE");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_FAST");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_NORMAL");
        field.getOptions().add("SYSTEM_BOUNTY_REWARD");
        field.getOptions().add("PERSON_BOUNTY_REWARD");
        field.getOptions().add("COMBAT_WITH_ENEMY");
        field.getOptions().add("TRADE_EFFECT");
        field.getOptions().add("SMUGGLING_EFFECT");
        field.getOptions().add("TRADE_WITH_ENEMY");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MINOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MAJOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_CRITICAL");
        field.getOptions().add("SMUGGLING_INVESTIGATION_GUILTY");
        field.getOptions().add("COMM_SNIFFER_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY_MAJOR");
        field.getOptions().add("CAUGHT_INSTALLING_SNIFFER");
        field.getOptions().add("CUSTOMS_NOTICED_EVADING");
        field.getOptions().add("CUSTOMS_CAUGHT_SMUGGLING");
        field.getOptions().add("CUSTOMS_REFUSED_TOLL");
        field.getOptions().add("CUSTOMS_REFUSED_FINE");
        field.getOptions().add("CUSTOMS_COULD_NOT_AFFORD");
        field.getOptions().add("CUSTOMS_PAID");
        field.getOptions().add("REP_DECAY_POSITIVE");
        field.getOptions().add("TRANSPONDER_OFF");
        field.getOptions().add("TRANSPONDER_OFF_REFUSE");
        field.getOptions().add("CARGO_SCAN_REFUSE");
        field.getOptions().add("MISSION_SUCCESS");
        field.getOptions().add("MISSION_FAILURE");
        field.getOptions().add("MAKE_SUSPICOUS_AT_WORST");
        field.getOptions().add("MAKE_HOSTILE_AT_BEST");
        field.getOptions().add("COMMISSION_ACCEPT");
        field.getOptions().add("COMMISSION_BOUNTY_REWARD");
        field.getOptions().add("COMMISSION_NEUTRAL_BATTLE_PENALTY");
        field.getOptions().add("COMMISSION_PENALTY_HOSTILE_TO_NON_ENEMY");
        field.getOptions().add("SHRINE_OFFERING");
        field.getOptions().add("INTERDICTED");
        field.getOptions().add("CUSTOM");
        //</editor-fold>
        cmd.setNotes("Adjusts the player's reputation with the "
                    + "given person based on the given action. "
                    + "See: com.fs.starfarer.api.impl.campaign."
                    + "CoreReputationPlugin.RepActions");

        cmd = new Command("AdjustRepPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("importantPersonID");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("RepActions_action");
        cmd.getFields().add(field);
        field.getOptions().add("COMBAT_NORMAL");
        field.getOptions().add("COMBAT_AGGRESSIVE");
        field.getOptions().add("COMBAT_NORMAL_TOFF");
        field.getOptions().add("COMBAT_AGGRESSIVE_TOFF");
        field.getOptions().add("COMBAT_HELP_MINOR");
        field.getOptions().add("COMBAT_HELP_MAJOR");
        field.getOptions().add("COMBAT_HELP_CRITICAL");
        field.getOptions().add("COMBAT_FRIENDLY_FIRE");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_FAST");
        field.getOptions().add("FOOD_SHORTAGE_PLAYER_ENDED_NORMAL");
        field.getOptions().add("SYSTEM_BOUNTY_REWARD");
        field.getOptions().add("PERSON_BOUNTY_REWARD");
        field.getOptions().add("COMBAT_WITH_ENEMY");
        field.getOptions().add("TRADE_EFFECT");
        field.getOptions().add("SMUGGLING_EFFECT");
        field.getOptions().add("TRADE_WITH_ENEMY");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MINOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_MAJOR");
        field.getOptions().add("OTHER_FACTION_GOOD_REP_INVESTIGATION_CRITICAL");
        field.getOptions().add("SMUGGLING_INVESTIGATION_GUILTY");
        field.getOptions().add("COMM_SNIFFER_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY");
        field.getOptions().add("FOOD_INVESTIGATION_GUILTY_MAJOR");
        field.getOptions().add("CAUGHT_INSTALLING_SNIFFER");
        field.getOptions().add("CUSTOMS_NOTICED_EVADING");
        field.getOptions().add("CUSTOMS_CAUGHT_SMUGGLING");
        field.getOptions().add("CUSTOMS_REFUSED_TOLL");
        field.getOptions().add("CUSTOMS_REFUSED_FINE");
        field.getOptions().add("CUSTOMS_COULD_NOT_AFFORD");
        field.getOptions().add("CUSTOMS_PAID");
        field.getOptions().add("REP_DECAY_POSITIVE");
        field.getOptions().add("TRANSPONDER_OFF");
        field.getOptions().add("TRANSPONDER_OFF_REFUSE");
        field.getOptions().add("CARGO_SCAN_REFUSE");
        field.getOptions().add("MISSION_SUCCESS");
        field.getOptions().add("MISSION_FAILURE");
        field.getOptions().add("MAKE_SUSPICOUS_AT_WORST");
        field.getOptions().add("MAKE_HOSTILE_AT_BEST");
        field.getOptions().add("COMMISSION_ACCEPT");
        field.getOptions().add("COMMISSION_BOUNTY_REWARD");
        field.getOptions().add("COMMISSION_NEUTRAL_BATTLE_PENALTY");
        field.getOptions().add("COMMISSION_PENALTY_HOSTILE_TO_NON_ENEMY");
        field.getOptions().add("SHRINE_OFFERING");
        field.getOptions().add("INTERDICTED");
        field.getOptions().add("CUSTOM");
        //</editor-fold>
        cmd.setNotes("Adjusts the player's reputation with the "
                    + "given important person based on the given action. "
                    + "See: com.fs.starfarer.api.impl.campaign."
                    + "CoreReputationPlugin.RepActions");

        cmd = new Command("BeginConversation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BeginConversation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("minimalMode");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BeginConversation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("minimalMode");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("showRelationBar");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BeginMission", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("mission_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOptionOpenCore", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("CARGO");
        cmd.getFields().add(field);
        field.getOptions().add("CARGO");

        field = new CommandField(Command.FieldType.STRING);
        field.setName("$tradeMode");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOptionOpenCore", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("core_ui");
        cmd.getFields().add(field);
        field.getOptions().add("CHARACTER");
        field.getOptions().add("FLEET");
        field.getOptions().add("REFIT");
        field.getOptions().add("CARGO");
        field.getOptions().add("MAP");
        field.getOptions().add("INTEL");
        field.getOptions().add("OFFICERS");
        field.getOptions().add("OUTPOSTS");

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("tradeMode");
        cmd.getFields().add(field);
        field.getOptions().add("OPEN");
        field.getOptions().add("SNEAK");
        field.getOptions().add("NONE");
        //</editor-fold>

        cmd = new Command("BroadcastPlayerAction", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("action");
        cmd.getFields().add(field);
        field.getOptions().add("CANCEL");
        field.getOptions().add("HOSTILE");

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$response_variable");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BroadcastPlayerWaitAction", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("wait_handle");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("action");
        cmd.getFields().add(field);
        field.getOptions().add("CANCEL");
        field.getOptions().add("HOSTILE");

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$response_variable");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("BroadcastCancelPlayerAction", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$response_variable");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("Call", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$event_handle");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("params ...");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("CallEvent", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$event_handle");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("params ...");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Use Call command instead.");

        cmd = new Command("CaresAboutTransponder", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("CheckSetting", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("setting_id");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Checks the given boolean in settings.json");

        cmd = new Command("UpdateMemory", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("DoCanAffordCheck", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("price");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("show_total");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("HailPlayer", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("HideSecondPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("HideThirdPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("Highlight", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("highlights ...");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Alias of SetTextHighlights.");

        cmd = new Command("HighlightComms", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("IsSeenByAnyFleet", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeFullySurveyed", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("MakeNearbyFleetsNonHostile", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("faction_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        field.setMin(0);
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("days");
        field.setMin(0);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetAvoidContact", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("avoid_contact");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Calling this command without an argument unsets it.");

        cmd = new Command("MakeOtherFleetDoThing", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("enitity_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("duration");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("with_clear");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetGoAway", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("with_clear");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MovePersonToMarket", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("market_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("PLAddEntry", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("PlayerFleetHasShipWithBaseHull", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("base_hull_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("PlayerFleetHasShipWithId", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("fleet_member_uid");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("This looks for the fleet member's unique id.");

        cmd = new Command("PlayerHasCargo", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("cargo_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("quantity");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Checks commodities, weapons, fighters, and special items.");

        cmd = new Command("RemoveCommodity", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("commodity_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("quantity");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("params ...");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("RemoveShip", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$fleet_member_reference");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("RepGTE", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("rep_level");
        cmd.getFields().add(field);
        field.getOptions().add("VENGEFUL");
        field.getOptions().add("HOSTILE");
        field.getOptions().add("INHOSPITABLE");
        field.getOptions().add("SUSPICIOUS");
        field.getOptions().add("NEUTRAL");
        field.getOptions().add("FAVORABLE");
        field.getOptions().add("WELCOMING");
        field.getOptions().add("FRIENDLY");
        field.getOptions().add("COOPERATIVE");
        //</editor-fold>
        cmd.setNotes("Alias of RepIsAtWorst.");

        cmd = new Command("RepLTE", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("rep_level");
        cmd.getFields().add(field);
        field.getOptions().add("VENGEFUL");
        field.getOptions().add("HOSTILE");
        field.getOptions().add("INHOSPITABLE");
        field.getOptions().add("SUSPICIOUS");
        field.getOptions().add("NEUTRAL");
        field.getOptions().add("FAVORABLE");
        field.getOptions().add("WELCOMING");
        field.getOptions().add("FRIENDLY");
        field.getOptions().add("COOPERATIVE");
        //</editor-fold>
        cmd.setNotes("Alias of RepIsAtBest.");

        cmd = new Command("ResetActivePerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("RestoreSavedVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("RollProbability", rulesetId, ShowOn.BOTH);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("probability");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SaveCurrentVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("SetColor", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$variable");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.COLOR);
        field.setName("color");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetNearbyFleetsVariable", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("range");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$variable_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("value");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("duration");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetOptionColor", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.COLOR);
        field.setName("color");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetOptionText", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetOtherFleetAllowJump", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("allow");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetPersonHidden", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("hidden");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("SetPersonPortrait", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("portrait_id");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("SetStoryColor", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetStoryOption", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("sound_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("log_text");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Default of 1 SP. Not sure how much bonus XP.");

        cmd = new Command("SetStoryOption", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("num_story_points");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("bonus_xp_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("sound_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetStoryOption", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("num_story_points");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("bonus_xp_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("sound_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("log_text");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowRemainingCapacity", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("commodity_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Shows remaining cargo capacity by default.");

        cmd = new Command("ShowResCost", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("commodity_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("quantity");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("consumed");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowSecondPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("ShowThirdPerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("UnhidePerson", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("expire", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$name");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expiration");
        field.setHasMin(true);
        field.setMin(0);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("unset", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$variable");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("unsetAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$prefix");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Unsets all keys in the local context with "
                    + "the given prefix.");

        cmd = new Command("unsetAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$context.prefix");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Unsets all keys in the given context with "
                    + "the given prefix.");

        cmd = new Command("DismissDialog", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("DismissDialog", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("dismiss_text");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("The text is stored in the sector's memory "
                    + "under $core_dismissParam, which can be accessed"
                    + "normally or via DismissDialog.getDismissParam(). "
                    + "The latter also unsets it.");

        cmd = new Command("DumpMemory", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("EndConversation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("EndConversation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("options");
        field.getOptions().add("DO_NOT_FIRE");
        field.getOptions().add("NO_CONTINUE");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FireAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$trigger_var");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FireAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("trigger");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FireBest", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$trigger_var");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FireBest", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("trigger");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("FleetDesc", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("HasAttentionOfAuthorities", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("MakePlayerImmediatelyAttackable", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("IsSeenByPatrols", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("must_care_about_transponder");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("IsSoughtByPatrols", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCAddCargo", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("cargo_type");
        field.getOptions().add("RESOURCES");
        field.getOptions().add("WEAPONS");
        field.getOptions().add("FIGHTER_CHIP");
        field.getOptions().add("SPECIAL");
        field.getOptions().add("NULL");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("cargo_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("quantity");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCRemoveCargo", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("cargo_type");
        field.getOptions().add("RESOURCES");
        field.getOptions().add("WEAPONS");
        field.getOptions().add("FIGHTER_CHIP");
        field.getOptions().add("SPECIAL");
        field.getOptions().add("NULL");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("cargo_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("quantity");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCAddCharacterPoints", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("amount");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCAddCredits", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("credits");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCAddShip", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("variant_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCAddShipSilent", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("variant_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCRemoveShip", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("variant_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCCanSkipTutorial", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("NGCDone", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("NGCSetAptitude", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("aptitude_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("level");
        field.setHasMin(true);
        field.setHasMax(true);
        field.setMin(0);
        field.setMax(3);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCSetSkill", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("skill_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("level");
        field.setHasMin(true);
        field.setHasMax(true);
        field.setMin(0);
        field.setMax(3);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCSetCustom", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("key");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCSetDifficulty", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("difficulty");
        field.getOptions().add("EASY");
        field.getOptions().add("NORMAL");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("NGCSetStartingLocation", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("location_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("x");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("y");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("The location_id is either \"hyperspace\" or "
                    + "a star system's id.");

        cmd = new Command("NGCSetWithTimePass", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("true or false");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("OpenCommDirectory", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("OpenComms", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("OpenCoreTab", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("core_ui");
        cmd.getFields().add(field);
        field.getOptions().add("CHARACTER");
        field.getOptions().add("FLEET");
        field.getOptions().add("REFIT");
        field.getOptions().add("CARGO");
        field.getOptions().add("MAP");
        field.getOptions().add("INTEL");
        field.getOptions().add("OFFICERS");
        field.getOptions().add("OUTPOSTS");

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("tradeMode");
        cmd.getFields().add(field);
        field.getOptions().add("OPEN");
        field.getOptions().add("SNEAK");
        field.getOptions().add("NONE");
        //</editor-fold>

        cmd = new Command("PaginatedOptions", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("options ...");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("PickCommsNPC", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("Ping", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("type");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("entity_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("See data/campaign/pings.json for ping types.");

        cmd = new Command("PrintDescription", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("index");
        field.setHasMin(true);
        field.setHasMax(true);
        field.setMin(1);
        field.setMax(3);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ReinitDialog", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("RepIsAtBest", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("rep_level");
        cmd.getFields().add(field);
        field.getOptions().add("VENGEFUL");
        field.getOptions().add("HOSTILE");
        field.getOptions().add("INHOSPITABLE");
        field.getOptions().add("SUSPICIOUS");
        field.getOptions().add("NEUTRAL");
        field.getOptions().add("FAVORABLE");
        field.getOptions().add("WELCOMING");
        field.getOptions().add("FRIENDLY");
        field.getOptions().add("COOPERATIVE");
        //</editor-fold>

        cmd = new Command("RepIsAtWorst", rulesetId, ShowOn.COND);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.ENUM);
        field.setName("rep_level");
        cmd.getFields().add(field);
        field.getOptions().add("VENGEFUL");
        field.getOptions().add("HOSTILE");
        field.getOptions().add("INHOSPITABLE");
        field.getOptions().add("SUSPICIOUS");
        field.getOptions().add("NEUTRAL");
        field.getOptions().add("FAVORABLE");
        field.getOptions().add("WELCOMING");
        field.getOptions().add("FRIENDLY");
        field.getOptions().add("COOPERATIVE");
        //</editor-fold>

        cmd = new Command("RepairAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        cmd.setNotes("Uses supplies.");

        cmd = new Command("RepairAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("false");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("RepairAll", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$use_supplies");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("RepairAvailable", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("RepairEnoughSupplies", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("RepairNeeded", rulesetId, ShowOn.COND);
        commands.add(cmd);

        cmd = new Command("SetActiveMission", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.KEY);
        field.setName("$event_handle");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ClearActiveMission", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        cmd.setNotes("\"Super unused.\"");

        cmd = new Command("SetActiveMission", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("enabled");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("DespawnEntity", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("entity_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetFlagship", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("SetPromptText", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetShortcut", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("key");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("put_last");
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("See org.lwjgl.input.Keyboard in lwjgl.jar "
                    + "for KEY_ IDs.");

        cmd = new Command("SetTextHighlightColors", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.COLOR);
        field.setName("colors ...");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetTextHighlights", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("highlights ...");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetTooltip", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("text");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetTooltipHighlightColors", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.COLOR);
        field.setName("colors");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetTooltipHighlights", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("highlights");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowDefaultVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);

        cmd = new Command("ShowImageVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("image_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowImageVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("category");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.STRING);
        field.setName("image_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowPersonVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("minimal_mode");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("person_id");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>
        cmd.setNotes("Person must have been added to "
                    + "ImportantPeople at some point.");

        cmd = new Command("ShowPersonVisual", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("minimal_mode");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("ShowPic", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("image_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SubCredits", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("credits");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("TakeRepCheck", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("faction_id");
        cmd.getFields().add(field);

        field = new CommandField(Command.FieldType.KEY);
        field.setName("$result");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddRaidObjective", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("icon_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("name");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("danger_level");
        field.getOptions().add("None");
        field.getOptions().add("Minimal");
        field.getOptions().add("Low");
        field.getOptions().add("Medium");
        field.getOptions().add("High");
        field.getOptions().add("Extreme");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("xp");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("trigger_on_success");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("tooltip");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddBarEvent", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_text");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.STRING);
        field.setName("blurb");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("RemoveBarEvent", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("SetEnabled", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("option_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("enabled");
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MakeOtherFleetImportant", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("reason");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.BOOLEAN);
        field.setName("important");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.FLOAT);
        field.setName("expire");
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("AddAbility", rulesetId, ShowOn.SCRIPT);
        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.STRING);
        field.setName("ability_id");
        cmd.getFields().add(field);
        field = new CommandField(Command.FieldType.INTEGER);
        field.setName("slot_index");
        field.setMin(0);
        field.setOptional(true);
        cmd.getFields().add(field);
        //</editor-fold>

        cmd = new Command("MarketCMD", rulesetId, ShowOn.BOTH);
//        commands.add(cmd);
        //<editor-fold defaultstate="collapsed" desc="fields">
        field = new CommandField(Command.FieldType.ENUM);
        field.setName("command");
        List<String> options = field.getOptions();
        options.add("showDefenses");
        options.add("goBackToDefenses");
        options.add("engage");
        options.add("raidMenu");
        options.add("raidNonMarket");
        options.add("raidValuable");
        options.add("raidDisrupt");
        options.add("raidConfirm");
        options.add("raidConfirmContinue");
        options.add("raidNeverMind");
        options.add("addContinueToRaidResultOption");
        options.add("raidResult");
        options.add("bombardMenu");
        options.add("bombardTactical");
        options.add("bombardSaturation");
        options.add("bombardConfirm");
        options.add("bombardResult");
        options.add("bombardNeverMind");
        options.add("checkDebtEffect");
        options.add("applyDebtEffect");
        options.add("checkMercsLeaving");
        options.add("convinceMercToStay");
        options.add("mercLeaves");
        cmd.getFields().add(field);
        //</editor-fold>

        commands.sort(new Comparator<Command>() {
            @Override
            public int compare(Command o1, Command o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        return commands;
    }
}
