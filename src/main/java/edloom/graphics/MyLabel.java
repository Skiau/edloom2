package edloom.graphics;

import javax.swing.*;
import java.awt.*;

public class MyLabel extends JLabel {

    public MyLabel(int alignment) {
        if (alignment == 2)
            setHorizontalAlignment(SwingConstants.LEFT);
        else if (alignment == 4)
            setHorizontalAlignment(SwingConstants.RIGHT);
        else
            setHorizontalAlignment(SwingConstants.CENTER);
        setFont(new Font("Times new roman", Font.PLAIN, 18));

    }
}
