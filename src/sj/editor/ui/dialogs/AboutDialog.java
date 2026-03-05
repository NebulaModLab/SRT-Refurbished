/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.awt.Dimension;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import sj.editor.MainWindow;

/**
 * @author SafariJohn
 */
public class AboutDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(AboutDialog.class.getName());

    private final JLabel titleLabel = new JLabel();
    private final JLabel versionLabel = new JLabel();
    private final JLabel description1Label = new JLabel();
    private final JLabel description2Label = new JLabel();
    private final JLabel licenseLabel = new JLabel();
    private final JTextArea licenseTextArea = new JTextArea();
    private final JScrollPane licenseScrollPane = new JScrollPane();
    private final JLabel changelogLabel = new JLabel();
    private final JTextArea changelogTextArea = new JTextArea();
    private final JScrollPane changelogScrollPane = new JScrollPane();

    public AboutDialog() {
        setTitle("About");
        setAlwaysOnTop(true);
        setMinimumSize(new Dimension(500, 600));
        setResizable(false);
        setModal(true);


        titleLabel.setText("SafariJohn's Rules Tool");

        versionLabel.setText("v" + MainWindow.getInstance().getVersion());

        description1Label.setText("This program is a modding tool for the game Starsector.");
        description2Label.setText("It provides a convenient interface for modifying rules.csv files.");

        description1Label.setHorizontalAlignment(JLabel.CENTER);
        description2Label.setHorizontalAlignment(JLabel.CENTER);

        licenseLabel.setText("License:");

        licenseTextArea.setText(""
            + "SafariJohn's Rules Tool is an interface for editing rules.csv files.\n"
            + "\n"
            + "Copyright (C) 2018-2022 SafariJohn\n"
            + "\n"
            + "SafariJohn's Rules Tool is free software: you can redistribute it and/or modify\n"
            + "it under the terms of the GNU General Public License as published by\n"
            + "the Free Software Foundation, either version 3 of the License, or\n"
            + "(at your option) any later version.\n"
            + "\n"
            + "SafariJohn's Rules Tool is distributed in the hope that it will be useful,\n"
            + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
            + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
            + "GNU General Public License for more details.\n"
            + "\n"
            + "You should have received a copy of the GNU General Public License\n"
            + "along with SafariJohn's Rules Tool.  If not, see <https://www.gnu.org/licenses/>.");
        licenseTextArea.setEditable(false);
        licenseTextArea.setLineWrap(true);
        licenseTextArea.setWrapStyleWord(true);

        licenseScrollPane.setBorder(BorderFactory.createEtchedBorder());
        licenseScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        licenseScrollPane.setViewportView(licenseTextArea);

        changelogLabel.setText("Changelog:");

        // Read changelog from file
        File file = new File("changelog.txt");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String st;
            while ((st = reader.readLine()) != null) {
                sb.append(st).append("\n");
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.toString(), ex);
        }

        changelogTextArea.setText(sb.toString());
        changelogTextArea.setEditable(false);
        changelogTextArea.setLineWrap(true);
        changelogTextArea.setWrapStyleWord(true);

        changelogScrollPane.setBorder(BorderFactory.createEtchedBorder());
        changelogScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        changelogScrollPane.setViewportView(changelogTextArea);


        GroupLayout layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(titleLabel)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(versionLabel)
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(description1Label)
                            .addGap(10, 10, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(description2Label)
                            .addGap(10, 10, Short.MAX_VALUE))
                        .addComponent(licenseLabel)
                        .addComponent(licenseScrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addComponent(changelogLabel)
                        .addComponent(changelogScrollPane, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                    .addContainerGap())
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(titleLabel)
//                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(versionLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(description1Label)
                    .addComponent(description2Label)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(licenseLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(licenseScrollPane, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(changelogLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(changelogScrollPane, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addContainerGap())
        );
        //</editor-fold>
        getContentPane().setLayout(layout);
    }

}
