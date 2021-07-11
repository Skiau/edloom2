package edloom.core;

import java.io.Serializable;

public class QA implements Serializable {
    private final String question;
    private final String[] answers;
    private final int correctAnswer;

    public QA(int correctAnswer, String question, String... answers) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }
}
