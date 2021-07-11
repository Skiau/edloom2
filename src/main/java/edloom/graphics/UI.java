package edloom.graphics;

import edloom.Main;
import edloom.core.Language;
import edloom.core.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import static edloom.graphics.Statics.*;

public class UI extends JFrame {
    private JMenu accountMenu;
    private JMenuItem logout, settings;
    public static User user;

    public UI() {
        UIManager.put("TabbedPane.selected", Color.WHITE);
        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("Button.disabledText", Color.BLACK);
        UIManager.put("ProgressBar.selectionForeground", Color.black);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // create the JMenuBar with Language and Account options
        JMenuBar menuBar = new JMenuBar();
        JMenu languageMenu = new JMenu("Language");
        for (Language language : Main.getLANGarray()) {
            JMenuItem languageItem = new JMenuItem(language.getName());
            languageMenu.add(languageItem);
            languageItem.addActionListener(e -> {
                Main.setLANG(language);
                updateLanguage(this);
                setTitle(language.get(13));
                languageMenu.setText(language.get(14));
                accountMenu.setText(language.get(18));
                logout.setText(language.get(19));
                settings.setText(language.get(20));
            });
        }
        accountMenu = new JMenu(Main.getLANG().get(18));
        logout = new JMenuItem(Main.getLANG().get(19));
        settings = new JMenuItem(Main.getLANG().get(20));
        accountMenu.add(logout);
        accountMenu.add(settings);

        menuBar.add(languageMenu);
        menuBar.add(accountMenu);
        accountMenu.setEnabled(false);
        accountMenu.setVisible(false);
        setJMenuBar(menuBar);

        logout.addActionListener(e -> {
            user = null;
            accountMenu.setEnabled(false);
            accountMenu.setVisible(false);
            setVisible(false);
            JIPanel innerPanel = (JIPanel) getContentPane().getComponent(0);
            innerPanel.removeAll();
            remove(innerPanel);
            System.gc();
            loggedOUT();
        });

        settings.addActionListener(e -> {
            switchEnable();
            JFrame settingsWindow = new JFrame("Settings");
            settingsWindow.setSize(400, 250);
            settingsWindow.setLocationRelativeTo(null);
            settingsWindow.setResizable(false);
            settingsWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            settingsWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    switchEnable();
                }
            });
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JTextPane email = new JTextPane();
            JTextPane regDate = new JTextPane();
            email.setBackground(getBackground());
            regDate.setBackground(getBackground());
            email.setText(user.get(User.details.EMAIL));
            regDate.setText(user.get(User.details.DATE));
            regDate.setEditable(false);
            email.setEditable(false);
            MyButton passwordButton = new MyButton();
            MyButton deleteButton = new MyButton();
            passwordButton.setText(Main.getLANG().get(59));
            passwordButton.setFont(new Font(getFont().getFontName(), Font.PLAIN, 12));
            passwordButton.setForeground(Color.BLACK);
            deleteButton.setText(Main.getLANG().get(60));
            deleteButton.setForeground(Color.RED);

            passwordButton.addActionListener(e1 -> {
                panel.remove(passwordButton);
                JPasswordField pf1 = new JPasswordField();
                JPasswordField pf2 = new JPasswordField();
                pf1.setPreferredSize(new Dimension(144, 22));
                pf2.setPreferredSize(new Dimension(144, 22));
                pf1.putClientProperty("JPasswordField.cutCopyAllowed", true);
                pf2.putClientProperty("JPasswordField.cutCopyAllowed", true);
                pf2.addActionListener(ev -> {
                    String oldPass = String.valueOf(pf1.getPassword());
                    if (oldPass.equals(user.get(User.details.PASSWORD)) &
                            checkLength(8, pf2) &&
                            !oldPass.equals(String.valueOf(pf2.getPassword()))) {
                        String np = String.valueOf(pf2.getPassword());
                        user.setPassword(np);
                        JLabel message = new JLabel(Main.getLANG().get(64));
                        message.setForeground(Color.GREEN);
                        try {
                            Main.getService().updateUser(np, user.get(User.details.EMAIL));

                        } catch (RemoteException | SQLException exception) {
                            exception.printStackTrace();
                            message.setText(Main.getLANG().get(65));
                            message.setForeground(Color.RED);
                        }

                        int l = panel.getComponents().length;
                        if (l > 9) {
                            panel.remove(l - 3);
                            panel.remove(l - 4);
                            panel.remove(l - 5);
                            panel.remove(l - 6);
                            panel.revalidate();
                            panel.repaint();
                        } else {
                            panel.remove(l - 1);
                            panel.remove(l - 2);
                            panel.remove(l - 3);
                            panel.remove(l - 4);
                        }

                        c.gridwidth = 2;
                        c.anchor = GridBagConstraints.CENTER;
                        c.gridx = 0;
                        c.gridy = 3;
                        panel.add(message, c);
                        c.gridwidth = 1;
                        panel.revalidate();
                        panel.repaint();
                    }
                });

                c.anchor = GridBagConstraints.LINE_END;
                c.gridy = 2;
                c.gridx = 0;
                panel.add(new JLabel(Main.getLANG().get(61)), c);

                c.gridx = 0;
                c.gridy = 3;
                panel.add(new JLabel(Main.getLANG().get(62)), c);

                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 1;
                c.gridy = 2;
                panel.add(pf1, c);

                c.gridy = 3;
                c.gridx = 1;
                panel.add(pf2, c);

                panel.revalidate();
                panel.repaint();
            });
            deleteButton.addActionListener(e2 -> {
                panel.remove(deleteButton);
                c.anchor = GridBagConstraints.LINE_END;
                c.gridy = 4;
                c.gridx = 0;
                JLabel label = new JLabel(Main.getLANG().get(63));
                label.setForeground(Color.RED);
                panel.add(label, c);

                c.anchor = GridBagConstraints.LINE_START;
                c.gridx = 1;
                JPasswordField pf = new JPasswordField();
                pf.setPreferredSize(new Dimension(144, 22));
                pf.putClientProperty("JPasswordField.cutCopyAllowed", true);
                pf.addActionListener(ev -> {
                    if (String.valueOf(pf.getPassword()).equals(user.get(User.details.PASSWORD))) {
                        try {
                            Main.getService().deleteUser(user.get(User.details.EMAIL));
                        } catch (RemoteException | SQLException exception) {
                            exception.printStackTrace();
                        }
                        user = null;
                        settingsWindow.dispose();
                        accountMenu.setEnabled(false);
                        accountMenu.setVisible(false);
                        setVisible(false);
                        JIPanel innerPanel = (JIPanel) getContentPane().getComponent(0);
                        innerPanel.removeAll();
                        remove(innerPanel);
                        System.gc();
                        loggedOUT();
                    }
                });
                panel.add(pf, c);

                panel.revalidate();
                panel.repaint();
            });

            c.insets.set(5, 5, 5, 5);
            c.anchor = GridBagConstraints.LINE_END;
            c.gridx = 0;
            c.gridy = 0;
            panel.add(new JLabel(Main.getLANG().get(0)), c);

            c.gridy = 1;
            panel.add(new JLabel(Main.getLANG().get(58)), c);

            c.anchor = GridBagConstraints.LINE_START;
            c.gridx = 1;
            c.gridy = 0;
            panel.add(email, c);

            c.gridy = 1;
            panel.add(regDate, c);

            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 2;
            c.gridx = 0;
            c.gridy = 2;
            panel.add(passwordButton, c);

            c.gridy = 4;
            panel.add(deleteButton, c);

            c.gridwidth = 1;
            settingsWindow.add(panel);
            settingsWindow.setVisible(true);
        });
    }

    public void loggedOUT() {
        user = null;
        setTitle(Main.getLANG().get(13));
        setSize(890, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        add(new JOPanel());
        revalidate();
        repaint();
        setVisible(true);
    }

    public void loggedIN(User user) throws InterruptedException, IOException, SQLException {
        if (user != null)
            UI.user = user;
        else {
            notifyWindow(Main.getLANG().get(41), "Unexpected", 2);
            return;
        }

        setVisible(false);
        setTitle(Main.getLANG().get(13));
        setSize(1068, 660);
        setResizable(true);
        setLocationRelativeTo(null);

        //dereference
        JOPanel outerPanel = (JOPanel) getContentPane().getComponent(0);
        ((JPanel) outerPanel.getComponent(0)).removeAll();
        ((JPanel) outerPanel.getComponent(1)).removeAll();
        outerPanel.removeAll();
        getContentPane().remove(outerPanel);

        //add new components to the now empty JFrame
        accountMenu.setEnabled(true);
        accountMenu.setVisible(true);
        add(new JIPanel());

        revalidate();
        repaint();
        setVisible(true);
    }

    private void switchEnable() {
        this.setEnabled(!this.isEnabled());
    }
}
