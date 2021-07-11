package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class ResizableLabel extends JLabel {
    ResizableLabel(String text, int size) {
        super(text);
        setFont(new Font(getFont().toString(), Font.PLAIN, size));
    }
}
