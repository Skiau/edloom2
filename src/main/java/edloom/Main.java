package edloom;

import edloom.graphics.UI;
import edloom.server.RemoteService;
import edloom.server.Server;
import edloom.core.Language;

import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static edloom.graphics.Statics.notifyWindow;
import static edloom.core.Statics.generateLang;


public class Main {
    private static UI ui;
    private static ArrayList<String> LANG;
    private static ArrayList<Language> LANGarray;
    private static RemoteService service = null;

    public static void main(String[] args) throws FileNotFoundException {

        // Start the server (is a thread only for simulation purposes)
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

        // Connect to the server
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

        // read and load language files
        LANGarray = new ArrayList<>();
        generateLang(LANGarray);
        try {
            LANG = LANGarray.get(0);
        } catch (IndexOutOfBoundsException e) {
            LANG = new Language("default");
            for (int i = 0; i < 100; i++) LANG.add("?");
            notifyWindow("No languages detected ", "File processing", 0);
        }

        ui = new UI();
        ui.loggedOUT();
    }


    public static ArrayList<String> getLANG() {
        return LANG;
    }

    public static void setLANG(Language language) {
        LANG = language;
    }

    public static ArrayList<Language> getLANGarray() {
        return LANGarray;
    }

    public static UI getUI() {
        return ui;
    }

    public static RemoteService getService() {
        return service;
    }
}
