package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class MyPasswordField extends JPasswordField {

    public MyPasswordField() {
        putClientProperty("JPasswordField.cutCopyAllowed", true);
        setFont(new Font("Times New Roman", Font.PLAIN, 22));
        setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.5f)));
    }

    public void highlight(boolean b) {

        if (b)
            this.setBorder(BorderFactory.createSoftBevelBorder(0, Color.PINK, Color.RED));
        else
            this.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.5f)));

    }
}
