/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import com.inet.jortho.CustomDictionaryProvider;
//import io.github.geniot.jortho.CustomDictionaryProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sj.editor.data.commands.Command;

/**
 * @author SafariJohn
 */
public class SRTDictionaryProvider implements CustomDictionaryProvider {
    private static final Logger logger = Logger.getLogger(SRTDictionaryProvider.class.getName());

    @Override
    public Iterator<String> getWords(Locale locale) {
        List<String> list = new ArrayList<>();

        File file = new File(SearchManager.DICTIONARY_PATH + "/starsector_dictionary.txt");
        try {
            if (file.exists()) list.addAll(Files.readAllLines(file.toPath(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }

        // Load data dynamically from RulesetsManager.
        Ruleset vanilla = RulesetsManager.getVanilla();
        list.addAll(vanilla.getTriggers());
        list.addAll(vanilla.getCFunctions());
        list.addAll(vanilla.getCVariables());
        list.addAll(vanilla.getSFunctions());
        list.addAll(vanilla.getSVariables());
        list.addAll(vanilla.getTVariables());

        for (Command com : vanilla.getCommands()) {
            list.add(com.getName());
        }

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            list.addAll(ruleset.getTriggers());
            list.addAll(ruleset.getCFunctions());
            list.addAll(ruleset.getCVariables());
            list.addAll(ruleset.getSFunctions());
            list.addAll(ruleset.getSVariables());
            list.addAll(ruleset.getTVariables());

            for (Command com : ruleset.getCommands()) {
                list.add(com.getName());
            }
        }

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).trim();

            if (s.startsWith("$")) s = s.substring(1);

            list.set(i, s);
        }

        // Remove duplicates
        Set<String> set = new HashSet<>(list);
        list.retainAll(set);

        return list.iterator();
    }
}
