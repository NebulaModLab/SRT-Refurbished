/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import sj.editor.data.rules.DirectoryFile;
import sj.editor.data.rules.RuleFile;
import java.awt.datatransfer.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;
import sj.editor.MainWindow;

/**
 * @author SafariJohn (original SRT)
 */
public class AddRuleTransferHandler extends TransferHandler {
    private static final Logger logger = Logger.getLogger(AddRuleTransferHandler.class.getName());

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];

    boolean directory;

    public AddRuleTransferHandler(boolean directory) {
        this.directory = directory;

        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                        + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch(ClassNotFoundException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();

        if (directory) {
            DirectoryFile dir = new DirectoryFile();
            dir.getRule().setId("NEW DIRECTORY");
            nodes.add(new DefaultMutableTreeNode(dir));
        } else {
            RuleFile rule = new RuleFile();
            rule.getRule().setId("NEW RULE");
            nodes.add(new DefaultMutableTreeNode(rule));
        }


        DefaultMutableTreeNode[] transfer = nodes.toArray(new DefaultMutableTreeNode[1]);
        return new NodesTransferable(transfer);
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        MainWindow.getInstance().refreshAllData();
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);

            return nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
