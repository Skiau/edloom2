package edloom.graphics;

import edloom.Main;
import edloom.core.User;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class WorkspacePanel extends JPanel implements TableModelListener {
    private final JTabbedPane tabs;
    private final JTable table;
    private boolean flag = false;
    JButton addCourseButton, viewButton, deleteButton;
    private final TableRowSorter<TableModel> rowSorter;

    public WorkspacePanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        //  create tabs so that multiple courses can be opened
        tabs = new JTabbedPane();
        tabs.setFocusable(false);

        //  first tab is default and contains the list of published courses
        JPanel defaultPanel = new JPanel();
        defaultPanel.setOpaque(false);
        defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.PAGE_AXIS));

        //  put the default panel in a scrollPane
        JScrollPane listScroller = new JScrollPane(tabs,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.getVerticalScrollBar().setUnitIncrement(9);
        listScroller.setOpaque(false);
        listScroller.getViewport().setOpaque(false);

        // table used for displaying course details
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0, 2 -> Integer.class;
                    case 1, 3 -> String.class;
                    default -> Boolean.class;
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 3 || column == 1;
            }

        };
        table = new MyJTable(model, new String[]{"ID", Main.getLANG().get(45), Main.getLANG().get(50), Main.getLANG().get(51), Main.getLANG().get(52)});
        table.setDefaultRenderer(Object.class, new VisitorRenderer());
        table.getModel().addTableModelListener(this);
        table.setAutoCreateRowSorter(true);
        TableColumnModel cmod = table.getColumnModel();
        cmod.getColumn(0).setMaxWidth(80);
        cmod.getColumn(0).setPreferredWidth(60);
        cmod.getColumn(2).setMaxWidth(80);
        cmod.getColumn(2).setPreferredWidth(80);
        cmod.getColumn(3).setMaxWidth(120);
        cmod.getColumn(3).setPreferredWidth(120);
        cmod.getColumn(4).setMaxWidth(60);
        cmod.getColumn(4).setPreferredWidth(60);
        cmod.getColumn(0).setCellRenderer(new Visitor2Renderer());
        cmod.getColumn(2).setCellRenderer(new Visitor2Renderer());
        cmod.getColumn(3).setCellRenderer(new Visitor2Renderer());
        rowSorter
                = new TableRowSorter<>(table.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(90);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);
        table.setRowSorter(rowSorter);

        //  a panel with buttons
        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        ((FlowLayout) buttons.getLayout()).setHgap(100);

        //  create & add the buttons
        addCourseButton = new MyButton(Main.getLANG().get(23), Color.BLUE, Color.WHITE);
        viewButton = new MyButton(Main.getLANG().get(24), Color.BLUE, Color.WHITE);
        deleteButton = new MyButton(Main.getLANG().get(26), Color.RED, Color.WHITE);
        buttons.add(addCourseButton);
        buttons.add(viewButton);
        buttons.add(deleteButton);

        //  add action listeners to buttons
        addCourseButton.addActionListener(e -> new CourseBuilder(this, UI.user.get(User.details.FIRSTNAME) + " " + UI.user.get(User.details.LASTNAME), UI.user.get(User.details.EMAIL)));
        viewButton.addActionListener(e -> {
            flag = false;

            int row = table.getSelectedRow();
            // get the ID of the selected row, if no row is selected do not proceed
            if (row > -1) {
                try {
                    int ID = (Integer) (table.getValueAt(row, 0));
                    String[][] data = Main.getService().getAnalytics(ID);
                    tabs.add(table.getValueAt(row, 1).toString(), new AnalyticsPanel(tabs, data, ID));
                    // automatically switch to the newly created tab
                    tabs.setSelectedIndex(tabs.getTabCount() - 1);
                } catch (RemoteException | SQLException remoteException) {
                    remoteException.printStackTrace();
                }
            }
            flag = true;
        });

        deleteButton.addActionListener(e -> {
            flag = false;
            int row = table.getSelectedRow();
            // get the ID of the selected row, if no row is selected do not proceed
            if (row > -1) {

                try {
                    //delete from database
                    Main.getService().deleteCourse((Integer) (table.getValueAt(row, 0)));
                } catch (RemoteException | SQLException remoteException) {
                    remoteException.printStackTrace();
                }
                //delete from table
                ((DefaultTableModel) table.getModel()).removeRow(row);
            }
            flag = true;
        });

        //  add components
        defaultPanel.add(table.getTableHeader());
        defaultPanel.add(table);
        tabs.add(Main.getLANG().get(54), defaultPanel);
        add(buttons, BorderLayout.SOUTH);
        add(listScroller, BorderLayout.CENTER);
    }


    public void addCourse(Object[] data) {
        flag = false;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(data);
        model.fireTableDataChanged();
        flag = true;
    }

    public void updateData() {
        flag = false;
        //delete all rows
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        //reload the rows with new data
        displayCourses();
    }

    private void displayCourses() {
        try {
            for (Object[] row : Main.getService().getPublishedCourses(UI.user.get(User.details.EMAIL))) {
                addCourse(row);
            }
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {   // flag ensures this is only activated on 'Listed' column changes
        if (flag) {
            int row = table.getSelectedRow();
            int column = table.getSelectedColumn();
            int ID = (Integer) table.getValueAt(row, 0);
            try {
                // make changes directly to the database
                if (column == 4)
                    Main.getService().setListed(ID, Boolean.parseBoolean(table.getValueAt(row, 4).toString()));
                else if (column == 3)
                    Main.getService().setKey(ID, table.getValueAt(row, 3).toString());
                else
                    Main.getService().setTitle(ID, table.getValueAt(row, 1).toString());
            } catch (RemoteException | SQLException remoteException) {
                remoteException.printStackTrace();
            }
        }
    }

    public TableRowSorter<TableModel> getSorter() {
        return rowSorter;
    }

    public void updateLanguage() {
        JTableHeader header = table.getTableHeader();
        TableColumnModel colMod = header.getColumnModel();
        TableColumn tabCol = colMod.getColumn(1);
        tabCol.setHeaderValue(Main.getLANG().get(45));
        tabCol = colMod.getColumn(2);
        tabCol.setHeaderValue(Main.getLANG().get(50));
        tabCol = colMod.getColumn(3);
        tabCol.setHeaderValue(Main.getLANG().get(51));
        tabCol = colMod.getColumn(4);
        tabCol.setHeaderValue(Main.getLANG().get(52));
        header.repaint();
        tabs.setTitleAt(0, Main.getLANG().get(54));
        addCourseButton.setText(Main.getLANG().get(23));
        viewButton.setText(Main.getLANG().get(24));
        deleteButton.setText(Main.getLANG().get(26));
    }
}