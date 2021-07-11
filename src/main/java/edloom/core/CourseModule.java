package edloom.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CourseModule implements Serializable {
    private final Date startDate, endDate;
    private final Quiz quiz;
    private final ArrayList<Link> links;
    private byte[] submissions = null;
    private String submissionsName= "N/A";

    public CourseModule(Date startDate, Date endDate, Quiz quiz, ArrayList<Link> links) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.quiz = quiz;
        this.links = links;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public byte[] getSubmissions() {
        return submissions;
    }

    public String getSubmissionsName() {
        return submissionsName;
    }

    public void submit(byte[] file, String name) {
        submissions = file;
        submissionsName = name;
    }
}
