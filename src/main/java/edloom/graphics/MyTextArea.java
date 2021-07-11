package edloom.graphics;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;

public class MyTextArea extends JTextArea {

/**
 * Create a JTextArea with capped character count
 * @param max the maximum number of characters that can be inserted into this
 */
    public MyTextArea(int max) {
        setFont(new Font("Times new Roman", Font.PLAIN, 16));
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "160",
                TitledBorder.RIGHT,
                TitledBorder.DEFAULT_POSITION);
        border.setTitleColor(Color.LIGHT_GRAY);
        setBorder(border);
        setPreferredSize(new Dimension(300, 120));
        setLineWrap(true);
        DefaultStyledDocument doc = new DefaultStyledDocument();
        setDocument(doc);
        doc.setDocumentFilter(new DocumentSizeFilter(max));
        doc.addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                border.setTitle(String.valueOf(max - getText().length()));
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                border.setTitle(String.valueOf(max - getText().length()));
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                border.setTitle(String.valueOf(max - getText().length()));
                repaint();
            }
        });
    }
}
