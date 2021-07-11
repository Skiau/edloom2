package edloom.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {
    boolean admin;
    private final Date registeredDate;
    private final String firstName, lastName, email;
    private String password;

    public enum details {EMAIL, FIRSTNAME, LASTNAME, PASSWORD, DATE}

    // to create a new User
    public User(boolean admin, String... extra) {
        firstName = extra[0];
        lastName = extra[1];
        email = extra[2];
        password = extra[3];
        this.admin = admin;
        registeredDate = Calendar.getInstance().getTime();
    }

    // to create a User from existing data
    public User(boolean admin, Date registeredDate, String... extra) {
        this.admin = admin;
        this.registeredDate = registeredDate;
        firstName = extra[0];
        lastName = extra[1];
        email = extra[2];
        password = extra[3];
    }


    // a fancy getter method
    public String get(details d) {
        switch (d) {
            case EMAIL -> {
                return email;
            }

            case FIRSTNAME -> {
                return firstName;
            }

            case LASTNAME -> {
                return lastName;
            }
            case PASSWORD -> {
                return password;
            }
            case DATE -> {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MMM.yyyy");
                return simpleDateFormat.format(registeredDate);
            }

            default -> {
                return null;
            }
        }


    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }
}
