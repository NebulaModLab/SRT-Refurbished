/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import java.util.Stack;

/**
 * Author: SafariJohn
 */
public class FileStack extends Stack {
    private final int fileId;

    public FileStack(int fileId) {
        this.fileId = fileId;
    }

    public int getFileId() {
        return fileId;
    }
}
