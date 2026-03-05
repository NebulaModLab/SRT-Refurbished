/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

/**
 * @author SafariJohn
 * Note to self: implement the Galil rule (skipping sections that are known to match) at some point.
 */
public class BoyerMooreParser {
    private final char[] pattern;
    private final int patternLength;
    private final int[] alphaDelta;
    private final int[] rprDelta;

    /**
     * @param pattern The string this parser searches for.
     */
    public BoyerMooreParser(String pattern) {
        this.pattern = pattern.toCharArray();
        patternLength = pattern.length();

        alphaDelta = constructAlphaTable();
        rprDelta = constructRPRTable();
    }

    /**
     * The alphabet table tells how far to shift based on whether a given
     * character is in pattern. If it is, the shift is based on how far
     * the rightmost occurrence of the character is from the right end
     * of the pattern.
     *
     * To reduce the length of the array, this method hashes each character
     * into a character in the alphabet by only using its lowest 8 bits.
     *
     * @return A 1D reference table of shift distances.
     */
    private int[] constructAlphaTable() {
        int[] delta = new int[256];

        for (int i = 0; i < (256); i++) {
            delta[i] = patternLength;
        }

        if (patternLength == 0) return delta;

        for (int i = 0; i < patternLength; i++) {
            delta[getReducedCharIndex(pattern[i])] = patternLength - (i + 1);
        }

        return delta;
    }

    private int getReducedCharIndex(char ch) {
        return (ch & 0x000000FF);
    }

    /**
     * The Rightmost Plausible Reoccurrence is a repetition of the last X
     * characters (or suffix) of pattern. The RPR table tells, based on the given
     * index, how far to shift to align the RPR with the current search index.
     *
     * @return A 1D reference table of shift distances.
     */
    private int[] constructRPRTable() {
        int[] delta = new int[patternLength];

        for (int i = 0; i < patternLength; i++) {
            int k = patternLength - 1;
            int tmp = k;
            boolean unity = false;
            while (!unity || (k >= 0 && pattern[k] == pattern[i])) {
                if (i + 1 == patternLength) break;
                if (k >= i) {
                    k--;
                    continue;
                }

                unity = checkUnity(i, k);
                tmp = k--;
            }

            delta[i] = patternLength - tmp;
        }

        return delta;
    }

    /**
     * The two sub-patterns are in "unity" if each character matches the
     * character at the same index in the other sub-pattern and/or the
     * character in the k sub-pattern does not occur in pattern.
     *
     * @param i The index of the suffix.
     * @param k The index of the sub-pattern to compare.
     *
     * @return Whether the suffix at i is in unity with
     * the sub-pattern at k.
     */
    private boolean checkUnity(int i, int k) {
        boolean unity = true;

        for (int j = i + 1; j < patternLength; j++) {
            if (k < 0) {
                k++;
                continue;
            }

            if (pattern[j] != pattern[k] && alphaDelta[getReducedCharIndex(pattern[k])] != patternLength) {
                unity = false;
            }

            k++;

            if (!unity) break;
        }

        return unity;
    }

    /**
     * Searches the given string for the pattern given to the parser's
     * constructor. The calling code must handle searching for
     * multiple occurrences.
     *
     * @param source The string to be searched.
     *
     * @return The index of the first occurrence of the pattern. Or -1
     * if pattern doesn't exist in source.
     */
    public int search(String source) {
        if (patternLength > source.length()) return -1;

        int index = patternLength - 1;
        char[] s = source.toCharArray();

        int[] largeDelta = alphaDelta;
        int large = source.length() + patternLength + 1; // Used to break the "fast" loop.
        largeDelta[getReducedCharIndex(pattern[patternLength - 1])] = large;
        while (true) {
            if (index >= source.length()) return -1;

            // Perform a "fast" search first - only checking for the last character of the pattern.
            index += largeDelta[getReducedCharIndex(s[index])];

            if (index < source.length()) continue;

            if (index < large) return -1;

            index = index - large;

            int j = patternLength - 1;
            if (j == 0) return index; // If the pattern only has one character.

            // Perform a "slow" search - checking each character in turn.
            while (s[index] == pattern[j]) {
                if (j == 0) return index;

                j--;
                index--;
            }

            index += Math.max(alphaDelta[getReducedCharIndex(s[index])], rprDelta[j + 1]);
        }
    }
}
