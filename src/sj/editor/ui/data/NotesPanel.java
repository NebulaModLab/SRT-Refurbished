/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import sj.editor.MainWindow;
import sj.editor.data.SearchManager;
import sj.editor.data.SearchResult;
import sj.editor.data.rules.RuleFile;
import sj.editor.data.rules.RulesManager;
import sj.editor.ui.SRTInterface;
import sj.editor.ui.SpellcheckManager;
import sj.misc.Misc;

/**
 * Author: SafariJohn
 */
public class NotesPanel extends JPanel implements SRTInterface {
    private static final Logger logger = Logger.getLogger(NotesPanel.class.getName());

    private final JLabel notesLabel = new JLabel("Notes:");
    private final JScrollPane notesScrollPane = new JScrollPane();
    private final JTextArea notesArea = new JTextArea();

    private boolean writeLock;
    private boolean suppressChange;
    private long changeTime;
    private boolean updateLater;

    public NotesPanel() {
        logger.log(Level.FINE, "Constructing");
        writeLock = false;
        suppressChange = false;
        changeTime = 0;
        updateLater = false;

        notesArea.getDocument().addDocumentListener(new DocumentListener() {
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

                String newNotes = "";
                try {
                    newNotes = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    logger.log(Level.INFO, ex.toString(), ex);
                }

                notesUpdated(newNotes);
            }
            //</editor-fold>
        });

        notesArea.setToolTipText("Anything put here is ignored by the game.");
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        SpellcheckManager.register(notesArea, "NOTES_AREA", true, false, true, true);

        notesScrollPane.setBorder(BorderFactory.createEtchedBorder());
        notesScrollPane.setViewportView(notesArea);

        GroupLayout layout = new GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup()
                    .addComponent(notesLabel)
                    .addComponent(notesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notesLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(notesScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        setLayout(layout);
    }

    private void notesUpdated(String newNotes) {
        if (!RulesManager.getActiveRule().getRule().getNotes().equals(newNotes)) {
            if (RulesManager.doBackup(RulesManager.getActiveRule().getId())) {
                RulesManager.backupActiveFile();
            }

            RulesManager.getActiveRule().getRule().setNotes(newNotes);

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

    @Override
    public void refreshInterface() {
        RuleFile activeFile = RulesManager.getActiveRule();

        if (writeLock || MainWindow.getInstance().isSummaryLock()) writeLock = false;
        else {
            suppressChange = true;
            notesArea.setText(activeFile.getRule().getNotes());
            suppressChange = false;
        }

        if (activeFile.getParentId() < 0) notesArea.setEditable(false);
        else notesArea.setEditable(true);

        if (SearchManager.isSearchingFlagRaised()) {
            SearchResult result = SearchManager.getSearchResult();

            if (result != null && result.getColumn() == SearchResult.Column.NOTES) {
                notesArea.getCaret().setSelectionVisible(true);
                notesArea.setSelectionStart(result.getIndexes().get(0));
                notesArea.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }
        } else if (!notesArea.isFocusOwner()) {
            notesArea.getCaret().setSelectionVisible(false);
        }

        //<editor-fold defaultstate="collapsed" desc="Find/Replace">
        SearchResult result = SearchManager.getSearchResult();

        if (result != null && result.getColumn() == SearchResult.Column.NOTES) {
            if (SearchManager.isSearchingFlagRaised()) {
                notesArea.getCaret().setSelectionVisible(true);
                notesArea.setSelectionStart(result.getIndexes().get(0));
                notesArea.setSelectionEnd(result.getIndexes().get(0) + SearchManager.getSearchPattern().length());
            }

            if (SearchManager.catchReplaceChange()) {
                // Convert String to char[], then to Character[], then to List<Character>
                List<Character> text = Misc.toCharacterList(notesArea.getText());

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
                if (!RulesManager.getActiveRule().getRule().getNotes().equals(newText)) {

                    if (SearchManager.isReplacementStarting()) RulesManager.backupActiveFile();

                    RulesManager.getActiveRule().getRule().setNotes(newText);

                    writeLock = true;
                    MainWindow.getInstance().refreshAllData();

                    suppressChange = true;
                    notesArea.setText(activeFile.getRule().getNotes());
                    suppressChange = false;
                }
            }
        } else if (!notesArea.isFocusOwner()) {
            notesArea.getCaret().setSelectionVisible(false);
        }
        //</editor-fold>
    }

    @Override
    public void getPreferences(Preferences prefs) {}

    @Override
    public void setPreferences(Preferences prefs) {}
}
