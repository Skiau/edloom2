package edloom.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Map;

import static edloom.core.Statics.openWebpage;

public class MyButton extends JButton {
    private Color color = Color.YELLOW;
    private final Color defaultBG = getBackground();

    public MyButton(String title, Dimension dimension) {
        super(title);
        setBorderPainted(false);
        setFocusPainted(false);
        setPreferredSize(dimension);
        setBackground(Color.decode("#b8cfe5"));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(color);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.decode("#b8cfe5"));
            }
        });

        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public MyButton(String title, Color bg) {
        super(title);
        setBorderPainted(false);
        setFocusPainted(false);
        setBackground(bg);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.BLACK);
                setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(bg);
                setForeground(Color.BLACK);
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public MyButton(String title, Color bg, Color fg) {
        super(title);
        setBorderPainted(false);
        setFocusPainted(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(bg);
                setForeground(fg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(defaultBG);
                setForeground(Color.BLACK);
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public MyButton(String title) {
        super(title);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(color);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Color.BLACK);
            }
        });
    }

    public MyButton(String title, String url, Color color) {
        super(title);
        setForeground(color);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addActionListener(e -> openWebpage(url));
        getModel().addChangeListener(evt -> {
            ButtonModel model = (ButtonModel) evt.getSource();
            Font btnFont = getFont();
            Map attributes = btnFont.getAttributes();

            if (model.isRollover()) {
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            } else {
                attributes.put(TextAttribute.UNDERLINE, null);
            }
            btnFont = btnFont.deriveFont(attributes);
            setFont(btnFont);
        });
    }

    public MyButton() {
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setForeground(Color.BLUE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        getModel().addChangeListener(evt -> {
            ButtonModel model = (ButtonModel) evt.getSource();
            Font btnFont = getFont();
            Map attributes = btnFont.getAttributes();

            if (model.isRollover()) {
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            } else {
                attributes.put(TextAttribute.UNDERLINE, null);
            }
            btnFont = btnFont.deriveFont(attributes);
            setFont(btnFont);
        });
    }


    public void setColor(Color c) {
        color = c;
    }
}
