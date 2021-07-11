package edloom.graphics;

import edloom.Main;
import edloom.core.QA;
import edloom.core.Quiz;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class QuizBuilder extends JFrame {
    // memorize data that will be used to construct a Quiz object
    Quiz quiz;
    // similarly, QApanels memorize their own variables to help construct QA objects
    private ArrayList<QApanel> qaPanels = new ArrayList<>();

    public QuizBuilder(ModulePanel parent, String week) {
        // only one course can be created at a time
        parent.getCaller().setEnabled(false);

        // setup JFrame
        setTitle("Quiz builder - " + week);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parent.getCaller().setEnabled(true);
            }
        });

        // setup the JScrollPane and the JPanel that will be added to the JFrame
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(panel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(9);

        // counts number of questions
        AtomicInteger counter = new AtomicInteger(2);


        // instantiate, construct & customize components
        // format the textField for minutes to only accept integers between 0 and 1000
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(1000);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField minutesField = new JFormattedTextField(formatter);

        // ...
        minutesField.setPreferredSize(new Dimension(50, 26));
        minutesField.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        minutesField.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(0.5f)));
        minutesField.setHorizontalAlignment(SwingConstants.CENTER);
        minutesField.setValue(0);
        minutesField.setBorder(null);

        MyTextField passwordField = new MyTextField();
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setPreferredSize(new Dimension(170, 26));
        passwordField.setBorder(null);

        JButton addQuestionButton = new JButton(Main.getLANG().get(36));
        JButton saveButton = new MyButton(Main.getLANG().get(38), Color.decode("#e5ceb8"));
        JButton removeButton = new JButton("-");
        addQuestionButton.setFocusPainted(false);
        removeButton.setFocusPainted(false);
        addQuestionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        QApanel defaultQApanel = new QApanel(1);
        qaPanels.add(defaultQApanel);

        // add components using layout
        c.insets.set(10, 0, 15, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new ResizableLabel(Main.getLANG().get(34), 16), c);

        c.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordField, c);

        c.insets.set(0, 0, 25, 0);
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(new ResizableLabel(Main.getLANG().get(35), 16), c);

        c.anchor = GridBagConstraints.LINE_END;
        panel.add(minutesField, c);

        c.insets.set(0, 0, 0, 0);
        c.gridy = 2;
        panel.add(defaultQApanel, c);


        c.insets.set(20, 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.gridy++;
        c.anchor = GridBagConstraints.LAST_LINE_START;
        panel.add(addQuestionButton, c);


        c.insets.set(20, 0, 5, 0);
        c.gridy++;
        panel.add(saveButton, c);


        add(scrollPane);
        setVisible(true);


        // setup action listeners for buttons
        saveButton.addActionListener(e -> { //  create a Quiz from memorized data

            ArrayList<QA> qas = new ArrayList<>();
            for (QApanel qaPanel : qaPanels) {
                if (!qaPanel.isSelected()) {
                    ((JButton) e.getSource()).setForeground(Color.RED);
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            for (QApanel qaPanel : qaPanels) {
                qas.add(qaPanel.toQA());
            }
            Quiz quiz = new Quiz((Integer) minutesField.getValue(), passwordField.getText(), qas);
            this.quiz = quiz;
            for (ActionListener actionListener : parent.getCaller().getActionListeners()) {
                parent.getCaller().removeActionListener(actionListener);
            }
            parent.setQuiz(quiz);
            parent.getCaller().addActionListener(ev -> this.setVisible(true));
            ((MyButton) parent.getCaller()).setColor(Color.ORANGE);
            parent.getCaller().setEnabled(true);
            this.setVisible(false);
        });

        addQuestionButton.addActionListener(e -> {
            // remove the buttons to make room, add the new panel, add the buttons back
            panel.remove(addQuestionButton);
            panel.remove(saveButton);
            if (counter.get() > 2) panel.remove(removeButton);

            // add new panel, then add the buttons back
            c.insets.set(10, 0, 0, 0);
            c.gridy--;
            QApanel qaPanel = new QApanel(counter.getAndIncrement());
            panel.add(qaPanel, c);

            c.anchor = GridBagConstraints.LAST_LINE_END;
            c.insets.set(20, 0, 0, 0);
            c.gridy++;
            c.fill = GridBagConstraints.NONE;
            panel.add(removeButton, c);

            c.anchor = GridBagConstraints.LAST_LINE_START;
            panel.add(addQuestionButton, c);
            c.insets.set(20, 0, 5, 0);
            c.gridy++;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            panel.add(saveButton, c);
            panel.revalidate();
            panel.repaint();

            // automatically scroll down after adding components
            panel.scrollRectToVisible(new Rectangle(0, scrollPane.getVerticalScrollBar().getMaximum() + 215, 0, 0));

            // save the newly created QApanel into the ArrayList class variable
            qaPanels.add(qaPanel);
        });

        removeButton.addActionListener(e -> {
            panel.remove(addQuestionButton);
            panel.remove(saveButton);
            panel.remove(removeButton);
            c.gridy--;
            panel.remove(panel.getComponent(2 + counter.getAndDecrement()));

            c.insets.set(20, 0, 0, 0);
            c.fill = GridBagConstraints.NONE;
            if (counter.get() > 2) {

                panel.add(removeButton, c);
            }
            c.anchor = GridBagConstraints.LAST_LINE_START;
            panel.add(addQuestionButton, c);
            c.insets.set(20, 0, 5, 0);
            c.gridy++;
            c.anchor = GridBagConstraints.LAST_LINE_END;
            panel.add(saveButton, c);
            panel.revalidate();
            panel.repaint();

            // delete the last saved qaPanel from the Arraylist class variable
            qaPanels.remove(qaPanels.size() - 1);
        });

    }

}
