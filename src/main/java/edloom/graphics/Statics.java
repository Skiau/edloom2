package edloom.graphics;

import edloom.Main;
import edloom.core.Link;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Statics {
    private Statics() {
        throw new UnsupportedOperationException();
    }

    public static JFrame urlForm(ModulePanel modulePanel, GridBagConstraints c) {   // creates a new window for the user to input data
        JFrame f = new JFrame("Add files");
        f.setResizable(false);
        f.setLocationRelativeTo(null);
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(270, 105));
        JTextField nametxt = new JTextField();
        JTextField urltxt = new JTextField();
        JLabel nameLabel = new JLabel(Main.getLANG().get(2) + " ");
        JLabel urlLabel = new JLabel("URL: ");
        JButton saveButton = new MyButton(Main.getLANG().get(38), Color.decode("#e5ceb8"));

        saveButton.addActionListener(e -> { // create a Link object from user input, dispose of this
            try {
                new URL(urltxt.getText());
            } catch (MalformedURLException ex) {
                saveButton.setForeground(Color.RED);
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            if (nametxt.getText().isEmpty())
                nametxt.setText(urltxt.getText());
            modulePanel.addLink((new Link(nametxt.getText(), urltxt.getText())), c);
            f.dispose();
        });

        nameLabel.setBounds(15, 10, 45, 25);
        nametxt.setBounds(60, 10, 185, 25);
        urlLabel.setBounds(15, 40, 45, 25);
        urltxt.setBounds(60, 40, 185, 25);
        saveButton.setBounds(60, 75, 85, 20);


        panel.add(nameLabel);
        panel.add(nametxt);
        panel.add(urlLabel);
        panel.add(urltxt);
        panel.add(saveButton);
        f.add(panel);
        f.pack();
        f.setVisible(true);
        return f;
    }

    public static class CmpHighlighter implements DocumentListener {
        private final MyPasswordField tf1;
        private final MyPasswordField tf2;


        /**
         * Takes the inputs of two JPasswordFields and compares them for matching
         * <p>In case of mismatch, the passwordField to compare has its border set to color red.</p>
         * @param tf1 passwordField to compare to
         * @param tf2  passwordField to compare
         */
        public CmpHighlighter(final JTextField tf1, final JTextField tf2) {
            this.tf1 = (MyPasswordField) tf1;
            this.tf2 = (MyPasswordField) tf2;
            tf2.getDocument().addDocumentListener(this);
            tf1.getDocument().addDocumentListener(this);
        }


        @Override
        public void insertUpdate(DocumentEvent e) {
            tf2.highlight(!String.valueOf(tf1.getPassword()).equals(String.valueOf(tf2.getPassword())));
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            tf2.highlight(!String.valueOf(tf1.getPassword()).equals(String.valueOf(tf2.getPassword())));
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            tf2.highlight(String.valueOf(tf1.getPassword()).equals(String.valueOf(tf2.getPassword())));
        }

    }


    public static void updateLanguage(JFrame frame) {
        JPanel panel = (JPanel) frame.getContentPane().getComponent(0);
        if ((panel instanceof JIPanel))
            ((JIPanel) panel).updateLanguage();
        else
            ((JOPanel) panel).updateLanguage();


    }

    /**
     * Takes an input string and verifies it using Regex.
     * @param email String to verify
     * @return boolean true if the string matches the email pattern, else false
     */
    public static boolean checkEmail(JTextField email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.getText());
        if (!matcher.matches()) email.setBorder(BorderFactory.createSoftBevelBorder(0, Color.PINK, Color.RED));
        return matcher.matches();
    }


    /**
     * Checks if a given array of JTextFields has input equal to or greater than a give minimum value
     * <p>A check which returns false will also highlight the JTextField border with the color red</p>
     * @param min the minimum to compare to
     * @param fields the array of JTextFields
     * @return boolean true if the minimum is met, false otherwise
     */
    public static boolean checkLength(int min, JTextField... fields) {
        boolean b = true;
        for (JTextField field : fields) {
            if (field.getText().strip().length() < min) {
                field.setBorder(BorderFactory.createSoftBevelBorder(0, Color.PINK, Color.RED));
                b = false;
            } else field.setBorder(null);
        }
        return b;
    }

    /**
     * Shows a popup message dialog
     * @param message the message to be displayed
     * @param title window title
     * @param type type of notification, 0 to 3
     */
    public static void notifyWindow(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message + " ¯\\_(ツ)_/¯", title, type);
    }


}
