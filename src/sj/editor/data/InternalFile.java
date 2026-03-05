/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.data;

import sj.editor.MainWindow;

/**
 * @author SafariJohn
 */
public abstract class InternalFile {
    private final int id;
    private long timestamp;

    public InternalFile() {
        id = MainWindow.getNextId();
        timestamp = System.currentTimeMillis();
    }

    public InternalFile(InternalFile file) {
        this(file, true);
    }

    public InternalFile(InternalFile file, boolean copyId) {
        if (copyId) id = file.getId();
        else id = MainWindow.getNextId();

        timestamp = file.getTimestamp();
    }

    /**
     * @return The file's ID number, or -1 if it's a dummy file.
     */
    public final int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(InternalFile file) {
        return id == (file.getId()) && id > 0;
    }
}
