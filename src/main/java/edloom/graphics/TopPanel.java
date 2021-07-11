package edloom.graphics;

import edloom.Main;
import edloom.core.User;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;


public class TopPanel extends JPanel {

    public TopPanel(JIPanel parent) {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        // create components
        JLabel username = new JLabel(UI.user.get(User.details.FIRSTNAME) + " " + UI.user.get(User.details.LASTNAME).charAt(0) + ".");
        username.setIcon(new ImageIcon("src/Images/uicon.png"));
        username.setFont(new Font(getFont().getName(), Font.PLAIN, 22));

        JLabel banner = new JLabel(new ImageIcon("src/Images/edloom1.png"));

        MyTextField searchField = new MyTextField();
        searchField.setPreferredSize(new Dimension(335, 28));
        searchField.addActionListener(e -> {
            String text = searchField.getText().replaceAll("[^a-zA-Z0-9]", "");
            if (text.trim().length() == 0) {
                parent.getHome().getSorter().setRowFilter(null);
                parent.getDash().getSorter().setRowFilter(null);
                parent.getWork().getSorter().setRowFilter(null);
            } else {
                parent.getHome().getSorter().setRowFilter(RowFilter.regexFilter("(?i)" + text));
                parent.getDash().getSorter().setRowFilter(RowFilter.regexFilter("(?i)" + text));
                parent.getWork().getSorter().setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
            // FIXME table display is bugged when refreshed by switching tabs with the RowFilter on
        });

        // enable copy/cut/paste
        JPopupMenu menu = new JPopupMenu();
        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        menu.add(cut);

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        menu.add(copy);

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        menu.add(paste);

        searchField.setComponentPopupMenu(menu);

        new GhostText(searchField, Main.getLANG().get(53));

        // add components
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        add(banner, c);

        c.insets.set(50, 20, 4, 5);
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.gridy = 1;
        add(username, c);

        c.anchor = GridBagConstraints.LAST_LINE_END;
        add(searchField, c);

    }

}
