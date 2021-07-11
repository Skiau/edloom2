package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class MyTextPane extends JTextPane {
    public MyTextPane(String text, int n) {
        setEditable(false);
        setBackground(null);
        setBorder(null);
        setText(text);
        setFont(new Font("Times new roman", Font.PLAIN, n));
    }
}
