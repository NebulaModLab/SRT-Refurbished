package sj.editor.ui;

import com.inet.jortho.SpellChecker;
//import io.github.geniot.jortho.SpellChecker;
import java.io.File;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;
import sj.editor.MainWindow;
import sj.editor.data.*;

/**
 * @author SafariJohn (original SRT)
 */
public class SpellcheckManager {
    private static final Logger logger = Logger.getLogger(SpellcheckManager.class.getName());

    private static boolean enabled = true;
    private static final HashMap registry = new HashMap();

    public static void register(JTextComponent c, String key) {
        register(c, key, true, true, true, true);
    }

    public static void register(JTextComponent c, String key, boolean hasPopup, boolean submenu,
                boolean hasShortKey, boolean hasAutoSpell) {
        if (registry.containsKey(key)) return;

        logger.log(Level.FINE, "Registering {0} with spellchecker.", key);
        RegisteredComp comp = new RegisteredComp(c, hasPopup, submenu, hasShortKey, hasAutoSpell);
        registry.put(key, comp);

        if (enabled) SpellChecker.register(c, hasPopup, submenu, hasShortKey, hasAutoSpell);
    }

    public static void unregister(String key) {
        if (!registry.containsKey(key)) return;

        RegisteredComp comp = (RegisteredComp) registry.get(key);
        registry.remove(key);

        if (enabled) SpellChecker.unregister(comp.c);
    }

    public static void enableSpellchecking(boolean enable) {
        if (enabled == enable) return;

        enabled = enable;
        if (enabled) {
            for (Object key : registry.keySet()) {
                RegisteredComp comp = (RegisteredComp) registry.get(key);
                SpellChecker.register(comp.c, comp.hasPopup, comp.submenu,
                            comp.hasShortKey, comp.hasAutoSpell);
            }
        } else {
            for (Object key : registry.keySet()) {
                RegisteredComp comp = (RegisteredComp) registry.get(key);
                SpellChecker.unregister(comp.c);
            }
        }
    }

    public static void updateSpellchecker() {
        if (!enabled) return;

        SpellChecker.setCustomDictionaryProvider(new SRTDictionaryProvider());

        Settings settings = MainWindow.getInstance().getSettings();
        String lang = settings.getLanguage().getLanguage();

        // Trying to stop a memory leak
        // Root issue still exists, but this seems to have worked
        if (SpellChecker.getCurrentLocale() != null
                    && SpellChecker.getCurrentLocale().equals(settings.getLanguage())
                    && SpellChecker.isDictionaryLoaded()) return;

        String langFile = SearchManager.DICTIONARY_PATH + "/dictionary_" + lang + ".ortho";
        String available = settings.getAvailableLanguages().toString();
        available = available.replace("[", "");
        available = available.replace("]", "");
        try {
            if (new File(langFile).exists()) {
                SpellChecker.registerDictionaries(new URL("file", null, langFile), available, lang);
            } else {
                String text = "Spellchecking disabled! " + langFile + " not found!";
                JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE);
                settings.setLanguage(null);
                enableSpellchecking(false);
            }
        } catch (MalformedURLException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }
    }


    private static class RegisteredComp {
        JTextComponent c;
        boolean hasPopup, submenu, hasShortKey, hasAutoSpell;

        public RegisteredComp(JTextComponent c, boolean hasPopup, boolean submenu,
                    boolean hasShortKey, boolean hasAutoSpell) {
            this.c = c;
            this.hasPopup = hasPopup;
            this.submenu = submenu;
            this.hasShortKey = hasShortKey;
            this.hasAutoSpell = hasAutoSpell;
        }
    }
}
