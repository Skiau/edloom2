package edloom.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edloom.core.Course;
import edloom.core.CourseModule;
import edloom.core.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import static edloom.core.Statics.generateKey;

@SuppressWarnings("all")
public final class RemoteServiceImp implements RemoteService {

    public RemoteServiceImp() {
    }


    /**
     * @param email    used as primary key in database to identify a account
     * @param password attempt to connect to account with this
     * @return boolean true if the given password matches the database password, else false
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public boolean login(String email, String password) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT password FROM user WHERE email= '" + email + "'");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getString("password").equals(password);
    }

    /**
     * Create a new entry in the users database
     *
     * @param email     user data used as primary key in database
     * @param password  user data used for signing in
     * @param firstName user data - first name
     * @param lastName  user data - last name
     * @return boolean true if no exceptions were thrown, else false
     * @throws SQLException
     */
    @Override
    public boolean signup(String email, String password, String firstName, String lastName) throws SQLException {
        Date today = new java.sql.Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "INSERT INTO user(email,password,first_name,last_name,registered,banned,admin) " +
                        "VALUES('" + email + "','" + password + "','" + firstName + "','" + lastName + "','" + formatter.format(today) + "',0,0)");

        return preparedStatement.executeUpdate() == 1;
    }

    /**
     * Checks if an account is marked as banned din the database
     *
     * @param email used as primary key in database to identify a account
     * @return the boolean value registered in the database under the column "banned"
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public boolean isBanned(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT banned FROM user WHERE email= '" + email + "'");
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next() && resultSet.getBoolean("banned");
    }


    /**
     * Sends an email
     *
     * @param email    send email to this address
     * @param contents message to send
     * @throws RemoteException
     * @throws MessagingException
     */
    @Override
    public void sendMail(String email, String contents) throws RemoteException, MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.mailtrap.io");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.ssl.trust", "smtp.mailtrap.io");

        // TODO use your own Mailtrap username and password to be able to see sent emails
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("61b7aa5d55692f", "b2659424d4e0a4");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@example.com"));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Account created");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(contents, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Gets the enrollment key of a course form the database
     *
     * @param ID used as primary key to identify a course in database
     * @return the enrollment key
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public String getKey(int ID) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("Select enrollment_key FROM course WHERE ID=" + ID + ";");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getString(1);
    }

    /**
     * Constructs a user object from database data
     *
     * @param email used as primary key in database to identify a account
     * @return a User object
     * @throws RemoteException
     * @throws SQLException
     * @see User
     */
    @Override
    public User getUser(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT* FROM user WHERE email= '" + email + "'");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {

            return new User(resultSet.getBoolean("admin"),
                    resultSet.getDate("registered"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("password"));
        }
        return null;
    }

    /**
     * Mark a course as listed/unlisted in the database
     *
     * @param ID used as primary key to identify a course in database
     * @param b  true to make the course listed, false for unlisted
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void setListed(int ID, boolean b) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("UPDATE course SET listed=" + b + " WHERE ID=" + ID + ";");
        preparedStatement.executeUpdate();
    }

    /**
     * Set the enrollment key of a course
     *
     * @param ID  used as primary key to identify a course in database
     * @param key the new key
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void setKey(int ID, String key) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("UPDATE course SET enrollment_key='" + key + "' WHERE ID=" + ID + ";");
        preparedStatement.executeUpdate();
    }


    /**
     * Set the title of a course
     *
     * @param ID    used as primary key to identify a course in database
     * @param title the new title
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void setTitle(int ID, String title) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("UPDATE course SET title='" + title.strip() + "' WHERE ID=" + ID + ";");
        preparedStatement.executeUpdate();
        preparedStatement = Server.getCon().prepareStatement("UPDATE enrollment SET title='" + title.strip() + "' WHERE course=" + ID + ";");
        preparedStatement.executeUpdate();
    }


    /**
     * Get the details of all listed courses that the user is not enrolled to
     *
     * @param email used as primary key to identify a user in database
     * @return a 2D array, each row represents a set of course details
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public Object[][] getListedCourses(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT ID, title, publisher_name, year FROM course WHERE listed=1 AND ID NOT IN (SELECT course from enrollment WHERE user='" + email + "');",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.last();
        Object[][] data = new Object[resultSet.getRow()][5];
        int index = 0;
        resultSet.beforeFirst();
        while (resultSet.next()) {
            data[index][0] = resultSet.getInt("ID");
            data[index][1] = " " + resultSet.getString("title");
            data[index][2] = " " + resultSet.getString("publisher_name");
            data[index][3] = resultSet.getInt("year");
            data[index++][4] = "Enter key";
        }
        return data;
    }

    /**
     * Get the details of all courses that the user is enrolled to
     *
     * @param email used as primary key to identify a user in database
     * @return a 2D array, each row represents a set of course details
     * @throws RemoteException
     * @throws SQLException
     */
    public Object[][] getEnrolledCourses(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT course, title, publisher_name, progress FROM enrollment WHERE user='" + email + "';", ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.last();
        Object[][] data = new Object[resultSet.getRow()][4];
        int index = 0;
        resultSet.beforeFirst();
        while (resultSet.next()) {
            data[index][0] = resultSet.getInt("course");
            data[index][1] = " " + resultSet.getString("title");
            data[index][2] = " " + resultSet.getString("publisher_name");
            data[index++][3] = resultSet.getString("progress");
        }
        return data;
    }

    /**
     * Get the details of all courses that the user (admin) has published
     *
     * @param email used as primary key to identify a user in database
     * @return a 2D array, each row represents a set of course details
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public Object[][] getPublishedCourses(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "SELECT ID, title, enrollment_key, listed FROM course WHERE publisher_email='" + email + "';", ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.last();
        Object[][] data = new Object[resultSet.getRow()][5];
        int index = 0;
        resultSet.beforeFirst();
        while (resultSet.next()) {
            int ID = resultSet.getInt("ID");
            PreparedStatement ps = Server.getCon().prepareStatement("SELECT user FROM enrollment WHERE course=" + ID + ";", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = ps.executeQuery();
            rs.last();

            data[index][0] = ID;
            data[index][1] = " " + resultSet.getString("title");
            data[index][2] = rs.getRow();
            data[index][3] = resultSet.getString("enrollment_key");
            data[index++][4] = resultSet.getBoolean("listed");
        }
        return data;
    }

    /**
     * Constructs a course object from database data
     *
     * @param ID used as primary key to identify a course in database
     * @return the Course object
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public Course getCourse(int ID) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("Select* FROM course WHERE ID =" + ID + ";");
        ResultSet resultSet = preparedStatement.executeQuery();
        Gson gson = new Gson();
        if (resultSet.next())
            return new Course(
                    ID,
                    resultSet.getInt("year"),
                    gson.fromJson(resultSet.getString("modules"), ArrayList.class),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getString("publisher_email"),
                    resultSet.getString("publisher_name"));
        return null;
    }

    /**
     * Constructs a course object associated with a user, has saved progress data
     *
     * @param email used as primary key to identify a user in database
     * @param ID    used as primary key to identify a course in database
     * @return the Course object
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public Course getCourse(String email, int ID) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("SELECT object FROM enrollment WHERE user ='" + email + "' AND course =" + ID + ";");
        ResultSet resultSet = preparedStatement.executeQuery();
        Gson gson = new Gson();
        if (resultSet.next())
            return gson.fromJson(resultSet.getString("object"), Course.class);

        return null;
    }

    /**
     * Update user password
     *
     * @param email    used as primary key to identify a user in database
     * @param password the new password
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void updateUser(String password, String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("UPDATE user SET password ='" + password + "' WHERE email='" + email + "';");
        preparedStatement.executeUpdate();

    }

    /**
     * Delete a user from database
     *
     * @param email used as primary key to identify a user in database
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void deleteUser(String email) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("DELETE FROM user WHERE email='" + email + "';");
        preparedStatement.executeUpdate();
    }


    /**
     * Get a list of enrolled students and their progress through a course
     *
     * @param ID used as primary key to identify a course in database
     * @return a 2D array, each row represents a student set of details
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public String[][] getAnalytics(int ID) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("SELECT email, first_name, last_name, progress FROM user JOIN enrollment ON enrollment.user = user.email WHERE course =" + ID + ";",
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.last();
        String[][] data = new String[resultSet.getRow()][4];
        int index = 0;
        resultSet.beforeFirst();
        while (resultSet.next()) {
            data[index][0] = resultSet.getString("email");
            data[index][1] = resultSet.getString("first_name");
            data[index][2] = resultSet.getString("last_name");
            data[index++][3] = resultSet.getString("progress");
        }
        return data;
    }

    /**
     * Update the saved progress data of a course associated with a student
     *
     * @param course the course to update
     * @param email  used as primary key to identify a user in database
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void updateCourse(Course course, String email) throws RemoteException, SQLException {
        Gson gson = new Gson();
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("UPDATE enrollment SET object='" + gson.toJson(course) + "' WHERE user='" + email + "' AND course=" + course.getID() + ";");
        preparedStatement.executeUpdate();
        int sum = 0; // total percentage
        int qn = 0; // number of quizzes
        for (CourseModule cm : course.getModules()) {
            if (cm.getQuiz() != null) {
                qn++;
                if (cm.getQuiz().getScore() < 0)
                    return;
                else sum += cm.getQuiz().getScore();
            }
        }
        String result;
        if (sum / qn < 50) result = "failed";
        else result = "passed";
        preparedStatement = Server.getCon().prepareStatement("UPDATE enrollment SET progress='" + result + "' WHERE user='" + email + "' AND course=" + course.getID() + ";");
        preparedStatement.executeUpdate();

    }

    /**
     * Enroll a user to a course
     *
     * @param email  used as primary key to identify a user in database
     * @param ID     used as primary key to identify a course in database
     * @param course give the user a unique Course object to track progress
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void enroll(String email, int ID, Course course) throws RemoteException, SQLException {
        Gson gson = new Gson();
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("INSERT INTO enrollment(user, course, title, publisher_name, object, progress) " +
                "VALUES ('" + email + "','" + ID + "','" + course.getTitle() + "','" + course.getPublisherName() + "','" + gson.toJson(course) + "', 'ongoing');");
        preparedStatement.executeUpdate();
    }

    /**
     * Delete a course from database
     *
     * @param ID used as primary key to identify a course in database
     * @throws RemoteException
     * @throws SQLException
     */
    @Override
    public void deleteCourse(int ID) throws RemoteException, SQLException {
        PreparedStatement preparedStatement = Server.getCon().prepareStatement("DELETE FROM enrollment WHERE course=" + ID + ";");
        preparedStatement.executeUpdate();
        preparedStatement = Server.getCon().prepareStatement("DELETE FROM course WHERE ID=" + ID + ";");
        preparedStatement.executeUpdate();
    }


    @Override
    public synchronized Object[] serializeCourse(String title, int year, String description, String publisher_name, String publisher_email, ArrayList<CourseModule> modules) throws RemoteException, SQLException {
        Gson gson = new GsonBuilder().create();
        String key = generateKey();
        PreparedStatement preparedStatement = Server.getCon().prepareStatement(
                "INSERT INTO course(title, year, description, publisher_name, publisher_email, enrollment_key,listed, modules)" +
                        " VALUES('" + title + "','" + year + "','" + description + "','" + publisher_name + "','" + publisher_email + "','" + key + "','" + "0','" + gson.toJson(modules) + "');", Statement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        generatedKeys.next();
        return new Object[]{generatedKeys.getInt(1), key};

    }

    /**
     * Creates a stub for RMI implementation
     *
     * @throws RemoteException
     */
    public void createStub() throws RemoteException {
        RemoteService stub = (RemoteService) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.createRegistry(1099);
        registry.rebind("RemoteService", stub);
    }
}