package edloom.core;

import java.io.Serializable;
import java.util.ArrayList;

public class Quiz implements Serializable {
    private final String password;
    private final int time;
    private final ArrayList<QA> qaList;
    private int score = -1;

    public Quiz(int time, String password, ArrayList<QA> qaList) {
        this.password = password;
        this.time = time;
        this.qaList = qaList;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<QA> getQaList() {
        return qaList;
    }

    public int getTime() {
        return time;
    }

    public void setScore(int percentage) {
        score = percentage;
    }

    public int getScore() {
        return score;
    }
}
