/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import sj.editor.data.commands.TFVContainer;

/**
 * Assumes it is attached to a JList
 *
 * @author SafariJohn
 */
public class TFVDnDHandler extends TransferHandler {
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
        TFVContainer tfv = (TFVContainer) list.getSelectedValue();

        return new StringSelection(tfv.getTFV());
    }
}
