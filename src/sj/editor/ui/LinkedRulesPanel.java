/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.event.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;
import sj.editor.ui.dialogs.forceblock.ForceBlockDialog;

/**
 * @author SafariJohn
 */
public class LinkedRulesPanel extends JSplitPane implements SRTInterface {
    private static final Logger logger = Logger.getLogger(LinkedRulesPanel.class.getName());

    private final JPanel linkedFromPanel = new JPanel();
    private final JLabel linkedFromLabel = new JLabel("Triggered By:");
    private final JButton fromForceBlockButton = new JButton();
    private final JScrollPane linkedFromScrollPane = new JScrollPane();
    private final JList linkedFromList = new JList();
    private final DefaultListModel linkedFromListModel = new DefaultListModel();

    private final JPanel linkedToPanel = new JPanel();
    private final JLabel linkedToLabel = new JLabel("May Trigger:");
    private final JButton toForceBlockButton = new JButton();
    private final JList linkedToList = new JList();
    private final JScrollPane linkedToScrollPane = new JScrollPane();
    private final DefaultListModel linkedToListModel = new DefaultListModel();

    public LinkedRulesPanel() {
        super(JSplitPane.VERTICAL_SPLIT);
        logger.log(Level.FINE, "Constructing");
        setBorder(BorderFactory.createEmptyBorder());

        setLeftComponent(linkedFromPanel);
        setRightComponent(linkedToPanel);
        setDividerLocation(280);

        linkedFromList.setToolTipText("<html>Rules that may trigger this rule. Double click a rule to switch to it."
                    + "<br>Drag rules from the left panel to force them to appear in this list."
                    + "<br>Press the delete key to block the selected rules.</html>");
        linkedFromList.setModel(linkedFromListModel);
        linkedFromList.setDropMode(DropMode.ON);
        linkedFromList.setTransferHandler(new LinkFromTransferHandler());
        linkedFromList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    RuleFile rule = (RuleFile) linkedFromList.getSelectedValue();

                    RulesManager.setActiveRule(rule);
                    MainWindow.getInstance().throwLinkChange();
                    MainWindow.getInstance().refreshAllData();
                }
            }
        });
        linkedFromList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) { blockFromRules(); }
            }
        });

        linkedFromScrollPane.setBorder(BorderFactory.createEtchedBorder());
        linkedFromScrollPane.setViewportView(linkedFromList);

        fromForceBlockButton.setText("+");
        fromForceBlockButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        fromForceBlockButton.setToolTipText("View the lists of forced and blocked IDs for the list below.");
        fromForceBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { fromForce(); }
        });

        GroupLayout layout = new GroupLayout(linkedFromPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(linkedFromLabel)
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(fromForceBlockButton))
                        .addComponent(linkedFromScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(linkedFromLabel)
                        .addComponent(fromForceBlockButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(linkedFromScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        linkedFromPanel.setLayout(layout);


        linkedToList.setToolTipText("<html>Rules that this rule may trigger. Double click a rule to switch to it."
                    + "<br>Drag rules from the left panel to force them to appear in this list."
                    + "<br>Press the delete key to block the selected rules.</html>");
        linkedToList.setModel(linkedToListModel);
        linkedToList.setDropMode(DropMode.ON);
        linkedToList.setTransferHandler(new LinkToTransferHandler());
        linkedToList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    RuleFile rule = (RuleFile) linkedToList.getSelectedValue();

                    RulesManager.setActiveRule(rule);
                    MainWindow.getInstance().throwLinkChange();
                    MainWindow.getInstance().refreshAllData();
                }
            }
        });
        linkedToList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) { blockToRules(); }
            }
        });

        linkedToScrollPane.setBorder(BorderFactory.createEtchedBorder());
        linkedToScrollPane.setViewportView(linkedToList);

        toForceBlockButton.setText("+");
        toForceBlockButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        toForceBlockButton.setToolTipText("View the lists of forced and blocked IDs for the list below.");
        toForceBlockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { toForce(); }
        });

        layout = new GroupLayout(linkedToPanel);
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(linkedToLabel)
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(toForceBlockButton))
                        .addComponent(linkedToScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(linkedToLabel)
                        .addComponent(toForceBlockButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(linkedToScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        linkedToPanel.setLayout(layout);
    }

    private void blockFromRules() {
        RulesManager.backupActiveFile();

        RuleFile activeRule = RulesManager.getActiveRule();
        List<RuleFile> selected = linkedFromList.getSelectedValuesList();

        for (RuleFile file : selected) {
            activeRule.getFromWhitelist().remove(file.getRule().getId());
            activeRule.getFromBlacklist().add(file.getRule().getId());
        }

        MainWindow.getInstance().refreshAllData();
    }

    private void blockToRules() {
        RulesManager.backupActiveFile();

        RuleFile activeRule = RulesManager.getActiveRule();
        List<RuleFile> selected = linkedToList.getSelectedValuesList();

        for (RuleFile file : selected) {
            activeRule.getToWhitelist().remove(file.getRule().getId());
            activeRule.getToBlacklist().add(file.getRule().getId());
        }

        MainWindow.getInstance().refreshAllData();
    }

    private void fromForce() {
        new ForceBlockDialog(true).setVisible(true);
    }

    private void toForce() {
        new ForceBlockDialog(false).setVisible(true);
    }

    private List<RuleFile> getRules(DirectoryFile dir) {
        List<RuleFile> rules = new ArrayList<>();

        for (DirectoryFile branch : dir.getBranches()) {
            rules.addAll(getRules(branch));
        }

        for (RuleFile leaf : dir.getLeaves()) {
            if (leaf.isComment() || leaf.isSpacer()) continue;

            rules.add(leaf);
        }

        return rules;
    }

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();

        if (!activeFile.isRule() || activeFile.getRulesetId() < 0) {
            fromForceBlockButton.setEnabled(false);
            toForceBlockButton.setEnabled(false);
        } else {
            fromForceBlockButton.setEnabled(true);
            toForceBlockButton.setEnabled(true);
        }


        if (!activeFile.isRule() || activeFile.getRule().getTrigger().isEmpty()) {
            linkedFromListModel.clear();
            linkedToListModel.clear();
            return;
        }


        List<RuleFile> ruleFiles = new ArrayList<>();
        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            ruleFiles.addAll(getRules(ruleset.getRootDirectory()));
        }

        calculateLinks(activeFile, ruleFiles);
    }

    private void calculateLinks(RuleFile activeFile, List<RuleFile> ruleFiles) {
        RuleBean activeRule = activeFile.getRule();

        HashMap fromConfig = configureFromCheck(activeRule);
        HashMap toConfig = configureToCheck(activeRule);

        // Check links
        List<RuleFile> fromRules = new ArrayList<>();
        List<RuleFile> toRules = new ArrayList<>();
        for (RuleFile file : ruleFiles) {
            if (file.equals(activeFile)) continue;

            if (checkFrom(activeFile, file, fromConfig)) fromRules.add(file);
            if (checkTo(activeFile, file, toConfig)) toRules.add(file);
        }

        linkedFromListModel.clear();
        for (RuleFile file : fromRules) {
            linkedFromListModel.add(linkedFromListModel.size(), file);
        }

        linkedToListModel.clear();
        for (RuleFile file : toRules) {
            linkedToListModel.add(linkedToListModel.size(), file);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Configure Link Checks">
    private HashMap configureFromCheck(RuleBean activeRule) {
        HashMap config = new HashMap();

        boolean dialogSelected = activeRule.getTrigger().equals("DialogOptionSelected");

        String dialogOption = "";
        if (dialogSelected) {
            // What dialog option to check for
            if (activeRule.getConditions().contains("$option ==")
                        || activeRule.getConditions().contains("$option==")) {
                dialogOption = activeRule.getConditions().replaceAll("[\\t \\n\\S]*\\$option[ ]*==[ ]*", "");
                dialogOption = dialogOption.replaceAll("[ ]*\\n[\\t \\n\\S]*", "");
            } else {
                dialogSelected = false;
            }
        }

        config.put("dialogSelected", dialogSelected);
        config.put("dialogOption", dialogOption);

        return config;
    }

    private HashMap configureToCheck(RuleBean activeRule) {
        HashMap config = new HashMap();

        boolean hasFire = activeRule.getScript().contains("FireBest") || activeRule.getScript().contains("FireAll");
        List<String> triggers = new ArrayList<>();
        boolean hasOptions = !activeRule.getOptions().isEmpty();
        List<String> options = new ArrayList<>();

        config.put("hasFire", hasFire);
        config.put("hasOptions", hasOptions);

        // What triggers to check for, excluding DialogOptionSelected
        if (hasFire) {
            for (String trigger : Arrays.asList(activeRule.getScript().split("\\n"))) {
                if (!(trigger.contains("FireBest") || trigger.contains("FireAll"))) continue;

                triggers.add(trigger.replaceFirst(" *\\S+ ", ""));
            }
        }

        config.put("triggers", triggers);

        // What options to check for
        if (hasOptions) {
            for (String option : Arrays.asList(activeRule.getOptions().split("\\n"))) {
                for (int i = -10; i < 1000; i++) {
                    if (option.startsWith(i + ":")) {
                        option = option.replaceFirst(i + ":", "");
                        break;
                    }
                }

                option = option.replaceFirst(":.+", "");

                options.add(option);
            }
        }

        config.put("options", options);

        return config;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Link Checks">
    private boolean checkFrom(RuleFile activeFile, RuleFile file, HashMap fromConfig) {
        RuleBean activeRule = activeFile.getRule();
        RuleBean rule = file.getRule();

        // Check forced rules
        for (String id : activeFile.getFromWhitelist()) {
            if (rule.getId().equals(id)) {
                return true;
            }
        }

        // Check blocked rules
        for (String id : activeFile.getFromBlacklist()) {
            if (rule.getId().equals(id)) {
                return false;
            }
        }

        if (rule.getId().startsWith("#")) return false;

        boolean dialogSelected = (boolean) fromConfig.get("dialogSelected");
        String dialogOption = (String) fromConfig.get("dialogOption");

        if (dialogSelected) {
            if (rule.getOptions().isEmpty()) return false;
            else if (!rule.getOptions().contains(dialogOption)) return false;
        } else {
            if (!rule.getScript().contains(activeRule.getTrigger())) return false;
        }

        boolean result = compareConditions(activeRule, rule);
        if (result == false) return false;

        return true;
    }

    private boolean checkTo(RuleFile activeFile, RuleFile file, HashMap toConfig) {
        RuleBean activeRule = activeFile.getRule();
        RuleBean rule = file.getRule();

        boolean foundOption = false;
        boolean foundTrigger = false;

        // Check forced rules
        for (String id : activeFile.getToWhitelist()) {
            if (rule.getId().equals(id)) {
                return true;
            }
        }

        // Check blocked rules
        for (String id : activeFile.getToBlacklist()) {
            if (rule.getId().equals(id)) {
                return false;
            }
        }

        if (rule.getId().startsWith("#")) return false;

        List<String> options = (List) toConfig.get("options");
        List<String> triggers = (List) toConfig.get("triggers");

        if (rule.getTrigger().equals("DialogOptionSelected")) {
            if (!options.isEmpty() && rule.getConditions().contains("$option")) {
                for (String option : options) {
                    if (rule.getConditions().contains(option)) {
                        foundOption = true;
                        break;
                    }
                }
            }
        } else {
            for (String trigger : triggers) {
                if (rule.getTrigger().equals(trigger)) {
                    foundTrigger = true;
                    break;
                }
            }
        }

        if (!foundTrigger && !foundOption) return false;

        boolean result = compareConditions(activeRule, rule);
        if (result == false) return false;

        return true;
    }

    private boolean compareConditions(RuleBean activeRule, RuleBean rule) {
        for (String line : rule.getConditions().split("\n")) {
            boolean negated = false;
            if (line.startsWith("!")) negated = true;

            String token = "";
            String right = "";
            if (line.contains("==")) {
                token = line.replaceFirst("==.+", "");
                right = line.replaceFirst(".+==", "");
            } else if (line.contains("!=")) {
                token = line.replaceFirst("!=.+", "");
                right = line.replaceFirst(".+!=", "");
                negated = true;
            }

            if (line.contains("$faction.isNeutralFaction")) {
                token = "$faction.id";
                right = "neutral";
            }

            for (String aLine : activeRule.getConditions().split("\n")) {
                if (!negated && aLine.equals("!" + line)) return false;
                if (negated && ("!" + aLine).equals(line)) return false;

                if (token.isEmpty()) continue;
                if (!aLine.startsWith(token)) continue;

                // Get right side
                String aRight = "";
                if (aLine.contains("==")) {
                    aRight = line.replaceFirst(".+==", "");

                    if (!negated && right.equals(aRight)) continue;
                    if (!negated && !right.equals(aRight)) return false;
                    if (negated && right.equals(aRight)) return false;
                } else if (aLine.contains("!=")) {
                    aRight = line.replaceFirst(".+!=", "");

                    if (!negated && right.equals(aRight)) return false;
                }

                if (aLine.contains("$faction.isNeutralFaction") && token.equals("$faction.id")) {
                    if (right.equals("neutral")) {
                        if (!negated && aLine.startsWith("!")) return false;
                        if (negated && !aLine.startsWith("!")) return false;
                    } else {
                        if (!negated && !aLine.startsWith("!")) return false;
                    }
                }
            }
        }

        return true;
    }
    //</editor-fold>

    @Override
    public void getPreferences(Preferences prefs) {
        int dividerLoc = prefs.getInt("linkedDivider", 280);
        setDividerLocation(dividerLoc);
    }

    @Override
    public void setPreferences(Preferences prefs) {
        prefs.putInt("linkedDivider", getDividerLocation());
    }
}
