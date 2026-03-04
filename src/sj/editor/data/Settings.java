/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 * Author: SafariJohn
 */
public class Settings {
    private final List<String> previousRulesets;

    private File saveLocation;
    private File modsLocation;

    // Safe mode requires changes to be committed to affect the original CSV.
    private boolean safeMode;

    private Locale language;
    private final List<Locale> availableLanguages;

    // Theme(s)
    private LookAndFeel lookAndFeel;
    private final List<LookAndFeel> lookAndFeels;

    public Settings() {
        previousRulesets = new ArrayList<>();

        saveLocation = new File("rulesets");
        modsLocation = null;

        safeMode = false;

        language = Locale.ENGLISH;
        availableLanguages = new ArrayList<>();
        availableLanguages.add(language);

        lookAndFeel = null;
        lookAndFeels = new ArrayList<>();
        lookAndFeels.add(new FlatDarkLaf());
        lookAndFeels.add(new FlatDarculaLaf());
        lookAndFeels.add(new FlatIntelliJLaf());
        lookAndFeels.add(new FlatLightLaf());
    }

    /**
     * Returns the names of the rulesets that were active
     * last time this program closed.
     *
     * @return Active rulesets from last run.
     */
    public List<String> getPreviousRulesets() {
        return previousRulesets;
    }

    public File getSaveLocation() {
        return saveLocation;
    }

    public void setSaveLocation(File saveLocation) {
        this.saveLocation = saveLocation;
    }

    public File getModsLocation() {
        return modsLocation;
    }

    public void setModsLocation(File modsLocation) {
        this.modsLocation = modsLocation;
    }

    public boolean isSafeMode() {
        return safeMode;
    }

    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
    }

    public boolean isSpellchecking() {
        return language != null;
    }

    /**
     * This is currently only used for spellchecking.
     * @return
     */
    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
        if (!availableLanguages.contains(language)) availableLanguages.add(language);
    }

    public List<Locale> getAvailableLanguages() {
        return availableLanguages;
    }

    public LookAndFeel getLookAndFeel() {
        return lookAndFeel;
    }

    public void setLookAndFeel(LookAndFeel lookAndFeel) {
        this.lookAndFeel = lookAndFeel;
        if (!lookAndFeels.contains(lookAndFeel)) lookAndFeels.add(lookAndFeel);
    }

    public List<LookAndFeel> getLookAndFeels() {
        return lookAndFeels;
    }
}
