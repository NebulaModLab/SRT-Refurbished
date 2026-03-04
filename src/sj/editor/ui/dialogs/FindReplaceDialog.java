/**
 * This file is part of SafariJohn's Rules Tool.
 * Copyright (C) 2018-2022 SafariJohn
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package sj.editor.ui.dialogs;

import java.awt.Dimension;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import sj.editor.MainWindow;
import sj.editor.data.*;
import sj.editor.data.rules.*;

/**
 * Author: SafariJohn
 */
public class FindReplaceDialog extends JDialog {
    public static final int FIND_INDEX = 0;
    public static final int REPLACE_INDEX = 1;

    private final JTabbedPane findReplace = new JTabbedPane();

    private final JPanel findPanel = new JPanel();
    private final JLabel findLabel = new JLabel();
    private final JTextField findField = new JTextField();
    private final JCheckBox matchCaseCheckBox = new JCheckBox();

    private final JPanel replacePanel = new JPanel();
    private final JLabel rFindLabel = new JLabel();
    private final JTextField rFindField = new JTextField();
    private final JLabel replaceLabel = new JLabel();
    private final JTextField replaceField = new JTextField();
    private final JButton replaceButton = new JButton();
    private final JButton replaceAllButton = new JButton();
    private final JCheckBox rMatchCaseCheckBox = new JCheckBox();


    private final JPanel buttonPanel = new JPanel();
    private final JRadioButton idsButton = new JRadioButton();
    private final JRadioButton triggersButton = new JRadioButton();
    private final JRadioButton conditionsButton = new JRadioButton();
    private final JRadioButton scriptsButton = new JRadioButton();
    private final JRadioButton textsButton = new JRadioButton();
    private final JRadioButton dialogOptionsButton = new JRadioButton();
    private final JRadioButton notesButton = new JRadioButton();

    private final ResultsTableModel resultsTableModel = new ResultsTableModel();
    private final JTable resultsTable = new JTable(resultsTableModel);
    private final JScrollPane resultsScrollPane = new JScrollPane(resultsTable);

    private boolean suppressChange;
    private int searchIndex;
    private boolean updatingMatchCase;
    private List<SearchResult> searchResults;


    public FindReplaceDialog() {
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("Find & Replace");
        setAlwaysOnTop(true);
        setResizable(true);

        suppressChange = false;
        searchIndex = -1;
        updatingMatchCase = false;
        searchResults = new ArrayList<>();

//        KeyAdapter frAdapter = new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent evt) {
//                if (evt.getKeyCode() == KeyEvent.VK_F && evt.isControlDown()) {
//                    findReplace.setSelectedIndex(FIND_INDEX);
//                    evt.consume();
//                }
//
//                if (evt.getKeyCode() == KeyEvent.VK_R && evt.isControlDown()) {
//                    findReplace.setSelectedIndex(REPLACE_INDEX);
//                    evt.consume();
//                }
//            }
//        };

//        addKeyListener(frAdapter);

        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                if (findReplace.getSelectedIndex() == FIND_INDEX) {
                    findField.requestFocus();
                } else if (findReplace.getSelectedIndex() == REPLACE_INDEX) {
                    rFindField.requestFocus();
                }

                refreshInterface(true);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                MainWindow.getInstance().refreshAllData();
                refreshInterface(true);
            }

        });

        //<editor-fold defaultstate="collapsed" desc="Find Panel">
        findLabel.setText("Find:");

        findField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (suppressChange) return;

                suppressChange = true;
                SearchManager.setSearchPattern(findField.getText());
                rFindField.setText(findField.getText());
                suppressChange = false;

                searchRules();
            }
            //</editor-fold>
        });
//        findField.addKeyListener(frAdapter);

        matchCaseCheckBox.setText("Match Case");
        matchCaseCheckBox.setSelected(false);
        matchCaseCheckBox.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void actionPerformed(ActionEvent e) {
                if (updatingMatchCase) return;
                else updatingMatchCase = true;
                rMatchCaseCheckBox.setSelected(matchCaseCheckBox.isSelected());
                refreshInterface(true);
                updatingMatchCase = false;
            }
            //</editor-fold>
        });
        //</editor-fold>

        GroupLayout layout = new GroupLayout(findPanel);
        layout.setAutoCreateContainerGaps(true);

        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(findLabel)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(matchCaseCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(findField)))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(findLabel, GroupLayout.Alignment.TRAILING)
                    .addComponent(matchCaseCheckBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(findField))
        );
        //</editor-fold>
        findPanel.setLayout(layout);


        //<editor-fold defaultstate="collapsed" desc="Replace Panel">
        rFindLabel.setText("Find:");

        rFindField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (suppressChange) return;

                suppressChange = true;

                SearchManager.setSearchPattern(rFindField.getText());
                findField.setText(rFindField.getText());
                suppressChange = false;

                searchRules();
            }
            //</editor-fold>
        });
//        rFindField.addKeyListener(frAdapter);

        replaceLabel.setText("Replace:");

//        replaceField.addKeyListener(frAdapter);
        replaceField.getDocument().addDocumentListener(new DocumentListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SearchManager.setReplacement(replaceField.getText());
            }
            //</editor-fold>
        });

        replaceButton.setText("Replace");
        replaceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { replace(); }
        });
        replaceButton.setEnabled(false);

        replaceAllButton.setText("Replace All");
        replaceAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { replaceAll(); }
        });
        replaceAllButton.setEnabled(false);

        rMatchCaseCheckBox.setText("Match Case");
        rMatchCaseCheckBox.setSelected(false);
        rMatchCaseCheckBox.addActionListener(new ActionListener() {
            //<editor-fold defaultstate="collapsed">
            @Override
            public void actionPerformed(ActionEvent e) {
                if (updatingMatchCase) return;
                else updatingMatchCase = true;
                matchCaseCheckBox.setSelected(rMatchCaseCheckBox.isSelected());
                refreshInterface(true);
                updatingMatchCase = false;
            }
            //</editor-fold>
        });
        //</editor-fold>

        layout = new GroupLayout(replacePanel);
        layout.setAutoCreateContainerGaps(true);

        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rFindLabel)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(rMatchCaseCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rFindField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(replaceLabel)
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(replaceButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(replaceField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(replaceAllButton)))
        );

        layout.linkSize(replaceButton, replaceAllButton);

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rFindLabel, GroupLayout.Alignment.TRAILING)
                    .addComponent(rMatchCaseCheckBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rFindField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(replaceLabel, GroupLayout.Alignment.TRAILING)
                    .addComponent(replaceButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(replaceField)
                    .addComponent(replaceAllButton))
        );
        //</editor-fold>
        replacePanel.setLayout(layout);


        findReplace.addTab("Find", findPanel);
        findReplace.addTab("Replace", replacePanel);
        findReplace.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (findReplace.getSelectedIndex() == FIND_INDEX) {
                    findField.requestFocus();
                } else if (findReplace.getSelectedIndex() == REPLACE_INDEX) {
                    rFindField.requestFocus();
                }
            }
        });


        ActionListener buttonListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { refreshInterface(false); }
        };

        idsButton.setText("IDs");
        idsButton.setSelected(true);
        idsButton.addActionListener(buttonListener);

        triggersButton.setText("Triggers");
        triggersButton.setSelected(true);
        triggersButton.addActionListener(buttonListener);

        conditionsButton.setText("Conditions");
        conditionsButton.setSelected(true);
        conditionsButton.addActionListener(buttonListener);

        scriptsButton.setText("Scripts");
        scriptsButton.setSelected(true);
        scriptsButton.addActionListener(buttonListener);

        textsButton.setText("Texts");
        textsButton.setSelected(true);
        textsButton.addActionListener(buttonListener);

        dialogOptionsButton.setText("Dialog Options");
        dialogOptionsButton.setSelected(true);
        dialogOptionsButton.addActionListener(buttonListener);

        notesButton.setText("Notes");
        notesButton.setSelected(true);
        notesButton.addActionListener(buttonListener);

        buttonPanel.add(idsButton);
        buttonPanel.add(triggersButton);
        buttonPanel.add(conditionsButton);
        buttonPanel.add(scriptsButton);
        buttonPanel.add(textsButton);
        buttonPanel.add(dialogOptionsButton);
        buttonPanel.add(notesButton);


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultsTable.getColumnModel().getColumn(ResultsTableModel.MATCHES).setCellRenderer(centerRenderer);
        resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        resultsTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    rowSelected();
                    evt.consume();
                }
            }

        });
//        resultsTable.addKeyListener(frAdapter);

        resultsTable.addMouseListener(new MouseAdapter() {
            //<editor-fold defaultstate="collapsed">
            private int selectedRow = -1;
            private boolean control = false;
            private boolean shift = false;

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;

                int r = resultsTable.rowAtPoint(e.getPoint());

                // Outside bounds.
                if (r < 0 || r >= resultsTable.getRowCount()) return;

                selectedRow = r;
                control = e.isControlDown();
                shift = e.isShiftDown();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        RuleFile file = (RuleFile) resultsTableModel.getValueAt(selectedRow, ResultsTableModel.RULE);

                        // Skip ctrl-shift
                        if (control) {
                            if (resultsTable.getSelectedRowCount() == 0) {
                                searchIndex = -1;
                                MainWindow.getInstance().refreshAllData();
                                return;
                            }

                            if (selectedRow > resultsTable.getSelectedRow()) return;

                            if (selectedRow < resultsTable.getSelectedRow()) {
                                searchIndex = -1;
                            } else if (resultsTable.isRowSelected(selectedRow)) {
                                searchIndex = -1;
                            } else {
                                searchIndex = -1;
                                MainWindow.getInstance().refreshAllData();
                                return;
                            }
                        } else if (shift) {
                            if (!RulesManager.getActiveRule().equals(file)) {
                                searchIndex = -1;
                            } else if (selectedRow > resultsTable.getSelectedRow()) {
                                return;
                            }
                        } else {
                            if (!RulesManager.getActiveRule().equals(file)) {
                                searchIndex = -1;
                            }
                        }

                        rowSelected();

                        selectedRow = -1;
                        control = false;
                        shift = false;
                    }
                });

            }
            //</editor-fold>
        });

        resultsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);


        setMinimumSize(new Dimension(buttonPanel.getPreferredSize().width + 60, 400));

        layout = new GroupLayout(getContentPane());
        //<editor-fold defaultstate="collapsed" desc="Layout Code">
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(findReplace)
                    .addComponent(buttonPanel, 0, 0, Short.MAX_VALUE)
                    .addComponent(resultsScrollPane))
        );

        int buttonPanelHeight = (int) buttonPanel.getMinimumSize().getHeight();

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(findReplace, (int) findReplace.getMinimumSize().getHeight(),
                            (int) findReplace.getPreferredSize().getHeight(), (int) findReplace.getPreferredSize().getHeight())
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, buttonPanelHeight,
                            buttonPanelHeight, buttonPanelHeight)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsScrollPane)
        );
        //</editor-fold>
        getContentPane().setLayout(layout);
    }

    @Override
    public void setVisible(boolean b) {
        setVisible(b, FIND_INDEX);
    }

    public void setVisible(boolean visible, int tabIndex) {
        super.setVisible(visible);

        findReplace.setSelectedIndex(tabIndex);

        if (!visible) return;

        String searchPattern = SearchManager.getSearchPattern();

        if (!searchPattern.isEmpty()) {
            findField.setText(searchPattern);
            rFindField.setText(searchPattern);

            // Perform search
            searchRules();
        }
    }

    private void rowSelected() {
        int rowindex = resultsTable.getSelectedRow();

        SearchResult result = new SearchResult((SearchResult) resultsTableModel.getValueAt(rowindex, ResultsTableModel.RESULT));

        // Set active rule to selected
        RuleFile file = result.getRuleFile();
        if (!RulesManager.getActiveRule().equals(file)) searchIndex = -1;
        RulesManager.setActiveRule(file);


        List<Integer> indexes = result.getIndexes();
        if (searchIndex == -1 || searchIndex + 1 >= indexes.size()) searchIndex = 0;
        else indexes.set(0, indexes.get(++searchIndex));

        SearchManager.setSearchResult(result);

        SearchManager.setSearchingFlag(true);
        MainWindow.getInstance().throwLinkChange();
        MainWindow.getInstance().refreshAllData();
        SearchManager.setSearchingFlag(false);

        replaceButton.setEnabled(true);
        replaceAllButton.setEnabled(true);
    }

    private void replace() {
        // Need to be sure target is highlighted before replace happens.
            // Do this by enabling/disabling replace button
        int rowIndex = resultsTable.getSelectedRow();
        SearchResult result = new SearchResult((SearchResult) resultsTableModel.getValueAt(rowIndex, ResultsTableModel.RESULT));
        List<Integer> indexes = result.getIndexes();
        String replaceText = SearchManager.getReplacement();

        int offset = replaceText.length() - SearchManager.getSearchPattern().length();
        if (searchIndex + 1 < indexes.size()) {
            // Update post-replace indexes.
            indexes.set(searchIndex + 1, indexes.get(searchIndex + 1) + offset);
        }

        // Actual replacement.
        SearchManager.startingReplacement();
        SearchManager.throwReplaceChange();
        MainWindow.getInstance().refreshAllData();

        if (indexes.size() == 1) {
            refreshInterface(true);
            return;
        }

        searchIndex++;

        // Need to highlight the next index, if exists.
        if (searchIndex < indexes.size()) {
            indexes.set(0, indexes.get(searchIndex));
        } else searchIndex = 0;

        SearchManager.setSearchResult(result);

        SearchManager.setSearchingFlag(true);
        MainWindow.getInstance().refreshAllData();
        refreshInterface(true);
        SearchManager.setSearchingFlag(false);

        if (matchCaseCheckBox.isSelected()) {
            if (!SearchManager.getSearchPattern().contains(replaceText) && searchIndex != 0) {
                searchIndex--;
            }
        } else {
            if (!SearchManager.getSearchPattern().toLowerCase().contains(replaceText.toLowerCase()) && searchIndex != 0) {
                searchIndex--;
            }
        }

        resultsTable.setRowSelectionInterval(rowIndex, rowIndex);
    }

    /**
     * Replaces all results in the selected rows.
     */
    private void replaceAll() {
        int[] indexes = resultsTable.getSelectedRows();
        String replaceText = SearchManager.getReplacement();
        int offset = replaceText.length() - SearchManager.getSearchPattern().length();

        for (int i = indexes.length - 1; i >= 0; i--) {
            SearchResult result = new SearchResult((SearchResult) resultsTableModel.getValueAt(indexes[i], ResultsTableModel.RESULT));

            RuleFile file = result.getRuleFile();
            if (!RulesManager.getActiveRule().equals(file)) searchIndex = -1;
            RulesManager.setActiveRule(file);

            int loop = 0;
            SearchManager.startingReplacement();
            for (Integer j : result.getIndexes()) {
                result.getIndexes().set(0, j + (offset * loop));
                SearchManager.setSearchResult(result);

                SearchManager.throwReplaceChange();
                MainWindow.getInstance().refreshAllData();
                loop++;
            }
        }

        refreshInterface(true);
    }

    /**
     * Searches all rules for the pattern held by the SearchManager.
     */
    private void searchRules() {
        String pattern;
        if (matchCaseCheckBox.isSelected()) pattern = SearchManager.getSearchPattern();
        else pattern = SearchManager.getSearchPattern().toLowerCase();

        searchResults.clear();
        if (pattern.length() == 0) return;

        BoyerMooreParser parser = new BoyerMooreParser(pattern);

        for (Ruleset ruleset : RulesetsManager.getRulesets()) {
            recursiveRuleSearch(parser, ruleset.getRootDirectory());
        }

        refreshInterface(false);
    }

    private void recursiveRuleSearch(BoyerMooreParser parser, RuleFile file) {
        // Search this file
        SearchResult result = new SearchResult(file, SearchResult.Column.ID);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.TRIGGER);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.CONDITIONS);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.SCRIPT);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.TEXT);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.OPTIONS);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);

        result = new SearchResult(file, SearchResult.Column.NOTES);
        recursiveColumnSearch(parser, result, 0);
        if (!result.getIndexes().isEmpty()) searchResults.add(result);


        // If file is not a directory, we're done.
        if (!file.isDirectory()) return;
        // Else, we can cast it to DirectoryFile.
        DirectoryFile dir = (DirectoryFile) file;

        // Search branches
        for (RuleFile branch : dir.getBranches()) {
            recursiveRuleSearch(parser, branch);
        }

        // Search leaves
        for (RuleFile leaf : dir.getLeaves()) {
            recursiveRuleSearch(parser, leaf);
        }
    }

    private void recursiveColumnSearch(BoyerMooreParser parser, SearchResult result, int index) {
        String string = "";
        try {
            switch (result.getColumn()) {
                case ID: string = result.getRuleFile().getRule().getId().substring(index);
                    break;
                case TRIGGER: string = result.getRuleFile().getRule().getTrigger().substring(index);
                    break;
                case CONDITIONS: string = result.getRuleFile().getRule().getConditions().substring(index);
                    break;
                case SCRIPT: string = result.getRuleFile().getRule().getScript().substring(index);
                    break;
                case TEXT: string = result.getRuleFile().getRule().getText().substring(index);
                    break;
                case OPTIONS: string = result.getRuleFile().getRule().getOptions().substring(index);
                    break;
                case NOTES: string = result.getRuleFile().getRule().getNotes().substring(index);
                    break;
            }
        } catch (StringIndexOutOfBoundsException e) {
            return; // More concise than doing an if-return for each case when index > string.length.
        }

        if (!matchCaseCheckBox.isSelected()) string = string.toLowerCase();

        int place = index; // Store where we're at in the string.
        index = parser.search(string);

        if (index >= 0) {
            index += place;
            result.getIndexes().add(index);

            index += SearchManager.getSearchPattern().length(); // Skip past the pattern we just matched.
            recursiveColumnSearch(parser, result, index);
        }
    }

    public void refreshInterface(boolean visible) {
        // If dialog is visible, perform search
        if (visible) searchRules();

        if (!SearchManager.isSearchingFlagRaised()) {
            searchIndex = -1;
            replaceButton.setEnabled(false);
            replaceAllButton.setEnabled(false);
        }


        List<SearchResult> filteredResults = new ArrayList<>();
        for (SearchResult result : searchResults) {
            switch (result.getColumn()) {
                case ID: if (idsButton.isSelected()) filteredResults.add(result);
                    break;
                case TRIGGER: if (triggersButton.isSelected()) filteredResults.add(result);
                    break;
                case CONDITIONS: if (conditionsButton.isSelected()) filteredResults.add(result);
                    break;
                case SCRIPT: if (scriptsButton.isSelected()) filteredResults.add(result);
                    break;
                case TEXT: if (textsButton.isSelected()) filteredResults.add(result);
                    break;
                case OPTIONS: if (dialogOptionsButton.isSelected()) filteredResults.add(result);
                    break;
                case NOTES: if (notesButton.isSelected()) filteredResults.add(result);
                    break;
            }
        }

        resultsTableModel.setResultsList(filteredResults);
        resultsTableModel.fireTableDataChanged();
    }


    private class ResultsTableModel extends AbstractTableModel {
        static final int RESULT = -1;
        static final int RULESET = 0;
        static final int RULE = 1;
        static final int SECTION = 2;
        static final int MATCHES = 3;

        private List<SearchResult> results = new ArrayList<>();

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: return "Ruleset";
                case 1: return "Rule";
                case 2: return "Section";
                case 3: return "Matches";
                default: return "";
            }
        }

        @Override
        public int getRowCount() {
            return results.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        /**
         * If columnIndex is out of bounds, returns the SearchResult for that row.
         */
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < 0) rowIndex = 0;
            SearchResult result = results.get(rowIndex);

            switch (columnIndex) {
                case 0: return RulesetsManager.getRuleset(result.getRuleFile().getRulesetId());
                case 1: return result.getRuleFile();
                case 2: return result.getColumn();
                case 3: return result.getIndexes().size();
                default: return result;
            }
        }

        public void setResultsList(List<SearchResult> results) {
            this.results = results;
        }

    }
}
