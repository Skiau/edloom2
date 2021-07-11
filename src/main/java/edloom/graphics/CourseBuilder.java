package edloom.graphics;

import edloom.Main;
import edloom.core.CourseModule;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import static edloom.graphics.Statics.checkLength;
import static edloom.graphics.Statics.notifyWindow;

public class CourseBuilder extends JFrame {
    // memorize data that will be passed as constructor parameters for a Course object
    private int weeks = 0; //count the course modules as weeks
    private int year = 1;
    // similarly, ModulePanels memorize their own variables to help construct CourseModule objects
    private final ArrayList<ModulePanel> modulePanels = new ArrayList<>();

    public CourseBuilder(WorkspacePanel parent, String publisherName, String publisherEmail) {
        // only one course can be created at a time
        Main.getUI().setEnabled(false);

        // setup JFrame
        setTitle("Course builder");
        setSize(500, 495);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                for (ModulePanel mp : modulePanels) {
                    mp.disposeQuizBuilder();
                    mp.disposeFileRefs();
                }
                Main.getUI().setEnabled(true);

            }
        });

        // setup the JScrollPane and the JPanel that will be added to the JFrame
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(9);

        // instantiate, construct & customize components
        JTextField courseTitle = new JTextField();
        courseTitle.setPreferredSize(new Dimension(10, 28));
        courseTitle.setFont(new Font("Times new roman", Font.PLAIN, 18));
        courseTitle.setBorder(null);

        JTextArea courseDescription = new MyTextArea(160);

        JRadioButton year1 = new JRadioButton("1");
        JRadioButton year2 = new JRadioButton("2");
        JRadioButton year3 = new JRadioButton("3");
        JRadioButton year4 = new JRadioButton("4");
        year1.setFocusPainted(false);
        year2.setFocusPainted(false);
        year3.setFocusPainted(false);
        year4.setFocusPainted(false);
        ButtonGroup yearGroup = new ButtonGroup();
        yearGroup.add(year1);
        yearGroup.add(year2);
        yearGroup.add(year3);
        yearGroup.add(year4);

        JButton addModuleButton = new JButton(Main.getLANG().get(27));
        JButton removeModuleButton = new JButton("-");
        JButton saveButton = new MyButton(Main.getLANG().get(38), Color.decode("#e5ceb8"));
        addModuleButton.setFocusPainted(false);
        removeModuleButton.setFocusPainted(false);
        addModuleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeModuleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ModulePanel defaultModule = new ModulePanel(++weeks);
        modulePanels.add(defaultModule);

        // add components using layout
        c.insets.set(15, 0, 5, 15);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(new ResizableLabel(Main.getLANG().get(28), 16), c);

        c.insets.set(15, 0, 5, 0);
        c.gridx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(courseTitle, c);

        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets.set(15, 0, 5, 15);
        panel.add(new ResizableLabel(Main.getLANG().get(29), 16), c);

        c.insets.set(15, 0, 5, 58);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        panel.add(year1, c);

        c.gridx = 2;
        panel.add(year2, c);

        c.gridx = 3;
        panel.add(year3, c);

        c.insets.set(15, 0, 5, 0);
        c.gridx = 4;
        panel.add(year4, c);

        c.gridx = 0;
        c.gridy = 2;
        c.insets.set(15, 0, 5, 15);
        panel.add(new ResizableLabel(Main.getLANG().get(30), 16), c);

        c.gridx = 1;
        c.gridwidth = 4;
        c.insets.set(15, 0, 5, 0);
        panel.add(courseDescription, c);

        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets.set(10, 0, 0, 0);
        panel.add(defaultModule, c);

        c.fill = GridBagConstraints.NONE;
        c.gridy = 4;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        c.insets.set(10, 0, 20, 0);
        panel.add(addModuleButton, c);

        c.insets.set(10, 0, 5, 0);
        c.gridy = 5;
        c.gridx = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        panel.add(saveButton, c);

        add(scrollPane);
        setVisible(true);

        // add action listeners
        year1.addActionListener(e -> year = 1);
        year2.addActionListener(e -> year = 2);
        year3.addActionListener(e -> year = 3);
        year4.addActionListener(e -> year = 4);

        courseTitle.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                courseTitle.setBorder(null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                courseTitle.setBorder(null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                courseTitle.setBorder(null);
            }
        });

        saveButton.addActionListener(e -> { // create a Course from memorized data
            if (checkLength(3, courseTitle)) {
                ArrayList<CourseModule> courseModules = new ArrayList<>();
                for (ModulePanel modulePanel : modulePanels) {
                    modulePanel.disposeQuizBuilder();
                    courseModules.add(modulePanel.toModule());
                }
                try {
                    Object[] array = Main.getService().serializeCourse(courseTitle.getText(), year, courseDescription.getText(), publisherName, publisherEmail, courseModules);
                    Object[] data = {array[0], courseTitle.getText(), 0, array[1], false};
                    parent.addCourse(data);
                } catch (IOException | SQLException exception) {
                    notifyWindow("The course could not be saved.", "Server feedback.", 1);
                    exception.printStackTrace();
                }
                Main.getUI().setEnabled(true);
                dispose();
            } else saveButton.setForeground(Color.RED);
        });

        addModuleButton.addActionListener(e -> {
            // remove the last 3 buttons to make room for another panel
            panel.remove(saveButton);
            panel.remove(addModuleButton);
            if (weeks > 1) panel.remove(removeModuleButton);

            // add new panel, then add the buttons back
            ModulePanel modulePanel = new ModulePanel(++weeks);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;
            c.insets.set(10, 0, 0, 0);
            c.gridx = 1;
            panel.add(modulePanel, c);

            c.fill = GridBagConstraints.NONE;
            c.gridy++;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            c.insets.set(10, 0, 20, 0);
            panel.add(addModuleButton, c);

            if (weeks > 1) {    // prevent the first panel from being removed (1)
                c.anchor = GridBagConstraints.LAST_LINE_END;
                panel.add(removeModuleButton, c);
            }

            c.insets.set(10, 0, 5, 0);
            c.gridx = 1;
            c.gridy++;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            panel.add(saveButton, c);

            panel.revalidate();
            panel.repaint();

            // automatically scroll down after adding components
            panel.scrollRectToVisible(new Rectangle(0, scrollPane.getVerticalScrollBar().getMaximum() + 300, 0, 0));

            // save the newly created ModulePanel into the ArrayList class variable
            modulePanels.add(modulePanel);
        });

        removeModuleButton.addActionListener(e -> {
            // remove the last 4 components
            panel.remove(saveButton);
            panel.remove(addModuleButton);
            panel.remove(removeModuleButton);
            panel.remove(panel.getComponent(--weeks + 9));


            // add the buttons back
            c.gridx = 1;
            c.anchor = GridBagConstraints.LAST_LINE_START;
            c.insets.set(10, 0, 20, 0);
            panel.add(addModuleButton, c);

            if (weeks > 1) {    // prevent the first panel from being removed (2)
                c.anchor = GridBagConstraints.LAST_LINE_END;
                panel.add(removeModuleButton, c);
            }

            c.insets.set(10, 0, 5, 0);
            c.gridy++;
            c.gridx = 1;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            panel.add(saveButton, c);

            panel.revalidate();
            panel.repaint();

            // delete the last saved ModulePanel from the Arraylist class variable
            modulePanels.get(modulePanels.size() - 1).disposeQuizBuilder();
            modulePanels.remove(modulePanels.size() - 1);
        });

    }

}