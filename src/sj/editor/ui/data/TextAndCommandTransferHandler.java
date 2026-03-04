/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.data;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import sj.editor.data.commands.Command;
import sj.editor.data.commands.Command.FieldType;
import sj.editor.data.commands.CommandField;
import sj.editor.ui.dialogs.commands.CommandInputDialog;
import sj.misc.JavaTextTransferHandler;

/**
 * Author: SafariJohn
 */
public class TextAndCommandTransferHandler extends JavaTextTransferHandler {
    private static final Logger logger = Logger.getLogger(TextAndCommandTransferHandler.class.getName());

    @Override
    protected DataFlavor getImportFlavor(DataFlavor[] flavors, JTextComponent c) {
        for (DataFlavor flavor : flavors) {
            if (flavor == CommandDnDHandler.commandFlavor) return flavor;
        }

        return super.getImportFlavor(flavors, c);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] flavors) {
        if (!comp.isEnabled()) return false;
        if (!((JTextComponent) comp).isEditable()) return false;

        for (DataFlavor flavor : flavors) {
            if (flavor == CommandDnDHandler.commandFlavor) return true;
        }

        return super.canImport(comp, flavors);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {

        if (t.isDataFlavorSupported(CommandDnDHandler.commandFlavor)) {
            Command com = null;
            try {
                com = (Command) t.getTransferData(CommandDnDHandler.commandFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                logger.log(Level.WARNING, null, ex);
            }

            if (com == null) return false;

            // If the command has no fields, convert to StringSelection and go.
            if (com.getFields().isEmpty()) {
                return super.importData(comp, new StringSelection(com.getName()));
            }

            // If the command only has pre-done enums,
            // convert to StringSelection and go.
            boolean preFilledOnly = true;
            String result = com.getName();
            for (CommandField field : com.getFields()) {
                if (field.getType() != FieldType.ENUM) preFilledOnly = false;
                if (field.getOptions().size() != 1) preFilledOnly = false;
                if (field.isOptional()) preFilledOnly = false;

                if (!preFilledOnly) break;

                result += " " + field.getOptions().get(0);
            }

            if (preFilledOnly) {
                return super.importData(comp, new StringSelection(result));
            }

            // Otherwise, make the user fill out the Command's fields
            CommandInputDialog dialog = new CommandInputDialog(com);
            dialog.setModal(true);
            dialog.setVisible(true);

            result = dialog.getResult();
            dialog.dispose();

            return super.importData(comp, new StringSelection(result));
        }

        return super.importData(comp, t);
    }

    @Override
    public boolean importData(TransferSupport support) {
        return super.importData(support);
    }

}
