package edloom.graphics;

import edloom.Main;
import edloom.core.Course;
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

public class HomePanel extends JPanel implements TableModelListener {
    private final JTable table;
    private boolean flag = false; // for table change event triggers
    private final TableRowSorter<TableModel> rowSorter;

    public HomePanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // table used for displaying course details
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        table = new MyJTable(model, new String[]{"ID", Main.getLANG().get(45), Main.getLANG().get(46), Main.getLANG().get(47), Main.getLANG().get(48)});
        table.setDefaultRenderer(Object.class, new VisitorRenderer());
        table.getModel().addTableModelListener(this);
        table.setAutoCreateRowSorter(true);
        TableColumnModel cmod = table.getColumnModel();
        cmod.getColumn(0).setMinWidth(0);
        cmod.getColumn(0).setMaxWidth(0);
        cmod.getColumn(2).setMaxWidth(200);
        cmod.getColumn(2).setPreferredWidth(200);
        cmod.getColumn(3).setMaxWidth(45);
        cmod.getColumn(3).setPreferredWidth(45);
        cmod.getColumn(4).setMaxWidth(90);
        cmod.getColumn(4).setPreferredWidth(90);
        cmod.getColumn(3).setCellRenderer(new Visitor2Renderer());
        cmod.getColumn(4).setCellRenderer(new Visitor2Renderer());
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(Color.BLUE);
        cmod.getColumn(4).setCellEditor(new DefaultCellEditor(textField));
        rowSorter = new TableRowSorter<>(table.getModel());
        List<RowSorter.SortKey> sortKeys = new ArrayList<>(90);
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);
        table.setRowSorter(rowSorter);

        add(table.getTableHeader());
        add(table);
    }


    public void updateData() throws SQLException, RemoteException {
        flag = false;
        // delete all rows
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        //reload the rows with new data
        displayCourses(Main.getService().getListedCourses(UI.user.get(User.details.EMAIL)));
    }

    public void displayCourses(Object[][] data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (data != null)
            for (Object[] row : data) {
                model.addRow(row);
            }
        flag = true;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        /* when a correct enrollment key is entered in the last table column,
         remove that course listing and enroll the user to that course within the database */
        if (flag) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();
            String key = model.getValueAt(row, column).toString(); // get entered key
            int ID = Integer.parseInt(model.getValueAt(row, 0).toString()); // get course ID

            // if the key is correct, do the necessary updates
            try {
                String correctKey = Main.getService().getKey(ID);
                flag = false;
                if (key.equals(correctKey)) {
                    Course course = Main.getService().getCourse(ID);
                    Main.getService().enroll(UI.user.get(User.details.EMAIL), ID, course);
                    ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
                }
            } catch (RemoteException | SQLException throwables) {
                throwables.printStackTrace();
            }
            flag = true;
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
        tabCol.setHeaderValue(Main.getLANG().get(46));
        tabCol = colMod.getColumn(3);
        tabCol.setHeaderValue(Main.getLANG().get(47));
        tabCol = colMod.getColumn(4);
        tabCol.setHeaderValue(Main.getLANG().get(48));
        header.repaint();
    }

}
