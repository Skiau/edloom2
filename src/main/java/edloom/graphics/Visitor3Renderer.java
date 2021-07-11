package edloom.graphics;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class Visitor3Renderer extends DefaultTableCellRenderer {

    public Visitor3Renderer() {
        super();
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(noFocusBorder);
        if (table.getValueAt(row, 2).toString().equalsIgnoreCase("failed"))
            setBackground(new Color(255, 102, 102));
        else if (table.getValueAt(row, 2).toString().equalsIgnoreCase("passed"))
            setBackground(new Color(102, 255, 102));
        else setBackground(null);
        return this;
    }
}