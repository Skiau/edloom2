package edloom.core;

import java.io.Serializable;
import java.util.ArrayList;


public class Course implements Serializable {
    private final String title, description, publisherName, publisherEmail;
    protected int ID, studYear;
    private final ArrayList<CourseModule> modules;

    public Course(int ID, int studYear, ArrayList<CourseModule> courseModules, String... extra) {
        this.ID = ID;
        this.studYear = studYear;
        modules = courseModules;
        title = extra[0];
        description = extra[1];
        publisherEmail = extra[2];
        publisherName = extra[3];
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getID() {
        return ID;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getPublisherEmail() {
        return publisherEmail;
    }

    public ArrayList<CourseModule> getModules() {
        return modules;
    }


}
