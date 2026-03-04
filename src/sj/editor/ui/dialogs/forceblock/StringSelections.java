/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.forceblock;

import java.awt.Component;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;

/**
 * Author: SafariJohn
 */
public class StringSelections implements Transferable {
    public static DataFlavor stringSelectionsFlavor = new DataFlavor(java.util.List.class, "List of Strings");

    private final List<String> strings;

    public StringSelections(List<String> strings, Component source) {
        this.strings = strings;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { stringSelectionsFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor == stringSelectionsFlavor;
    }

    /**
     *
     * @param flavor Must be stringSelectionsFlavor.
     * @return A list of strings.
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor != stringSelectionsFlavor) return null;

        return strings;
    }
}
