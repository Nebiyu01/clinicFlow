import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class DatePicker extends JDialog {
    private LocalDate selectedDate;
    private LocalDate currentMonth;
    private JPanel daysPanel;
    private JLabel monthLabel;

    public DatePicker(Frame parent, LocalDate initialDate) {
        super(parent, "Select Date", true);
        if (initialDate == null) {
            initialDate = LocalDate.now();
        }
        this.selectedDate = null;
        this.currentMonth = initialDate.withDayOfMonth(1);
        buildUI();
        refreshCalendar();
        pack();
        setLocationRelativeTo(parent);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");

        monthLabel = new JLabel("", SwingConstants.CENTER);

        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            refreshCalendar();
        });

        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            refreshCalendar();
        });

        topPanel.add(prevButton, BorderLayout.WEST);
        topPanel.add(monthLabel, BorderLayout.CENTER);
        topPanel.add(nextButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        daysPanel = new JPanel(new GridLayout(0, 7));
        add(daysPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            selectedDate = null;
            dispose();
        });
        bottom.add(cancelButton);
        add(bottom, BorderLayout.SOUTH);
    }

    private void refreshCalendar() {
        daysPanel.removeAll();

        LocalDate today = LocalDate.now();
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        // Weekday headers
        String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String h : headers) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            daysPanel.add(lbl);
        }

        DayOfWeek firstDow = currentMonth.getDayOfWeek();
        int startOffset = firstDow.getValue() % 7; // Sunday 0

        for (int i = 0; i < startOffset; i++) {
            daysPanel.add(new JLabel("")); // empty cells before first day
        }

        int daysInMonth = currentMonth.lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) {
            LocalDate date = currentMonth.withDayOfMonth(d);
            JButton dayButton = new JButton(String.valueOf(d));
            dayButton.setMargin(new Insets(1, 1, 1, 1));

            if (date.isBefore(today)) {
                dayButton.setEnabled(false);
            }

            dayButton.addActionListener(e -> {
                if (!dayButton.isEnabled()) {
                    return;
                }
                selectedDate = date;
                dispose();
            });

            daysPanel.add(dayButton);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}
