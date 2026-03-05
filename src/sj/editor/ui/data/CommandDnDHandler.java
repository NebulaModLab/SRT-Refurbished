/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import sj.editor.data.commands.Command;

/**
 * @author SafariJohn
 */
public class CommandDnDHandler extends TransferHandler {
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return false;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        Command com = (Command) list.getSelectedValue();

        return new CommandTransferable(com);
    }

    public static final DataFlavor commandFlavor = new DataFlavor(Command.class, "Command");

    public static class CommandTransferable implements Transferable {
        private final Command com;

        public CommandTransferable(Command com) {
            this.com = com;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[0] = commandFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == commandFlavor;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            return com;
        }

    }
}
