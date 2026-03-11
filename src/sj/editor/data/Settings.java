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
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import pn.flatlaf.themes.*;

import javax.swing.*;
import java.io.File;
import java.util.*;

/**
 * @author SafariJohn (original SRT), Purple Nebula (SRT Refurbished)
 */
public class Settings {
    private final List<String> previousRulesets;

    private File saveLocation;
    private File modsLocation;

    // Safe mode requires changes to be committed to affect the original CSV.
    private boolean safeMode;
    private boolean doRuleOverlapCheck;
    private boolean resetSizeLocation;
    private boolean resetDividers;

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
        doRuleOverlapCheck = false;
        resetSizeLocation = false;
        resetDividers = false;

        language = Locale.ENGLISH;
        availableLanguages = new ArrayList<>();
        availableLanguages.add(language);

        // Set up the themes for the theme picker
        lookAndFeel = null;
        lookAndFeels = new ArrayList<>();
        lookAndFeels.add(new FlatDarkLaf());
        lookAndFeels.add(new FlatDarkLafPurple());
        lookAndFeels.add(new FlatDarkLafRed());
        lookAndFeels.add(new FlatLightLaf());
        lookAndFeels.add(new FlatlafAoTD());
        lookAndFeels.add(new FlatlafPurpleNebula());
        lookAndFeels.add(new FlatlafRemnant());
        lookAndFeels.add(new WindowsLookAndFeel());
        lookAndFeels.add(new WindowsClassicLookAndFeel());
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

    public boolean doRuleOverlapCheck() {
        return doRuleOverlapCheck;
    }

    public void setDoRuleOverlapCheck(boolean doRuleOverlapCheck) {
        this.doRuleOverlapCheck = doRuleOverlapCheck;
    }

    public boolean doResetSizeLocation() {
        return resetSizeLocation;
    }

    public void setResetSizeLocation(boolean resetSizeLocation) {
        this.resetSizeLocation = resetSizeLocation;
    }

    public boolean doResetDividers() {
        return resetDividers;
    }

    public void setResetDividers(boolean resetDividers) {
        this.resetDividers = resetDividers;
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
