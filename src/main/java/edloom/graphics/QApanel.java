package edloom.graphics;

import edloom.Main;
import edloom.core.QA;

import javax.swing.*;
import java.awt.*;

public class QApanel extends JPanel {
    private int correctAnswer = 0;
    private final JTextArea question;
    private final JTextField txt1, txt2, txt3, txt4, txt5; // the answer options

    public QApanel(int questionCount) {

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        question = new JTextArea("Q" + questionCount + ". ");
        question.setPreferredSize(new Dimension(290, 60));
        question.setFont(new Font("Times new roman", Font.PLAIN, 16));
        question.setLineWrap(true);

        JRadioButton button1 = new JRadioButton();
        JRadioButton button2 = new JRadioButton();
        JRadioButton button3 = new JRadioButton();
        JRadioButton button4 = new JRadioButton();
        JRadioButton button5 = new JRadioButton();
        ButtonGroup bg = new ButtonGroup();
        bg.add(button1);
        bg.add(button2);
        bg.add(button3);
        bg.add(button4);
        bg.add(button5);
        Font answerFont = new Font("Times new roman,", Font.PLAIN, 14);
        txt1 = new JTextField(Main.getLANG().get(37));
        txt1.setBackground(getBackground());
        txt1.setFont(answerFont);
        txt1.setBorder(null);
        txt2 = new JTextField(Main.getLANG().get(37));
        txt2.setBackground(getBackground());
        txt2.setFont(answerFont);
        txt2.setBorder(null);
        txt3 = new JTextField(Main.getLANG().get(37));
        txt3.setBackground(getBackground());
        txt3.setFont(answerFont);
        txt3.setBorder(null);
        txt4 = new JTextField(Main.getLANG().get(37));
        txt4.setBackground(getBackground());
        txt4.setFont(answerFont);
        txt4.setBorder(null);
        txt5 = new JTextField(Main.getLANG().get(37));
        txt5.setBackground(getBackground());
        txt5.setFont(answerFont);
        txt5.setBorder(null);

        // selecting a button marks it as correct
        button1.addActionListener(evt -> {
            selectOpt(txt1, txt2, txt3, txt4, txt5);
            correctAnswer = 1;
        });
        button2.addActionListener(evt -> {
            selectOpt(txt2, txt1, txt3, txt4, txt5);
            correctAnswer = 2;
        });
        button3.addActionListener(evt -> {
            selectOpt(txt3, txt2, txt1, txt4, txt5);
            correctAnswer = 3;
        });
        button4.addActionListener(evt -> {
            selectOpt(txt4, txt2, txt3, txt1, txt5);
            correctAnswer = 4;
        });
        button5.addActionListener(evt -> {
            selectOpt(txt5, txt2, txt3, txt4, txt1);
            correctAnswer = 5;
        });


        c.gridwidth = 2;
        c.gridy = 0;
        c.gridx = 0;
        c.insets.set(0, 0, 5, 0);
        add(question, c);
        c.insets.set(0, 0, 0, 0);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridy++;
        add(button1, c);
        c.gridx = 1;
        add(txt1, c);
        c.gridx = 0;

        c.gridy++;
        add(button2, c);
        c.gridx = 1;
        add(txt2, c);
        c.gridx = 0;

        c.gridy++;
        add(button3, c);
        c.gridx = 1;
        add(txt3, c);
        c.gridx = 0;

        c.gridy++;
        add(button4, c);
        c.gridx = 1;
        add(txt4, c);
        c.gridx = 0;

        c.gridy++;
        add(button5, c);
        c.gridx = 1;
        add(txt5, c);
        c.gridx = 0;


    }

    /**
     * Mark the correct answer with the color BLUE
     * @param i the correct answer
     */
    private void selectOpt(JTextField i, JTextField ii, JTextField iii, JTextField iiii, JTextField iiiii) {
        i.setForeground(Color.BLUE);
        ii.setForeground(Color.ORANGE);
        iii.setForeground(Color.ORANGE);
        iiii.setForeground(Color.ORANGE);
        iiiii.setForeground(Color.ORANGE);
    }

    boolean isSelected() {
        return correctAnswer != 0;
    }

    public QA toQA() {
        return new QA(correctAnswer, question.getText(), txt1.getText(), txt2.getText(), txt3.getText(), txt4.getText(), txt5.getText());
    }
}
