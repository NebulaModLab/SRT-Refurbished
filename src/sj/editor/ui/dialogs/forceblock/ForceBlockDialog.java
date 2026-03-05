/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs.forceblock;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import sj.editor.MainWindow;
import sj.editor.data.rules.*;

/**
 * @author SafariJohn
 */
public class ForceBlockDialog extends JDialog {

    private final JLabel idLabel = new JLabel("Add ID:");
    private final JTextField idField = new JTextField();
    private final JButton forceButton = new JButton();
    private final JButton blockButton = new JButton();

    private final JLabel forcedLabel = new JLabel("Forced Rules:");
    private final JList forcedList = new JList();
    private final JScrollPane forcedScrollPane = new JScrollPane();
    private final DefaultListModel forcedListModel = new DefaultListModel();

    private final JLabel blockedLabel = new JLabel("Blocked Rules:");
    private final JList blockedList = new JList();
    private final JScrollPane blockedScrollPane = new JScrollPane();
    private final DefaultListModel blockedListModel = new DefaultListModel();

    private final JButton okButton = new JButton();
    private final JButton cancelButton = new JButton();

    private final boolean from;

    public ForceBlockDialog(boolean from) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        if (from) setTitle("Force/Block for 'Triggered By'");
        else setTitle("Force/Block for 'May Trigger'");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(400, 350));
        setModal(true);

        this.from = from;

        idField.setToolTipText("Manually add an ID by typing it here and using one of the buttons to the right.");
//        idField.setTransferHandler(new TransferHandler() {
//            @Override
//            public boolean canImport(TransferHandler.TransferSupport support) { return false; }
//        });

        forceButton.setText("Force");
        forceButton.setToolTipText("Force rules with the given ID to appear.");
        forceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { forceId(); }
        });

        blockButton.setText("Block");
        blockButton.setToolTipText("Block rules with the given ID from appearing.");
        blockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { blockId(); }
        });


        forcedList.setDragEnabled(true);
        forcedList.setDropMode(DropMode.INSERT);
        forcedList.setTransferHandler(new ForceBlockDnDHandler(true));
        forcedList.setToolTipText("Rules with these IDs will be forced to appear.");
        forcedList.setModel(forcedListModel);
        forcedScrollPane.setViewportView(forcedList);
        forcedList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) { removeForcedIds(); }
            }
        });


        blockedList.setDragEnabled(true);
        blockedList.setDropMode(DropMode.INSERT);
        blockedList.setTransferHandler(new ForceBlockDnDHandler(true));
        blockedList.setToolTipText("Rules with these IDs will be blocked from appearing.");
        blockedList.setModel(blockedListModel);
        blockedScrollPane.setViewportView(blockedList);
        blockedList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_DELETE) { removeBlockedIds(); }
            }
        });


        okButton.setText("OK");
        okButton.setToolTipText("Commit changes.");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { okAction(); }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { cancelAction(); }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(idLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(idField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(forceButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(blockButton))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(forcedScrollPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(forcedLabel))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(blockedLabel)
                                .addComponent(blockedScrollPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(okButton)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cancelButton)))
                    .addContainerGap())
        );

        layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {forceButton, blockButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(idLabel)
                        .addComponent(idField)
                        .addComponent(forceButton)
                        .addComponent(blockButton))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(forcedLabel)
                        .addComponent(blockedLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(forcedScrollPane, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                        .addComponent(blockedScrollPane, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(cancelButton)
                        .addComponent(okButton))
                    .addContainerGap())
        );
        //</editor-fold>
        getContentPane().setLayout(layout);


        RuleFile activeRule = RulesManager.getActiveRule();
        if (from) {
            for (String id : activeRule.getFromWhitelist()) { forcedListModel.addElement(id); }
            for (String id : activeRule.getFromBlacklist()) { blockedListModel.addElement(id); }
        } else {
            for (String id : activeRule.getToWhitelist()) { forcedListModel.addElement(id); }
            for (String id : activeRule.getToBlacklist()) { blockedListModel.addElement(id); }
        }
    }

    private void forceId() {
        String id = idField.getText();
        if (id.isEmpty() || id.replaceAll("[ ]+", "").isEmpty()) return;

        if (!forcedListModel.contains(id)) forcedListModel.addElement(id);
        blockedListModel.removeElement(id);

        idField.setText("");
    }

    private void blockId() {
        String id = idField.getText();
        if (id.isEmpty() || id.replaceAll("[ ]+", "").isEmpty()) return;

        if (!blockedListModel.contains(id)) blockedListModel.addElement(id);
        forcedListModel.removeElement(id);

        idField.setText("");
    }

    private void removeForcedIds() {
        List<String> selected = forcedList.getSelectedValuesList();
        for (String id : selected) {
            forcedListModel.removeElement(id);
        }
    }

    private void removeBlockedIds() {
        List<String> selected = blockedList.getSelectedValuesList();
        for (String id : selected) {
            blockedListModel.removeElement(id);
        }
    }

    private void okAction() {
        RulesManager.backupActiveFile();
        RuleFile activeRule = RulesManager.getActiveRule();


        List<String> newWhiteList = new ArrayList<>();
        List<String> newBlackList = new ArrayList<>();
        for (Object s : forcedListModel.toArray()) {
            newWhiteList.add((String) s);
        }
        for (Object s : blockedListModel.toArray()) {
            newBlackList.add((String) s);
        }

        if (from) {
            activeRule.getFromWhitelist().clear();
            activeRule.getFromWhitelist().addAll(newWhiteList);

            activeRule.getFromBlacklist().clear();
            activeRule.getFromBlacklist().addAll(newBlackList);
        } else {
            activeRule.getToWhitelist().clear();
            activeRule.getToWhitelist().addAll(newWhiteList);

            activeRule.getToBlacklist().clear();
            activeRule.getToBlacklist().addAll(newBlackList);
        }

        MainWindow.getInstance().refreshAllData();

        setVisible(false);
    }

    private void cancelAction() {
        setVisible(false);
    }
}
