package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class MyTextField extends JTextField {
    public MyTextField() {
        setFont(new Font("Times New Roman", Font.PLAIN, 16));
        setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.5f)));
    }

}
