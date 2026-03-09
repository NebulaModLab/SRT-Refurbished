/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sj.editor.data.*;
import sj.editor.ui.data.*;

/**
 * @author SafariJohn (original SRT)
 */
public class RuleDataTabbedPane extends JTabbedPane implements SRTInterface {
    private static final Logger logger = Logger.getLogger(RuleDataTabbedPane.class.getName());

    public static int SUMMARY = 0;
    public static int TRIGGER = 1;
    public static int CONDITIONS = 2;
    public static int SCRIPT = 3;
    public static int TEXT = 4;
    public static int NOTES = 5;

    private final SummaryPanel summaryPanel;
    private final TriggerPanel triggerPanel;
    private final ConditionsPanel conditionsPanel;
    private final ScriptPanel scriptPanel;
    private final TextPanel textPanel;
    private final NotesPanel notesPanel;

    public RuleDataTabbedPane() {
        logger.log(Level.FINE, "Constructing");

        summaryPanel = new SummaryPanel();
        triggerPanel = new TriggerPanel();
        conditionsPanel = new ConditionsPanel();
        scriptPanel = new ScriptPanel();
        textPanel = new TextPanel();
        notesPanel = new NotesPanel();

        logger.log(Level.FINE, "Adding tabs.");
        addTab("Summary", summaryPanel);
        addTab("Trigger", triggerPanel);
        addTab("Conditions", conditionsPanel);
        addTab("Script", scriptPanel);
        addTab("Text", textPanel);
        addTab("Notes", notesPanel);

        logger.log(Level.FINE, "Adding change listener.");
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                refreshInterface(); // Clear writeLocks
                refreshInterface(); // Read triggers/functions/variables
            }
        });

    }

    @Override
    public void refreshInterface() {
        if (SearchManager.isSearchingFlagRaised()) {
            SearchResult result = SearchManager.getSearchResult();

            if (result != null) {
                switch (result.getColumn()) {
                    case ID: setSelectedIndex(SUMMARY);
                    break;
                    case TRIGGER: setSelectedIndex(TRIGGER);
                    break;
                    case CONDITIONS: setSelectedIndex(CONDITIONS);
                    break;
                    case SCRIPT: setSelectedIndex(SCRIPT);
                    break;
                    case TEXT:
                    case OPTIONS: setSelectedIndex(TEXT);
                    break;
                    case NOTES: setSelectedIndex(NOTES);
                    break;
                }
            }
        }

        SearchManager.setTabIndex(getSelectedIndex());

        summaryPanel.refreshInterface();
        triggerPanel.refreshInterface();
        conditionsPanel.refreshInterface();
        scriptPanel.refreshInterface();
        textPanel.refreshInterface();
        notesPanel.refreshInterface();
    }

    @Override
    public void getPreferences(Preferences prefs) {
        summaryPanel.getPreferences(prefs);
        triggerPanel.getPreferences(prefs);
        conditionsPanel.getPreferences(prefs);
        scriptPanel.getPreferences(prefs);
        textPanel.getPreferences(prefs);
        notesPanel.getPreferences(prefs);
    }

    @Override
    public void setPreferences(Preferences prefs) {
        summaryPanel.setPreferences(prefs);
        triggerPanel.setPreferences(prefs);
        conditionsPanel.setPreferences(prefs);
        scriptPanel.setPreferences(prefs);
        textPanel.setPreferences(prefs);
        notesPanel.setPreferences(prefs);
    }
}
