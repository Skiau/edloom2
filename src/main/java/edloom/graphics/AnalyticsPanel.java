package edloom.graphics;

import com.google.common.io.Files;
import edloom.Main;
import edloom.core.Course;
import edloom.core.CourseModule;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Arrays;

public class AnalyticsPanel extends JPanel {
    public AnalyticsPanel(JTabbedPane tabs, String[][] data, int ID) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // create a panel for the west border layout
        JPanel westPanel = new JPanel();
        westPanel.setOpaque(false);
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));

        // create a panel for the center border layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        // add the west panel to a scrollPane
        JScrollPane scrollerPane = new JScrollPane(westPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollerPane.getVerticalScrollBar().setUnitIncrement(9);
        scrollerPane.setOpaque(false);
        scrollerPane.getViewport().setOpaque(false);


        // a table displaying all enrolled students of given course
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("email");
        model.addColumn(Main.getLANG().get(17));
        model.addColumn("progress");
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 17));
        table.setRowHeight(24);
        table.setDefaultRenderer(Object.class, new Visitor3Renderer());
        table.setAutoCreateRowSorter(true);
        table.setBorder(null);
        TableColumnModel cmod = table.getColumnModel();
        cmod.getColumn(0).setMinWidth(0);
        cmod.getColumn(0).setMaxWidth(0);
        cmod.getColumn(1).setMaxWidth(300);
        cmod.getColumn(1).setPreferredWidth(150);
        cmod.getColumn(2).setMinWidth(0);
        cmod.getColumn(2).setMaxWidth(0);

        int p = 0;
        int f = 0;
        int o = 0;
        for (String[] row : data) {
            model.addRow(new String[]{row[0], " " + row[1] + " " + row[2], row[3]});
            if (row[3].equals("failed"))
                f++;
            else if (row[3].equals("passed"))
                p++;
            else o++;
        }

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2) {
                    try {
                        String studEmail = table.getValueAt(table.getSelectedRow(), 0).toString();
                        addStudentGraph(centerPanel, c, Main.getService().getCourse(studEmail, ID), studEmail);
                    } catch (RemoteException | SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // add the table to the west panel
        westPanel.add(table.getTableHeader());
        westPanel.add(table);


        // the close tab button
        c.weighty = 1.0;
        c.weightx = 1.0;
        MyButton closeTabButton = new MyButton("X");
        closeTabButton.setColor(Color.RED);
        closeTabButton.addActionListener(e -> {
            centerPanel.removeAll();
            tabs.remove(this);
        });

        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.gridx = 0;
        c.gridy = 0;
        centerPanel.add(closeTabButton, c);


        // add the remaining components
        addBasicGraph(centerPanel, c, f, p, o);
        add(scrollerPane, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

    }

    private void addBasicGraph(JPanel panel, GridBagConstraints c, int f, int p, int o) {
        Arrays.stream(panel.getComponents()).skip(1).forEach(panel::remove); // remove all components except the close tab button

        // create & add a pie chart of collective students progress
        var dataset = new DefaultPieDataset<String>();
        dataset.setValue("Passed", p);
        dataset.setValue("Failed", f);
        dataset.setValue("Ongoing", o);
        JFreeChart chart = ChartFactory.createPieChart(
                null,
                dataset,
                false, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getPlot().setDrawingSupplier(new ChartDrawingSupplier());
        chart.getPlot().setOutlineVisible(false);
        chartPanel.setPreferredSize(new Dimension(300, 300));

        c.anchor = GridBagConstraints.PAGE_START;
        c.gridy = 1;
        panel.add(chartPanel, c);
        panel.revalidate();
        panel.repaint();
    }


    private void addStudentGraph(JPanel panel, GridBagConstraints c, Course course, String email) {
        Arrays.stream(panel.getComponents()).skip(1).forEach(panel::remove); // remove all components except the close tab button
        if (course != null) {

            // create & add a bar chart of individual student progress
            var dataset = new DefaultCategoryDataset();
            c.anchor = GridBagConstraints.LINE_START;
            c.gridy = 0;
            JTextPane studEmail = new JTextPane();
            studEmail.setText(email);
            studEmail.setEditable(false);
            panel.add(studEmail, c);

            int counter = 0; // to help notate the X axis legend

            c.gridy = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            JFreeChart barChart = ChartFactory.createBarChart(
                    Main.getLANG().get(55),
                    "",
                    Main.getLANG().get(56),
                    dataset,
                    PlotOrientation.VERTICAL,
                    false, true, false);
            CategoryPlot plot = (CategoryPlot) barChart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setRange(0, 100);
            ChartPanel chartPanel = new ChartPanel(barChart);
            chartPanel.setPreferredSize(new Dimension(chartPanel.getWidth(), 300));
            panel.add(chartPanel, c);

            c.fill = GridBagConstraints.NONE;
            c.insets.set(10, 10, 0, 0);
            c.gridy++;
            c.insets.set(5, 10, 0, 0);
            panel.add(new JLabel(Main.getLANG().get(57)), c);
            for (CourseModule module : course.getModules()) {
                counter++;
                c.gridy++;
                if (module.getQuiz() != null) {
                    dataset.setValue(module.getQuiz().getScore(), Main.getLANG().get(56), Main.getLANG().get(31).charAt(0) + String.valueOf(counter));
                }

                if (module.getEndDate() != null) {
                    MyButton jButton = new MyButton();
                    jButton.setText(module.getSubmissionsName());
                    if (module.getSubmissions() != null)
                        jButton.addActionListener(e -> {
                            try {
                                Files.write(module.getSubmissions(), new File(System.getProperty("user.home") + "\\" + module.getSubmissionsName()));
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });
                    else jButton.setEnabled(false);
                    panel.add(jButton, c);

                }

            }


        } else panel.add(new JLabel(Main.getLANG().get(42))); // let the user know if the course was not found
        panel.revalidate();
        panel.repaint();
    }


}

