package edloom.graphics;

import edloom.Main;
import edloom.core.Course;
import edloom.core.CourseModule;
import edloom.core.Link;
import edloom.core.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class CoursePanel extends JPanel {

    public CoursePanel(JTabbedPane tabs, ArrayList<Integer> openedCourses, Course course) {
        CardLayout card = new CardLayout();
        setLayout(card);
        setOpaque(false);
        JPanel courseCard = new JPanel();

        //  setup courseCard
        courseCard.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        courseCard.setBackground(Color.WHITE);

        c.weightx = 1.0;
        c.weighty = 1.0;

        // close tab button
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.gridx = 0;
        c.gridy = 0;
        MyButton closeTabButton = new MyButton("X");
        closeTabButton.setColor(Color.RED);
        closeTabButton.addActionListener(e -> {
            // remove card panels & the ScrollPane
            this.removeAll();
            courseCard.removeAll();
            openedCourses.remove(Integer.valueOf(course.getID()));
            tabs.remove(this.getParent().getParent());
        });
        courseCard.add(closeTabButton, c);


        // display title, publisher email, description
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets.set(0, 10, 0, 0);
        c.gridy = 1;
        JTextPane title = new JTextPane();
        title.setText(course.getTitle());
        title.setFont(new Font("Times new roman", Font.BOLD, 20));
        title.setEditable(false);
        courseCard.add(title, c);

        c.gridy = 2;
        c.insets.set(5, 10, 0, 0);
        courseCard.add(new MyTextPane(course.getPublisherEmail(), 18), c);

        c.gridy = 3;
        c.insets.set(10, 10, 5, 0);
        courseCard.add(new MyTextPane(course.getDescription(), 18), c);


        int counter = 1; // week counter
        // iterate through the course modules and display them
        for (CourseModule module : course.getModules()) {
            c.gridy++;
            c.insets.set(10, 0, 5, 0);
            c.fill = GridBagConstraints.HORIZONTAL;
            JLabel weekLabel = new JLabel(Main.getLANG().get(31) + " " + counter++, SwingConstants.CENTER);
            weekLabel.setFont(new Font("Times new roman", Font.PLAIN, 18));
            weekLabel.setOpaque(true);
            courseCard.add(weekLabel, c);

            c.insets.set(0, 10, 0, 0);
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.LINE_START;

            // display module details only if the start date has passed
            if (module.getStartDate().before(Calendar.getInstance().getTime())) {
                for (Link link : module.getLinks()) {
                    c.gridy++;
                    MyButton linkButton = new MyButton(link.getName(), link.getUrl(), Color.blue);
                    linkButton.setFont(new Font("Times new roman", Font.PLAIN, 18));
                    courseCard.add(linkButton, c);
                }
                c.gridy++;
                c.anchor = GridBagConstraints.CENTER;
                c.gridwidth = 1;
                c.insets.set(10, 10, 5, 0);

                // enable or enable quiz based on saved data
                if (module.getQuiz() != null) {
                    JButton quizButton = new MyButton(Main.getLANG().get(21), Color.BLUE, Color.WHITE);
                    if (module.getQuiz().getScore() < 0)
                        quizButton.addActionListener(e -> {
                            add(new QuizPanel(quizButton, course, module.getQuiz(), card, this));
                            card.next(this);
                        });
                    else if (module.getQuiz().getScore() < 50) {
                        quizButton.setEnabled(false);
                        quizButton.setBorderPainted(false);
                        quizButton.setText(module.getQuiz().getScore() + "%");
                        quizButton.setBackground(new Color(255, 102, 102));
                        for (MouseListener ms : quizButton.getMouseListeners()) {
                            quizButton.removeMouseListener(ms);
                        }
                    } else {
                        quizButton.setEnabled(false);
                        quizButton.setBorderPainted(false);
                        quizButton.setText(module.getQuiz().getScore() + "%");
                        quizButton.setBackground(new Color(102, 255, 102));
                        for (MouseListener ms : quizButton.getMouseListeners()) {
                            quizButton.removeMouseListener(ms);
                        }
                    }
                    courseCard.add(quizButton, c);
                }
                c.gridx = 1;
                JButton uploadButton = new MyButton(Main.getLANG().get(22), Color.BLUE, Color.WHITE);

                // enable or enable submissions based on saved data
                if (module.getSubmissions() == null) {
                    uploadButton.addActionListener(ae -> {
                        uploadButton.setEnabled(false);

                        JFileChooser fileChooser = new JFileChooser();
                        int returnValue = fileChooser.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            try {
                                module.submit(readFileToByteArray(selectedFile), selectedFile.getName());
                                // if the course exists, update it, else clear this panel and display an error message
                                Main.getService().updateCourse(course, UI.user.get(User.details.EMAIL));
                            } catch (IOException | SQLException e) {
                                this.removeAll();
                                add(new JLabel(Main.getLANG().get(42) + " ¯\\_(ツ)_/¯"));
                                revalidate();
                                repaint();
                                // e.printStackTrace();
                            }
                            uploadButton.setText(selectedFile.getName());
                            for (MouseListener ms : uploadButton.getMouseListeners()) {
                                uploadButton.removeMouseListener(ms);
                            }
                        } else uploadButton.setEnabled(true);

                    });
                } else {
                    uploadButton.setEnabled(false);
                    uploadButton.setText(module.getSubmissionsName());
                    for (MouseListener ms : uploadButton.getMouseListeners()) {
                        uploadButton.removeMouseListener(ms);
                    }
                }
                courseCard.add(uploadButton, c);

                c.gridwidth = 2;
                c.gridx = 0;
            } else { // show the user when the course unlocks by displaying a suggestive label
                c.insets.set(10, 10, 5, 0);
                c.gridy++;
                JLabel lockedLabel = new JLabel(module.getStartDate().toString());
                lockedLabel.setIcon(new ImageIcon("src/Images/padlock.png"));
                lockedLabel.setFont(new Font(lockedLabel.getFont().getFontName(), Font.PLAIN, 14));
                courseCard.add(lockedLabel, c);
            }
        }
        add(courseCard);
    }

}

