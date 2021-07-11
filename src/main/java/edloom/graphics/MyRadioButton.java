package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class MyRadioButton extends JRadioButton {
    public MyRadioButton(String title) {
        super(title);
        setOpaque(false);
        setFocusPainted(false);
        setFont(new Font("Times new roman", Font.PLAIN, 18));
    }
}
