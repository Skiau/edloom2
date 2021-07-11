package edloom.graphics;

import edloom.Main;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import static edloom.Main.getLANG;
import static edloom.Main.getService;
import static edloom.graphics.Statics.*;

public class JOPanel extends JPanel {
    private final JLabel loginLabel, loginEmailLabel, loginPasswordLabel, loginWarningLabel,
            signupLabel, signupEmailLabel, signupPasswordLabel, signupNameLabel, signupWarningLabel;
    private final JButton loginCardButton, signupCardButton, signupButton;
    private final JTextField loginEmailField, signupFirstNameField, signupLastNameField, signupEmailField;
    private final MyPasswordField loginPasswordField, signupPasswordField, signupPasswordRepField;

    public JOPanel() {
        setBorder(null);
        CardLayout card = new CardLayout();
        setLayout(card);
        setSize(890, 550);
        JPanel loginPanel = new JPanel(null);
        JPanel signupPanel = new JPanel(null);
        signupPanel.setOpaque(false);
        loginPanel.setOpaque(false);


        //instantiate small components
        loginEmailLabel = new MyLabel(4);
        signupEmailLabel = new MyLabel(4);
        loginPasswordLabel = new MyLabel(4);
        signupPasswordLabel = new MyLabel(4);
        signupNameLabel = new MyLabel(4);
        loginLabel = new JLabel();
        signupLabel = new MyLabel(2);
        loginWarningLabel = new JLabel();
        signupWarningLabel = new JLabel();
        loginCardButton = new HoverButton();
        signupCardButton = new HoverButton();
        signupButton = new JButton();
        loginEmailField = new MyTextField();
        loginPasswordField = new MyPasswordField();
        signupFirstNameField = new MyTextField();
        signupLastNameField = new MyTextField();
        signupEmailField = new MyTextField();
        signupPasswordField = new MyPasswordField();
        signupPasswordRepField = new MyPasswordField();
        JLabel logo1 = new JLabel(new ImageIcon("src/Images/edloom4.png"));
        JLabel logo2 = new JLabel(new ImageIcon("src/Images/edloom4.png"));
        signupWarningLabel.setIcon(new ImageIcon("src/Images/mail.png"));
        signupButton.setBorderPainted(false);
        signupButton.setFocusPainted(false);
        loginCardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupCardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setHorizontalTextPosition(SwingConstants.CENTER);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginWarningLabel.setVisible(false);
        loginWarningLabel.setForeground(Color.WHITE);
        loginLabel.setFont(loginLabel.getFont().deriveFont(loginLabel.getFont().getStyle() & ~Font.BOLD));


        //define bounds for small components
        logo1.setBounds(180, 40, 500, 200);
        logo2.setBounds(180, 40, 500, 200);
        loginEmailLabel.setBounds(230, 275, 120, 20);
        loginPasswordLabel.setBounds(230, 315, 120, 20);
        loginLabel.setBounds(373, 250, 200, 20);
        loginWarningLabel.setBounds(373, 340, 250, 20);
        loginEmailField.setBounds(370, 270, 150, 28);
        loginPasswordField.setBounds(370, 310, 150, 28);
        loginCardButton.setBounds(320, 445, 240, 20);
        signupEmailLabel.setBounds(160, 315, 120, 20);
        signupPasswordLabel.setBounds(160, 355, 120, 20);
        signupNameLabel.setBounds(160, 275, 120, 20);
        signupLabel.setBounds(300, 240, 300, 20);
        signupWarningLabel.setBounds(452, 315, 250, 20);
        signupFirstNameField.setBounds(300, 270, 140, 28);
        signupLastNameField.setBounds(452, 270, 140, 28);
        signupEmailField.setBounds(300, 310, 140, 28);
        signupPasswordField.setBounds(300, 350, 140, 28);
        signupPasswordRepField.setBounds(452, 350, 140, 28);
        signupButton.setBounds(452, 395, 140, 25);
        signupCardButton.setBounds(320, 445, 240, 20);


        //add action listeners
        signupCardButton.addActionListener(e -> {
            card.previous(this);
            resetSmallComponents();
        });
        loginCardButton.addActionListener(e -> {
            card.next(this);
            resetSmallComponents();
        });

        ActionListener requestLogin = e -> {
            // do a regex check, the send login request to the server
            try {
                if (Statics.checkEmail(loginEmailField) &&
                        Main.getService().login(loginEmailField.getText().toLowerCase(),
                                String.valueOf(loginPasswordField.getPassword()))) {
                    // ask the server for a ban check before logging in
                    if (Main.getService().isBanned(loginEmailField.getText().toLowerCase()))
                        notifyWindow(getLANG().get(10), "Access restriction", 1);
                    else    //start painting the inside app UI & fill in user saved data from previous login session
                        Main.getUI().loggedIN(getService().getUser(loginEmailField.getText().toLowerCase()));
                } else {
                    loginWarningLabel.setText(getLANG().get(5));
                    loginWarningLabel.setVisible(true);
                }
            } catch (SQLException | IOException | InterruptedException exception) {
                notifyWindow("Something went wrong ", "Server feedback", 0);
                exception.printStackTrace();
            }

        };

        // do an empty fields check, followed by a regex check, a password match check, then send a signup request to the server
        signupButton.addActionListener(e -> {
            try {
                if (Statics.checkEmail(signupEmailField) &
                        checkLength(2, signupFirstNameField, signupLastNameField) &
                        checkLength(8, signupPasswordField) &&
                        String.valueOf(signupPasswordField.getPassword()).equals(String.valueOf(signupPasswordRepField.getPassword()))
                        && Main.getService().signup(
                        signupEmailField.getText().toLowerCase(),
                        String.valueOf(signupPasswordField.getPassword()),
                        signupFirstNameField.getText(),
                        signupLastNameField.getText())) {
                    Main.getService().sendMail(signupEmailField.getText(), "Welcome to Edloom!");
                    resetSmallComponents();
                    card.previous(this);
                } else {
                    signupButton.setForeground(Color.RED);
                    Toolkit.getDefaultToolkit().beep();
                }

            } catch (RemoteException | SQLException ex) {
                if (ex.getMessage().contains("Duplicate entry")) signupWarningLabel.setVisible(true);
                else {
                    notifyWindow("Something went wrong", "Server feedback", 0);
                    ex.printStackTrace();
                }
            } catch (IOException | MessagingException ex) {
                ex.printStackTrace();
            }
        });

        signupEmailField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                signupWarningLabel.setVisible(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                signupWarningLabel.setVisible(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                signupWarningLabel.setVisible(false);
            }
        });

        loginPasswordField.addActionListener(requestLogin);
        loginEmailField.addActionListener(requestLogin);
        new Statics.CmpHighlighter(signupPasswordField, signupPasswordRepField);

        //add components to panels
        loginPanel.add(logo1);
        loginPanel.add(loginLabel);
        loginPanel.add(loginEmailLabel);
        loginPanel.add(loginPasswordLabel);
        loginPanel.add(loginWarningLabel);
        loginPanel.add(loginCardButton);
        loginPanel.add(loginEmailField);
        loginPanel.add(loginPasswordField);
        signupPanel.add(logo2);
        signupPanel.add(signupLabel);
        signupPanel.add(signupEmailLabel);
        signupPanel.add(signupPasswordLabel);
        signupPanel.add(signupNameLabel);
        signupPanel.add(signupWarningLabel);
        signupPanel.add(signupCardButton);
        signupPanel.add(signupButton);
        signupPanel.add(signupFirstNameField);
        signupPanel.add(signupLastNameField);
        signupPanel.add(signupEmailField);
        signupPanel.add(signupPasswordField);
        signupPanel.add(signupPasswordField);
        signupPanel.add(signupPasswordRepField);
        updateLanguage();
        add(loginPanel);
        add(signupPanel);

    }


    public void resetSmallComponents() {    // clear all user inputs
        loginEmailField.setText("");
        loginPasswordField.setText("");
        signupFirstNameField.setText("");
        signupLastNameField.setText("");
        signupPasswordField.setText("");
        signupPasswordRepField.setText("");
        signupEmailField.setText("");
        loginWarningLabel.setVisible(false);
        signupWarningLabel.setVisible(false);
        signupButton.setForeground(Color.BLACK);
    }

    public void updateLanguage() {
        loginEmailLabel.setText(getLANG().get(0));
        signupEmailLabel.setText(getLANG().get(0));
        loginPasswordLabel.setText(getLANG().get(1));
        signupPasswordLabel.setText(getLANG().get(1));
        signupNameLabel.setText(getLANG().get(2));
        loginLabel.setText(getLANG().get(3));
        signupLabel.setText(getLANG().get(4));
        loginWarningLabel.setText(getLANG().get(5));
        signupWarningLabel.setText(getLANG().get(6));
        loginCardButton.setText(getLANG().get(7));
        signupCardButton.setText(getLANG().get(8));
        signupButton.setText(getLANG().get(9));
        signupPasswordField.setToolTipText(getLANG().get(11));
        signupPasswordRepField.setToolTipText(getLANG().get(12));
        signupFirstNameField.setToolTipText(getLANG().get(16));
        signupLastNameField.setToolTipText(getLANG().get(17));
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(new ImageIcon("src/Images/gradient2.png").getImage(), 0, 0, null);
    }

}
