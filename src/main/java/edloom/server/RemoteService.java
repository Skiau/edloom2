package edloom.server;

import edloom.core.Course;
import edloom.core.CourseModule;
import edloom.core.User;

import javax.mail.MessagingException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;


public interface RemoteService extends Remote {
    boolean login(String email, String password) throws RemoteException, SQLException;

    boolean signup(String email, String password, String firstName, String lastName) throws IOException, SQLException;

    boolean isBanned(String email) throws SQLException, RemoteException;

    void sendMail(String email, String contents) throws RemoteException, MessagingException;

    String getKey(int ID) throws RemoteException, SQLException;

    User getUser(String email) throws RemoteException, SQLException;

    void setListed(int ID, boolean b) throws RemoteException, SQLException;

    void setKey(int ID, String key) throws RemoteException, SQLException;

    void setTitle(int ID, String title) throws RemoteException, SQLException;

    Object[][] getListedCourses(String email) throws RemoteException, SQLException;

    Object[][] getEnrolledCourses(String email) throws RemoteException, SQLException;

    Object[][] getPublishedCourses(String email) throws RemoteException, SQLException;

    Course getCourse(int ID) throws RemoteException, SQLException;

    Course getCourse(String email, int ID) throws RemoteException, SQLException;

    void updateUser(String password, String email) throws RemoteException, SQLException;

    void deleteUser(String email) throws RemoteException, SQLException;

    String[][] getAnalytics(int ID) throws RemoteException, SQLException;

    void updateCourse(Course course, String email) throws RemoteException, SQLException;

    void enroll(String email, int ID, Course course) throws RemoteException, SQLException;

    void deleteCourse(int ID) throws RemoteException, SQLException;

    Object[] serializeCourse(String title, int year, String description, String publisher_name, String publisher_email, ArrayList<CourseModule> modules) throws RemoteException, SQLException;

}