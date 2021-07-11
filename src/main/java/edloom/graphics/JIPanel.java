package edloom.graphics;

import edloom.Main;
import edloom.core.User;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;

public class JIPanel extends JPanel {
    private final Image background = new ImageIcon("src/Images/bg2.jpg").getImage();
    private final HomePanel home;
    private final DashboardPanel dashboard;
    private WorkspacePanel workspace;
    private final JTabbedPane tabs;

    public JIPanel() throws IOException, SQLException {

        // this panel
        setLayout(new BorderLayout());
        setOpaque(false);

        // create and setup the tabbed pane that goes in CENTER
        dashboard = new DashboardPanel();
        home = new HomePanel();

        tabs = new JTabbedPane(JTabbedPane.LEFT);
        JScrollPane scrollHome = new JScrollPane(home,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollHome.getVerticalScrollBar().setUnitIncrement(9);
        scrollHome.setOpaque(false);
        scrollHome.getViewport().setOpaque(false);


        tabs.setBorder(null);
        tabs.setFocusable(false);
        tabs.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }

        });


        tabs.addTab("<html><h1 style ='padding:16px; font-size:12px'>Home</h1></html>", scrollHome);
        tabs.addTab("<html><h1 style ='padding:16px; font-size:12px'>Dashboard</h1></html>", dashboard);
        // if the user is an admin, instantiate & add an additional workspace tab
        if (UI.user.isAdmin()) {
            workspace = new WorkspacePanel();
            tabs.addTab("<html><h1 style ='padding:16px; font-size:12px'>Workspace</h1></html>", workspace);
        }

        // display only the details of the courses in HomePanel, without constructing the actual Course objects
        home.displayCourses(Main.getService().getListedCourses(UI.user.get(User.details.EMAIL)));


        tabs.addChangeListener(e -> {   // every time a new tab is selected, it refreshes its content
            int index = tabs.getSelectedIndex();
            if (index == 0) {
                try {
                    home.updateData();
                } catch (SQLException | RemoteException throwables) {
                    throwables.printStackTrace();
                }
            }
            // refresh table in dashboard every time this tab is accessed
            else if (index == 1) dashboard.updateData();
            else workspace.updateData();
        });

        add(tabs, BorderLayout.CENTER);
        add(new TopPanel(this), BorderLayout.NORTH);
    }

    @Override
    public void paintComponent(Graphics g) {    // scalable background
        super.paintComponent(g);
        int width = this.getSize().width;
        int height = this.getSize().height;

        if (this.background != null) {
            g.drawImage(this.background, 0, 0, width, height, null);
        }
    }

    public void updateLanguage() {
        home.updateLanguage();
        dashboard.updateLanguage();
        workspace.updateLanguage();
    }

    public HomePanel getHome() {
        return home;
    }

    public DashboardPanel getDash() {
        return dashboard;
    }

    public WorkspacePanel getWork() {
        return workspace;
    }
}
