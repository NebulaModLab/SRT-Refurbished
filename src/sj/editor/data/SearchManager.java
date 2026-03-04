/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

/**
 * Author: SafariJohn
 */
public class SearchManager {
    public static String DICTIONARY_PATH = "spellcheck";

    private static String searchPattern = "";
    private static String replacement = "";
    private static SearchResult searchResult = null;
    private static int tabIndex = 0;

    private static boolean searchingFlag = false;
    private static boolean replaceChange = false;
    private static boolean replaceStart = false;

    public static String getSearchPattern() {
        return searchPattern;
    }

    public static void setSearchPattern(String pattern) {
        if (pattern == null) searchPattern = "";
        else searchPattern = pattern;
    }


    public static String getReplacement() {
        return replacement;
    }

    public static void setReplacement(String replacement) {
        SearchManager.replacement = replacement;
    }


    public static SearchResult getSearchResult() {
        return searchResult;
    }

    /**
     * IMPORTANT: Assume only the first index in result will be used.
     * @param result
     */
    public static void setSearchResult(SearchResult result) {
        searchResult = result;
    }


    /**
     * @return Which rule data tab (Conditions, Script, etc.) is visible.
     */
    public static int getTabIndex() {
        return tabIndex;
    }

    /**
     * Should only be used by RuleDataTabbedPane.
     * @param tabIndex
     */
    public static void setTabIndex(int tabIndex) {
        SearchManager.tabIndex = tabIndex;
    }


    public static boolean isSearchingFlagRaised() {
        return searchingFlag;
    }

    public static void setSearchingFlag(boolean raised) {
        searchingFlag = raised;
    }


    public static boolean catchReplaceChange() {
        boolean canCatch = replaceChange;
        replaceChange = false;
        return canCatch;
    }

    public static void throwReplaceChange() {
        replaceChange = true;
    }


    public static boolean isReplacementStarting() {
        boolean temp = replaceStart;
        replaceStart = false;
        return temp;
    }

    public static void startingReplacement() {
        SearchManager.replaceStart = true;
    }
}
