package edloom.graphics;

import org.checkerframework.checker.units.qual.C;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Visitor2Renderer extends DefaultTableCellRenderer {

    @Override
    public void setHorizontalAlignment(int alignment) {
        super.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setBorder(noFocusBorder);
        if (column == 4) setForeground(Color.LIGHT_GRAY);

        if (column == 3 && table.getValueAt(row, 3).toString().equalsIgnoreCase("failed"))
            setBackground(new Color(255, 102, 102));
        else if (column == 3 && table.getValueAt(row, 3).toString().equalsIgnoreCase("passed"))
            setBackground(new Color(102, 255, 102));
        else  setBackground(null);
        return this;
    }
}