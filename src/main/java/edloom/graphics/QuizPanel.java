package edloom.graphics;


import edloom.Main;
import edloom.core.Course;
import edloom.core.QA;
import edloom.core.Quiz;
import edloom.core.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class QuizPanel extends JPanel {
    private final MyButton backButton;
    private final JButton caller;
    private final JButton submitButton = new JButton(Main.getLANG().get(43));
    private final Course course;
    private final Quiz quiz;
    private Thread thread = null;
    private final ArrayList<ButtonGroup> bgs = new ArrayList<>();

    public QuizPanel(JButton caller, Course course, Quiz quiz, CardLayout card, JPanel parent) {
        this.course = course;
        this.caller = caller;
        setBackground(Color.WHITE);
        this.quiz = quiz;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets.set(5, 20, 0, 0);

        c.gridx = 0;
        c.gridy = 0;
        backButton = new MyButton("<");
        backButton.setFont(new Font(getFont().toString(), Font.BOLD, 20));
        backButton.setColor(Color.BLUE);
        backButton.addActionListener(e -> {
            card.previous(parent);
            parent.remove(this);
        });
        add(backButton, c);

        // if the quiz has a password, ask for it, otherwise start quiz
        if (quiz.getPassword().isBlank()) startQuiz(c);
        else {
            c.anchor = GridBagConstraints.PAGE_START;
            c.gridx = 1;
            MyTextField passwordField = new MyTextField();
            new GhostText(passwordField, Main.getLANG().get(25));
            passwordField.setPreferredSize(new Dimension(300, 28));
            passwordField.addActionListener(e -> {
                if (passwordField.getText().equals(quiz.getPassword())) {
                    remove(passwordField);
                    startQuiz(c);
                }
            });
            add(passwordField, c);
        }
    }

    private void startQuiz(GridBagConstraints c) {
        // reset components
        c.gridx = 0;
        c.gridy = 1;
        //  if the quiz has a timer, display it & start a new countdown thread
        final int[] time = {quiz.getTime() * 60};
        if (time[0] > 0) {
            c.anchor = GridBagConstraints.PAGE_START;

            // disable back button
            backButton.setEnabled(false);
            JLabel timeLabel = new JLabel(time[0] / 60 + ":00");
            timeLabel.setFont(new Font(getFont().toString(), Font.PLAIN, 26));
            thread = new Thread(() -> {
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (thread.isInterrupted()) {
                            timer.cancel();
                            endQuiz();
                            timeLabel.setForeground(Color.GRAY);
                        }
                        if (time[0] > 0 && time[0] > 10) {
                            time[0]--;
                            timeLabel.setText(time[0] / 60 + ":" + time[0] % 60);

                        } else if (time[0] > 0) {
                            time[0]--;
                            timeLabel.setForeground(Color.RED);
                            timeLabel.setText("0:" + time[0] % 60);
                        } else { // if the timer counts all the way down, the quiz finishes automatically
                            timer.cancel();
                            endQuiz();
                            timeLabel.setForeground(Color.GRAY);
                        }
                    }
                };
                timer.scheduleAtFixedRate(timerTask, 0, 1000);
            });
            add(timeLabel, c);
        }

        c.anchor = GridBagConstraints.FIRST_LINE_START;
        for (QA qa : quiz.getQaList()) {
            c.gridy++;
            JLabel questionLabel = new JLabel(qa.getQuestion());
            questionLabel.setFont(new Font("Times new roman", Font.BOLD, 18));
            add(questionLabel, c);

            String[] answers = qa.getAnswers();
            JRadioButton button1 = new MyRadioButton(answers[0]);
            JRadioButton button2 = new MyRadioButton(answers[1]);
            JRadioButton button3 = new MyRadioButton(answers[2]);
            JRadioButton button4 = new MyRadioButton(answers[3]);
            JRadioButton button5 = new MyRadioButton(answers[4]);
            ButtonGroup bg = new ButtonGroup();
            bg.add(button1);
            bg.add(button2);
            bg.add(button3);
            bg.add(button4);
            bg.add(button5);
            bgs.add(bg);
            c.gridy++;
            add(button1, c);
            c.gridy++;
            add(button2, c);
            c.gridy++;
            add(button3, c);
            c.gridy++;
            add(button4, c);
            c.gridy++;
            add(button5, c);

            switch (qa.getCorrectAnswer()) {
                case 1 -> button1.setActionCommand("correct");
                case 2 -> button2.setActionCommand("correct");
                case 3 -> button3.setActionCommand("correct");
                case 4 -> button4.setActionCommand("correct");
                case 5 -> button5.setActionCommand("correct");
            }
        }

        // the submit button
        c.insets.set(5, 0, 10, 0);
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridy++;
        submitButton.setBorderPainted(false);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> {
            endQuiz();
            backButton.setEnabled(true);
        });
        add(submitButton, c);
        revalidate();
        repaint();
        if (thread != null) thread.start();

    }

    private void endQuiz() {
        submitButton.setEnabled(false);
        backButton.setEnabled(true);
        if (thread != null) thread.interrupt(); // if there is a timer, stop it
        int correct = 0; // count correct answers given
        for (Component component : getComponents()) {
            if (component instanceof JRadioButton && ((JRadioButton) component).isSelected()) {
                if (((JRadioButton) component).getModel().getActionCommand() != null) {
                    component.setForeground(Color.GREEN);
                    correct++;
                } else component.setForeground(Color.RED);
            }
            int score = correct * 100 / bgs.size(); // calculate the score as arithmetic average expressed in percentages
            quiz.setScore(score);
            caller.setEnabled(false);
            // naturally, correct answers are colored green, while incorrect answers are colored red
            if (score > 49)
                caller.setBackground(new Color(102, 255, 102));
            else
                caller.setBackground(new Color(255, 102, 102));
            caller.setText(score + "%");
            for (MouseListener ms : caller.getMouseListeners()) {
                caller.removeMouseListener(ms);
            }
        }
        try {
            Main.getService().updateCourse(course, UI.user.get(User.details.EMAIL)); // save changes to database
        } catch (RemoteException | SQLException e) {
            e.printStackTrace();
        }

    }
}
