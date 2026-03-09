/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.*;
import sj.editor.MainWindow;
import sj.editor.data.rules.RuleFile;
import sj.editor.data.rules.RulesManager;

/**
 * @author SafariJohn (original SRT)
 */
public class LinkToTransferHandler extends TransferHandler {
    private static final Logger logger = Logger.getLogger(LinkToTransferHandler.class.getName());

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    public LinkToTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                        + javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
        }
    }

    private RuleFile getRuleFromPath(TreePath path) {
        return getRuleFromNode((DefaultMutableTreeNode) path.getLastPathComponent());
    }

    private RuleFile getRuleFromNode(DefaultMutableTreeNode node) {
        return (RuleFile) node.getUserObject();
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) return false;
        if (!support.isDataFlavorSupported(nodesFlavor)) return false;

        RuleFile activeRule = RulesManager.getActiveRule();
        if (!activeRule.isRule()) return false;

        if (support.getDropAction() == MOVE) support.setDropAction(COPY);

        // Make sure at least one of the incoming nodes contains a RuleFile
        // that is not the active rule and/or not already forced for the active rule.
        try {
            DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) support.getTransferable().getTransferData(nodesFlavor);

            boolean foundValidRule = false;

            for (DefaultMutableTreeNode node : nodes) {
                RuleFile file = getRuleFromNode(node);

                if (!file.isRule()) continue;
                if (file.getParentId() < 0) continue;
                if (file.equals(activeRule)) continue;

                boolean found = false;
                for (String id : activeRule.getToWhitelist()) {
                    if (file.getRule().getId().equals(id)) {
                        found = true;
                        break;
                    }
                }
                if (found) continue;

                foundValidRule = true;
                break;
            }

            return foundValidRule;

        } catch (UnsupportedFlavorException | IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
        }

        return false;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) return false;

        RulesManager.backupActiveFile();

        try {
            DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) support.getTransferable().getTransferData(nodesFlavor);

            for (DefaultMutableTreeNode node : nodes) {
                RuleFile file = getRuleFromNode(node);
                RuleFile activeRule = RulesManager.getActiveRule();

                if (!file.isRule()) continue;
                if (file.equals(activeRule)) continue;

                boolean found = false;
                for (String id : activeRule.getToWhitelist()) {
                    if (file.getRule().getId().equals(id)) {
                        found = true;
                        break;
                    }
                }
                if (found) continue;

                activeRule.getToWhitelist().add(file.getRule().getId());
                activeRule.getToBlacklist().remove(file.getRule().getId());
            }
        } catch (UnsupportedFlavorException | java.io.IOException ex) {
            logger.log(Level.FINE, ex.toString(), ex);
            return false;
        }

        MainWindow.getInstance().refreshAllData();

        return true;
    }
}