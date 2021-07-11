package edloom.graphics;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MyJTable extends JTable {
    public MyJTable(DefaultTableModel model, String[] columns) {
        super(model);
        for (String columnName : columns) {
            model.addColumn(columnName);
        }
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFont(new Font("Arial", Font.PLAIN, 17));
        setRowHeight(24);
    }
}
