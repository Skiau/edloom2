package edloom.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// a button that fades on mouse hover
public class HoverButton extends JButton {
    float alpha=0.6f;

    public HoverButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setAlpha(1f);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setAlpha(0.6f);
            }
        });
    }

    public void paintComponent(java.awt.Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }


}