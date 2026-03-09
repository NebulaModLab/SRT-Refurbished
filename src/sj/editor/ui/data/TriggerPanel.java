/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.Ruleset;
import sj.editor.data.RulesetsManager;
import sj.editor.data.SearchManager;
import sj.editor.data.SearchResult;
import sj.editor.data.rules.RuleFile;
import sj.editor.data.rules.RulesManager;
import sj.editor.data.commands.TFVContainer;
import sj.editor.ui.SRTInterface;
import sj.misc.Misc;

/**
 * @author SafariJohn (original SRT)
 */
public class TriggerPanel extends JPanel implements SRTInterface {
    private static final Logger logger = Logger.getLogger(TriggerPanel.class.getName());

    private final JLabel triggerLabel = new JLabel("Trigger:");
    private final JTextField triggerField = new JTextField();

    private final JLabel existingLabel = new JLabel("Known Triggers:");
    private final JLabel filterLabel = new JLabel("Filter:");
    private final JTextField filterField = new JTextField();
    private final JScrollPane existingScrollPane = new JScrollPane();
    private final JList existingList = new JList();
    private final DefaultListModel existingListModel = new DefaultListModel();

    private boolean writeLock;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;
    private String filter;

    public TriggerPanel() {
        logger.log(Level.FINE, "Constructing");
        writeLock = false;
        suppressChange = false;
        changeTime = 0;
        updateLater = false;
        filter = "";

        logger.log(Level.FINE, "Constructing DnD transfer handler.");
        TransferHandler dnd = new TFVDnDHandler();

//        logger.log(Level.FINER, "Adding trigger field document listener.");
        triggerField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (suppressChange) return;

                String newTrigger = "";
                try {
                    newTrigger = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                triggerUpdated(newTrigger);
            }
            //</editor-fold>
        });

        triggerField.setToolTipText("The trigger defines what type of action may cause this rule to be executed.");

//        logger.log(Level.FINER, "Adding filter transfer handler and document listener.");
        filterField.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
        });
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    filter = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                refreshInterface();
            }
            //</editor-fold>
        });

        filterField.setToolTipText("Filtering is not case sensitive and does not detect ruleset names.");

//        logger.log(Level.FINE, "Setting up list of triggers.");
        existingScrollPane.setBorder(BorderFactory.createEtchedBorder());
        existingScrollPane.setViewportView(existingList);

        existingList.setDragEnabled(true);
        existingList.setTransferHandler(dnd);
        existingList.setToolTipText("Press the delete key to remove the selected triggers from the list.");
        existingList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) deleteTrigger();
            }
        });
        existingList.setModel(existingListModel);

//        logger.log(Level.FINE, "Laying out panel.");
        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(triggerLabel)
                    .addComponent(triggerField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(existingLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(filterLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filterField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(existingScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(triggerLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(triggerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(existingLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filterLabel)
                    .addComponent(filterField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(existingScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        setLayout(layout);
    }

    private void triggerUpdated(String newTrigger) {
        if (!RulesManager.getActiveRule().getRule().getTrigger().equals(newTrigger)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setTrigger(newTrigger);

            boolean canRefresh = System.currentTimeMillis() > changeTime;
            changeTime = System.currentTimeMillis() + 100;

            writeLock = true;
            if (canRefresh) MainWindow.getInstance().refreshAllData();
            else {
                updateLater = true;
                java.awt.EventQueue.invokeLater(getRefreshLaterRunnable());
            }
        }
    }

    private Runnable getRefreshLaterRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                if (updateLater && System.currentTimeMillis() > changeTime) {
                    updateLater = false;
                    MainWindow.getInstance().refreshAllData();
                } else if (updateLater) {
                    java.awt.EventQueue.invokeLater(this);
                }
            }
        };
    }

    private void deleteTrigger() {
        List<TFVContainer> triggers = existingList.getSelectedValuesList();
        for (TFVContainer trigger : triggers) {
            Ruleset ruleset = RulesetsManager.getRuleset(trigger.getRulesetId());
            if (ruleset != null) {
                ruleset.getTriggers().remove(trigger.getTFV());
                ruleset.getTempTriggers().remove(trigger.getTFV());
            }
        }

        MainWindow.getInstance().refreshAllData();
    }

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();
        boolean canRead = true;

        if (writeLock || MainWindow.getInstance().isSummaryLock()) {
            writeLock = false;
            canRead = false;
        }
        else {
            suppressChange = true;
            triggerField.setText(activeFile.getRule().getTrigger());
            suppressChange = false;
        }

        if (activeFile.getRulesetId() < 0 || !activeFile.isRule()) triggerField.setEditable(false);
        else triggerField.setEditable(true);

        //<editor-fold defaultstate="collapsed" desc="Read New Trigger">
        String activeTrigger = activeFile.getRule().getTrigger();

        if (RulesetsManager.contains(activeTrigger, RulesetsManager.DataType.TRIGGER, activeFile.getRulesetId())
                    || activeTrigger.startsWith("#") || activeTrigger.isEmpty())
        { canRead = false; }

        if (activeFile.getRulesetId() >= 0 && canRead) {
            Ruleset ruleset = RulesetsManager.getRuleset(activeFile.getRulesetId());
            ruleset.getTempTriggers().add(activeTrigger);
        }

        existingListModel.clear();
        for (String trigger : RulesetsManager.getVanilla().getTriggers()) {
            if (!trigger.toLowerCase().contains(filter.toLowerCase())) continue;

            existingListModel.addElement(new TFVContainer(trigger, RulesetsManager.getVanilla().getId()));
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            List<String> triggers = new ArrayList<>();
            triggers.addAll(ruleset.getTriggers());
            triggers.addAll(ruleset.getTempTriggers());

            for (String trigger : triggers) {
                if (!trigger.toLowerCase().contains(filter.toLowerCase())) continue;

                existingListModel.addElement(new TFVContainer(trigger, ruleset.getId()));
            }
        }

        //<editor-fold defaultstate="collapsed" desc="Find/Replace">
        SearchResult result = SearchManager.getSearchResult();

        if (result != null && result.getColumn() == SearchResult.Column.TRIGGER) {
            if (SearchManager.isSearchingFlagRaised()) {
                triggerField.getCaret().setSelectionVisible(true);
                triggerField.setSelectionStart(result.getIndexes().get(0));
                triggerField.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }

            if (SearchManager.catchReplaceChange()) {
                // Convert String to char[], then to Character[], then to List<Character>
                List<Character> text = Misc.toCharacterList(triggerField.getText());

                char[] replacement = SearchManager.getReplacement().toCharArray();


                // Replace the pattern, character by character
                int index = result.getIndexes().get(0);
                int searchLength = SearchManager.getSearchPattern().length();
                int removalIndex = -1;
                for (int i = index; i < index + searchLength; i++) {
                    // Exception for if replacement is smaller.
                    if (i - index >= replacement.length) {
                        removalIndex = i;
                        break;
                    }

                    // Exception for if replacement is larger.
                    if (i - index == searchLength - 1) {
                        text.set(i, replacement[i - index]);
                        i++;

                        for (int j = i - index; j < replacement.length; j++) {
                            text.add(i, replacement[j]);
                            i++;
                        }
                        break;
                    }

                    // Normal replacement
                    text.set(i, replacement[i - index]);
                }

                if (removalIndex >= 0) {
                    for (int i = removalIndex + (searchLength - replacement.length) - 1;
                                i >= removalIndex; i--) {
                        text.remove(i);
                    }
                }

                // Convert back to String.
                String newText = Misc.toString(text);
                if (!RulesManager.getActiveRule().getRule().getTrigger().equals(newText)) {

                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    RulesManager.getActiveRule().getRule().setTrigger(newText);

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();

                    suppressChange = true;
                    triggerField.setText(activeFile.getRule().getTrigger());
                    suppressChange = false;
                }
            }
        } else if (!triggerField.isFocusOwner()) {
            triggerField.getCaret().setSelectionVisible(false);
        }
        //</editor-fold>
    }

    @Override
    public void getPreferences(Preferences prefs) {}

    @Override
    public void setPreferences(Preferences prefs) {}

}
