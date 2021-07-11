package edloom.graphics;

import edloom.Main;
import edloom.core.CourseModule;
import edloom.core.Link;
import edloom.core.Quiz;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static edloom.core.Statics.getInterval;
import static edloom.graphics.Statics.urlForm;

public class ModulePanel extends JPanel {
    // memorize variables that will be passed as constructor parameters for a CourseModule
    private Quiz quiz;
    private final ArrayList<Link> links = new ArrayList<>();
    private Date startDate, endDate;
    private final JButton quizButton;
    private QuizBuilder quizBuilder = null;
    private final ArrayList<JFrame> fileRefs = new ArrayList<>();

    public ModulePanel(int weeks) {
        // setup ModulePanel
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
                Main.getLANG().get(31) + " " + weeks,
                TitledBorder.CENTER,
                TitledBorder.ABOVE_TOP));
        ((TitledBorder) getBorder()).setTitleFont(new Font(getFont().toString(), Font.PLAIN, 14));


        // count weeks added towards endDate
        AtomicInteger counter = new AtomicInteger(0);

        // instantiate, construct & customize components to be added to this
        JLabel endLabel = new JLabel();
        JButton filesButton = new MyButton("+ file", new Dimension(62, 15));
        quizButton = new MyButton("Q", new Dimension(44, 15));
        MyButton plusWeek = new MyButton(">");
        MyButton minusWeek = new MyButton("<");
        plusWeek.setColor(Color.BLUE);
        minusWeek.setColor(Color.BLUE);


        // create the date combo boxes and set to the current date
        MyComboBox<Integer> dayBox = new MyComboBox<>(getInterval(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH), 1), 33);
        MyComboBox<Integer> yearBox = new MyComboBox<>(getInterval(10, Calendar.getInstance().get(Calendar.YEAR)), 45);
        MyComboBox<String> monthBox = new MyComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"}, 42);
        monthBox.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        dayBox.setSelectedIndex(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);

        // set initial values for startDate & endDate
        updateStart(
                (Integer) Objects.requireNonNull(yearBox.getSelectedItem()),
                monthBox.getSelectedIndex(),
                dayBox.getSelectedIndex() + 1);
        updateEnd(endLabel, counter);


        // add action listeners
        yearBox.addActionListener(e -> {    // if year is changed, days are updated, then start and end dates are also updated
            Calendar calendar = Calendar.getInstance();
            int year = (Integer) Objects.requireNonNull(yearBox.getSelectedItem());
            int month = monthBox.getSelectedIndex();
            calendar.set(year, month, 1);
            setDays(dayBox, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            int day = dayBox.getSelectedIndex() + 1;
            updateStart(year, month, day);
            updateEnd(endLabel, counter);

        });

        monthBox.addActionListener(e -> {   // if month is changed, days are updated, then start and end dates are also updated
            Calendar calendar = Calendar.getInstance();
            int year = (Integer) Objects.requireNonNull(yearBox.getSelectedItem());
            int month = monthBox.getSelectedIndex();
            calendar.set(year, month, 1);
            setDays(dayBox, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            int day = dayBox.getSelectedIndex() + 1;
            updateStart(year, month, day);
            updateEnd(endLabel, counter);
        });

        dayBox.addActionListener(e -> {     // if day is changed, start and end dates are updated
            int year = (Integer) Objects.requireNonNull(yearBox.getSelectedItem());
            int month = monthBox.getSelectedIndex();
            int day = dayBox.getSelectedIndex() + 1;
            updateStart(year, month, day);
            updateEnd(endLabel, counter);
        });

        plusWeek.addActionListener(e -> {   // set endDate forward one week, update the endLabel too
            minusWeek.setEnabled(true);
            Date date;
            try {
                date = new SimpleDateFormat("dd.MMM.yyyy").parse(endLabel.getText());
            } catch (ParseException parseException) {
                date = startDate;
                counter.getAndDecrement();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Objects.requireNonNull(date));
            calendar.add(Calendar.DATE, 7);
            endDate = calendar.getTime();
            endLabel.setText(new SimpleDateFormat("dd.MMM.yyyy").format(endDate));
            counter.getAndIncrement();

        });

        minusWeek.addActionListener(e -> {   // set endDate back one week, update the endLabel too
            if (counter.get() > 0) {
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd.MMM.yyyy").parse(endLabel.getText());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Objects.requireNonNull(date));
                calendar.add(Calendar.DATE, -7);
                endDate = calendar.getTime();
                endLabel.setText(new SimpleDateFormat("dd.MMM.yyyy").format(endDate));
                counter.getAndDecrement();
            } else {
                minusWeek.setEnabled(false);
                endLabel.setText(Main.getLANG().get(44));
                endDate = null;
            }

        });

        quizButton.addActionListener(e -> quizBuilder = new QuizBuilder(this, ((TitledBorder) getBorder()).getTitle()));
        filesButton.addActionListener(e -> fileRefs.add(urlForm(this, c)));

        // add components using layout
        c.anchor = GridBagConstraints.CENTER;
        c.insets.set(10, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        add(new JLabel(Main.getLANG().get(32)), c);

        c.insets.set(10, 0, 0, 0);
        c.gridy = 0;
        c.gridx = 3;
        c.gridwidth = 3;
        add(new JLabel(Main.getLANG().get(33)), c);

        c.insets.set(5, 0, 0, 5);
        c.gridwidth = 1;
        c.gridy = 1;
        c.gridx = 0;
        add(dayBox, c);

        c.gridy = 1;
        c.gridx = 1;
        add(monthBox, c);

        c.gridy = 1;
        c.gridx = 2;
        add(yearBox, c);

        c.insets.set(5, 0, 0, 0);
        c.gridx = 3;
        add(minusWeek, c);

        c.gridx = 4;
        add(endLabel, c);

        c.gridx = 5;
        add(plusWeek, c);

        c.insets.set(10, 0, 0, 0);
        c.gridy = 2;
        c.gridx = 4;
        c.gridwidth = 2;
        c.gridheight = 50;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        add(filesButton, c);

        c.anchor = GridBagConstraints.LAST_LINE_START;
        add(quizButton, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.gridheight = 1;
        c.gridwidth = 3;

        //reset for adding file rows
        c.gridwidth = 6;
        c.gridx = 0;
        c.gridy = 2;
    }

    /**
     * Update this start date with given year, month and day
     */
    private void updateStart(int year, int month, int day) {
        startDate = new GregorianCalendar(year, month, day).getTime();
        Calendar.getInstance().setTime(startDate);
        repaint(); // remove artifacts, cause unknown
    }

    /**
     * Update this end date relative to start date
     *
     * @param endLabel this label will be set to display the new end date
     * @param counter  used to stop the user from setting an end date before start date by enabling/disabling a certain button
     */
    private void updateEnd(JLabel endLabel, AtomicInteger counter) { // end date is relative to start date, so it has to change accordingly
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.add(Calendar.DATE, 7);
        endDate = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd.MMM.yyyy");
        endLabel.setText(dateFormat.format(cal.getTime()));
        counter.set(0);
    }

    /**
     * Sets the length of a JComboBox according to a maximum value
     *
     * @param dayBox the JComboBox to be set
     * @param max    the maximum value
     */
    private void setDays(JComboBox<Integer> dayBox, int max) {  // set the JComboBox for days
        int index = dayBox.getSelectedIndex();
        Integer[] days = new Integer[max];
        for (int i = 0; i < max; i++) {
            days[i] = i + 1;
        }
        DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<>(days);
        dayBox.setModel(model);
        if (index + 1 > dayBox.getItemCount())
            dayBox.setSelectedIndex(dayBox.getItemCount() - 1);
        else dayBox.setSelectedIndex(index);
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }


    public void addLink(Link link, GridBagConstraints c) {
        links.add(link);
        MyButton linkButton = new MyButton(link.getName(), link.getUrl(), Color.BLUE);
        linkButton.setPreferredSize(new Dimension(170, 20));
        add(linkButton, c);
        revalidate();
        c.gridy++;
    }


    /**
     * Takes all user inputs in this component and creates a CourseModule object from them
     */
    CourseModule toModule() {
        return new CourseModule(startDate, endDate, quiz, links);
    }

    public void disposeQuizBuilder() {
        if (quizBuilder != null) {
            quizBuilder.dispose();
        }
    }

    /**
     * Dispose of any opened JFrames from this Component
     */
    public void disposeFileRefs() {
        for (JFrame f : fileRefs)
            f.dispose();
    }

    public JButton getCaller() {
        return quizButton;
    }


}