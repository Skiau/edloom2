package edloom.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static edloom.graphics.Statics.notifyWindow;

public class Server implements Runnable {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static Connection con;

    public Server() {
    }

    @Override
    public void run() { // the thread simulates a server application running separately from the client application
        System.out.println("Server thread started...");
        RemoteServiceImp service = new RemoteServiceImp();
        try {
            service.createStub();
            System.err.println(ANSI_GREEN + "Server connected" + ANSI_RESET);
        } catch (RemoteException e) {
            System.err.println("Server failure");
            e.printStackTrace();
        }

        setConnection("jdbc:mysql://127.0.0.1:3306/edloomdb", "root", ""); //TODO change this if necessary
    }


    // Attempts to create the connection with the provided parameters
    public static void setConnection(String url, String user, String password) {
        System.out.println("Logging into database...");
        try {
            Connection connection = DriverManager.getConnection(
                    url, user, password);
            if (connection != null) {
                con = connection;
                System.out.println(Server.ANSI_GREEN + "Database access granted" + Server.ANSI_RESET);
            } else {
                System.err.println("Database access denied");
                notifyWindow("Your MySQL connection is null.", "MySQL error", 0);
            }
        } catch (SQLException e) {
            System.err.println("Database access denied");
            notifyWindow("Something went wrong :\\", "MySQL error", 0);
            e.printStackTrace();
        }
    }


    public static Connection getCon() {
        return con;
    }

}
