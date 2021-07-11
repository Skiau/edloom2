package edloom.graphics;

import edloom.Main;
import edloom.core.Course;
import edloom.core.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final JTable table;
    private final ArrayList<Integer> openedCourses = new ArrayList<>();
    private final TableRowSorter<TableModel> rowSorter;
    JTabbedPane tabs;

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // create tabs so that multiple courses can be opened
        tabs = new JTabbedPane();
        tabs.setFocusable(false);


        // first tab is default and contains the list of enrolled courses
        JPanel defaultPanel = new JPanel();
        defaultPanel.setOpaque(false);
        defaultPanel.setLayout(new BoxLayout(defaultPanel, BoxLayout.PAGE_AXIS));

        // put the default panel in a scrollPane
        JScrollPane listScroller = new JScrollPane(defaultPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.getVerticalScrollBar().setUnitIncrement(9);
        listScroller.setOpaque(false);
        listScroller.getViewport().setOpaque(false);

        // the table where enrolled courses will be displayed
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };
        table = new MyJTable(model, new String[]{"ID", Main.getLANG().get(45), Main.getLANG().get(46), Main.getLANG().get(49)});
        table.setDefaultRenderer(Object.class, new VisitorRenderer());
        table.setAutoCreateRowSorter(true);
        TableColumnModel cmod = table.getColumnModel();
        cmod.getColumn(0).setMinWidth(0);
        cmod.getColumn(0).setMaxWidth(0);
        cmod.getColumn(2).setMaxWidth(200);
        cmod.getColumn(2).setPreferredWidth(200);
        cmod.getColumn(3).setMaxWidth(100);
        cmod.getColumn(3).setPreferredWidth(100);
        cmod.getColumn(3).setCellRenderer(new Visitor2Renderer());
        rowSorter = new TableRowSorter<>(table.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(90);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);
        table.setRowSorter(rowSorter);

        table.addMouseListener(new MouseAdapter() { // double click to open selected course in a new tab
            public void mouseClicked(MouseEvent me) {   // double click on row to open course in a new tab
                if (me.getClickCount() == 2) {
                    int row = ((JTable) me.getSource()).getSelectedRow();
                    int ID = (Integer) table.getValueAt(row, 0);

                    //  if the course is already opened, do nothing
                    if (!openedCourses.contains(ID)) {
                        try {
                            Course course = Main.getService().getCourse(UI.user.get(User.details.EMAIL), ID);
                            openedCourses.add(ID);
                            //  if the course exists, display it, otherwise remove it from the table
                            if (course != null) {
                                // create a new tab and add a scrollPane & panel to it
                                JScrollPane courseScroller = new JScrollPane(new CoursePanel(tabs, openedCourses, course),
                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                                courseScroller.getVerticalScrollBar().setUnitIncrement(9);
                                courseScroller.setOpaque(false);
                                courseScroller.getViewport().setOpaque(false);
                                tabs.addTab(table.getValueAt(row, 1).toString(), courseScroller);
                                // automatically switch to the newly created tab
                                tabs.setSelectedIndex(tabs.getTabCount() - 1);
                            } else model.removeRow(table.getSelectedRow());
                        } catch (RemoteException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        //  add remaining components
        defaultPanel.add(table.getTableHeader());
        defaultPanel.add(table);
        tabs.addTab("My courses", listScroller);
        add(tabs);
    }

    public void updateData() {
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
            for (Object[] row : Main.getService().getEnrolledCourses(UI.user.get(User.details.EMAIL))) {
                addCourse(row);
            }
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourse(Object[] data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(data);
        model.fireTableDataChanged();
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
        tabCol.setHeaderValue(Main.getLANG().get(46));
        tabCol = colMod.getColumn(3);
        tabCol.setHeaderValue(Main.getLANG().get(49));
        header.repaint();
        tabs.setTitleAt(0, Main.getLANG().get(54));
    }
}
