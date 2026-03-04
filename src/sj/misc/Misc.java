/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.misc;

import java.util.*;

/**
 * Author: SafariJohn
 */
public class Misc {

    public static List<Character> toCharacterList(String s) {
        List<Character> l = new ArrayList<>();

        for (int i = 0; i < s.length(); i++) {
            l.add(new Character(s.charAt(i)));
        }

        return l;
    }

    public static String toString(List<Character> l) {
        String s = "";

        for (Character c : l) {
            s += c;
        }

        return s;
    }

    // Returns number with zeroes added to the front, if necessary, to bring it to 3 digits.
    public static String threeDigitFormat(int number) {
        if (number >= 100) return "" + (number);
        if (100 > number && number >= 10) return "0" + (number);

        return "00" + (number);
    }
}
