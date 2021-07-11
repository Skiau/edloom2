package edloom.graphics;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class GhostText implements FocusListener, DocumentListener, PropertyChangeListener {
    private final JTextComponent jText;
    private boolean isEmpty;
    private final Color ghostColor;
    private Color foregroundColor;
    private final String ghostText;

    /** Creates a ghost text that disappears on focus & property change
     * @param jText the textField to be affected
     * @param ghostText the ghost text to be displayed
     */
    protected <T extends JTextComponent> GhostText(final T jText, String ghostText) {
        super();
        this.jText = jText;
        this.ghostText = ghostText;
        this.ghostColor = Color.LIGHT_GRAY;
        jText.addFocusListener(this);
        registerListeners();
        updateState();
        if (!this.jText.hasFocus()) {
            focusLost(null);
        }
    }


    private void registerListeners() {
        jText.getDocument().addDocumentListener(this);
        jText.addPropertyChangeListener("foreground", this);
    }

    private void unregisterListeners() {
        jText.getDocument().removeDocumentListener(this);
        jText.removePropertyChangeListener("foreground", this);
    }

    private void updateState() {
        isEmpty = jText.getText().length() == 0;
        foregroundColor = jText.getForeground();
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                jText.setText("");
                jText.setForeground(foregroundColor);
            } finally {
                registerListeners();
            }
        }

    }

    @Override
    public void focusLost(FocusEvent e) {
        if (isEmpty) {
            unregisterListeners();
            try {
                jText.setText(ghostText);
                jText.setForeground(ghostColor);
            } finally {
                registerListeners();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        updateState();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateState();
    }

}
