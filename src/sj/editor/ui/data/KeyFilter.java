/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author SafariJohn (original SRT)
 */
public class KeyFilter extends DocumentFilter {
    private int length;

    public KeyFilter(int length) {
        this.length = length;
    }

    @Override
    public void insertString(DocumentFilter.FilterBypass fb,
                int offset, String string, AttributeSet attr)
                throws BadLocationException {
        string = string.replaceAll(" ", "");
        if (offset == 0) {
            if (!string.startsWith("$")) string = "$" + string;
            if (string.length() == 1) string = "";
        }

        length += string.length();

        fb.insertString(offset, string, attr);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb,
                int offset, int length, String text,
                AttributeSet attrs)
                throws BadLocationException {
        text = text.replaceAll(" ", "");
        if (offset == 0) {
            if (!text.startsWith("$")) text = "$" + text;
            if (text.length() == 1) text = "";
        }

        this.length -= length;
        this.length += text.length();

        fb.replace(offset, length, text, attrs);
    }

    @Override
    public void remove(DocumentFilter.FilterBypass fb,
                int offset, int length)
                throws BadLocationException {


        if (offset == 0 && length != this.length) {
            offset++;
            length--;
        }

        if (offset == 1 && length == this.length - 1) {
            offset = 0;
            length = this.length;
        }

        this.length -= length;

        fb.remove(offset, length);
    }
}
