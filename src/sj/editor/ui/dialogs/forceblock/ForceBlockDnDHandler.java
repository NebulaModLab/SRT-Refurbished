/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.forceblock;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import sj.editor.ui.dialogs.AboutDialog;

/**
 * @author SafariJohn (original SRT)
 */
public class ForceBlockDnDHandler extends TransferHandler {
    private static final Logger logger = Logger.getLogger(AboutDialog.class.getName());

    private final boolean editable;

    private boolean dropBlock;

    public ForceBlockDnDHandler(boolean editable) {
        this.editable = editable;

        dropBlock = false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (dropBlock) return false;

        return support.isDataFlavorSupported(StringSelections.stringSelectionsFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!editable) return true;

        List<String> strings = new ArrayList<>();
        try {
            strings = (List) support.getTransferable().getTransferData(StringSelections.stringSelectionsFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
        }

        JList list = (JList) support.getComponent();

        for (String s : strings) {
            boolean found = false;
            for (Object o : ((DefaultListModel) list.getModel()).toArray()) {
                if (s.equals(o)) {
                    found = true;
                    break;
                }
            }

            if (!found) ((DefaultListModel) list.getModel()).addElement(s);
        }

        return true;
    }

    @Override
    public int getSourceActions(JComponent c) {
        if (editable) return TransferHandler.MOVE;
        else return TransferHandler.COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        dropBlock = true;

        JList list = (JList) c;
        return new StringSelections(list.getSelectedValuesList(), list);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        dropBlock = false;

        if (!editable || action == TransferHandler.NONE) return;

        List<String> strings = new ArrayList<>();
        try {
            strings = (List) data.getTransferData(StringSelections.stringSelectionsFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
        }

        JList list = (JList) source;
        for (String s : strings) {
            ((DefaultListModel) list.getModel()).removeElement(s);
        }

    }
}
