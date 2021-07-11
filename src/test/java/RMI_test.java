
import edloom.server.RemoteService;
import edloom.server.Server;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;

import static edloom.graphics.Statics.notifyWindow;
import static org.junit.Assert.*;

@SuppressWarnings("FieldCanBeLocal")
public class RMI_test {
    private Server server;
    Thread serverThread;
    RemoteService service;

    @Before
    public void init() {
        server = new Server();
        serverThread = new Thread(server);
        serverThread.start();
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry();
            service = (RemoteService) registry
                    .lookup("RemoteService");

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Server disconnected");
            serverThread.interrupt();
            notifyWindow("Could not connect to the server", "Server feedback", 0);
        }
    }

    @Test
    public void loginTest() {

        try {
            Registry registry = LocateRegistry.getRegistry();
            RemoteService service = (RemoteService) registry.lookup("RemoteService");
            assertTrue(service.signup("example@ex.ex", "password1", "AB", "CD"));
            assertTrue(service.login("example@ex.ex", "password1"));
            assertTrue(service.login("EXAMPLE@EX.EX", "password1"));
            assertFalse(service.login("example@ex.ex", "Password1"));
            assertFalse(service.login("example@ex.ex", ""));
            service.deleteUser("example@ex.ex");
            assertNull(service.getUser("example@ex.ex"));

        } catch (NotBoundException | SQLException | IOException e) {
            e.printStackTrace();
        }

    }




}