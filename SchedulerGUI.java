import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;

public class SchedulerGUI extends JFrame {

    private AppointmentManager manager;

    private JPanel mainPanel;
    private CardLayout mainLayout;

    

    private JTabbedPane tabbedPane;

    // Schedule tab
    private DefaultComboBoxModel<Patient> patientComboModel;
    private JComboBox<Patient> patientCombo;
    private JTextField newPatientNameField;
    private JTextField newPatientContactField;
    private JButton addPatientButton;

    private DefaultComboBoxModel<Doctor> doctorComboModel;
    private JComboBox<Doctor> doctorCombo;

    private JTextField dateField;
    private JButton dateButton;

    private DefaultComboBoxModel<LocalTime> timeComboModel;
    private JComboBox<LocalTime> timeCombo;

    private JButton scheduleButton;

    // View/Cancel tab
    private DefaultComboBoxModel<Doctor> doctorComboModel2;
    private JComboBox<Doctor> doctorCombo2;
    private JTextField dateField2;
    private JButton dateButton2;
    private DefaultListModel<String> apptListModel;
    private JList<String> apptList;
    private JButton viewButton;
    private JButton cancelButton;

    // who is logged in
    private String currentStaff;
    private JLabel staffStatusLabel;


    public SchedulerGUI(AppointmentManager manager) {
        this.manager = manager;
        initializeGUI();
    }

    private void initializeGUI() {
    mainLayout = new CardLayout();
    mainPanel = new JPanel(mainLayout);
    add(mainPanel);

    // Only build the app UI, no login panel
    buildAppPanel();

    // Show the app immediately
    mainLayout.show(mainPanel, "APP");

    // Load dropdown lists
    refreshPatientCombo();
    refreshDoctorCombos();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 450);
}



    private void buildLoginPanel() {

        
    }


    private void buildAppPanel() {
        JPanel appPanel = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        JPanel schedPanel = buildScheduleTab();
        JPanel viewPanel = buildViewTab();

        tabbedPane.addTab("Schedule Appointment", schedPanel);
        tabbedPane.addTab("View / Cancel", viewPanel);

        appPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(appPanel, "APP");
    }

    private JPanel buildScheduleTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Existing patients dropdown
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Select Patient:"), gc);
        gc.gridx = 1;
        patientComboModel = new DefaultComboBoxModel<>();
        patientCombo = new JComboBox<>(patientComboModel);
        patientCombo.setPrototypeDisplayValue(new Patient("PXXX", "Long Name Example", "555-5555"));
        panel.add(patientCombo, gc);

        row++;
        // New patient section
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("New Patient Name:"), gc);
        gc.gridx = 1;
        newPatientNameField = new JTextField(15);
        panel.add(newPatientNameField, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("New Patient Contact:"), gc);
        gc.gridx = 1;
        newPatientContactField = new JTextField(15);
        panel.add(newPatientContactField, gc);

        row++;
        gc.gridx = 1; gc.gridy = row;
        addPatientButton = new JButton("Add Patient");
        panel.add(addPatientButton, gc);

        addPatientButton.addActionListener(e -> handleAddPatient());

        // Doctor dropdown
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Doctor:"), gc);
        gc.gridx = 1;
        doctorComboModel = new DefaultComboBoxModel<>();
        doctorCombo = new JComboBox<>(doctorComboModel);
        panel.add(doctorCombo, gc);
        doctorCombo.addActionListener(e -> refreshTimeSlots());

        // Date picker
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Date:"), gc);
        gc.gridx = 1;
        dateField = new JTextField(10);
        dateField.setEditable(false);
        panel.add(dateField, gc);
        gc.gridx = 2;
        dateButton = new JButton("Select Date");
        panel.add(dateButton, gc);

        dateButton.addActionListener(e -> {
            openDatePicker(dateField);
            refreshTimeSlots();   // update slots after picking date
        });

        // Time slots combo
        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Time slot:"), gc);
        gc.gridx = 1;
        timeComboModel = new DefaultComboBoxModel<>();
        timeCombo = new JComboBox<>(timeComboModel);
        timeCombo.setPrototypeDisplayValue(LocalTime.of(9, 0)); // for width
        panel.add(timeCombo, gc);


        // Schedule button
        row++;
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 3;
        gc.anchor = GridBagConstraints.CENTER;
        scheduleButton = new JButton("Schedule Appointment");
        panel.add(scheduleButton, gc);

        scheduleButton.addActionListener(e -> handleSchedule());

        return panel;
    }

    private JPanel buildViewTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Doctor:"), gc);
        gc.gridx = 1;
        doctorComboModel2 = new DefaultComboBoxModel<>();
        doctorCombo2 = new JComboBox<>(doctorComboModel2);
        panel.add(doctorCombo2, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Date:"), gc);
        gc.gridx = 1;
        dateField2 = new JTextField(10);
        dateField2.setEditable(false);
        panel.add(dateField2, gc);
        gc.gridx = 2;
        dateButton2 = new JButton("Select Date");
        panel.add(dateButton2, gc);

        dateButton2.addActionListener(e -> openDatePicker(dateField2));

        row++;
        gc.gridx = 0; gc.gridy = row;
        viewButton = new JButton("View Schedule");
        panel.add(viewButton, gc);

        row++;
        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel("Appointments:"), gc);

        row++;
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 3;
        apptListModel = new DefaultListModel<>();
        apptList = new JList<>(apptListModel);
        apptList.setVisibleRowCount(8);
        apptList.setFixedCellWidth(350);
        JScrollPane scroll = new JScrollPane(apptList);
        panel.add(scroll, gc);

        row++;
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        cancelButton = new JButton("Cancel Selected Appointment");
        cancelButton.setEnabled(false);
        panel.add(cancelButton, gc);

        viewButton.addActionListener(e -> handleViewSchedule());
        cancelButton.addActionListener(e -> handleCancel());

        return panel;
    }

    // ---------- Helpers ----------

    private void refreshPatientCombo() {
        patientComboModel.removeAllElements();
        List<Patient> all = manager.getAllPatients();
        for (Patient p : all) {
            patientComboModel.addElement(p);
        }
        if (!all.isEmpty()) {
            patientCombo.setSelectedIndex(0);
        }
    }

    private void refreshDoctorCombos() {
        doctorComboModel.removeAllElements();
        doctorComboModel2.removeAllElements();
        List<Doctor> all = manager.getAllDoctors();
        for (Doctor d : all) {
            doctorComboModel.addElement(d);
            doctorComboModel2.addElement(d);
        }
        if (!all.isEmpty()) {
            doctorCombo.setSelectedIndex(0);
            doctorCombo2.setSelectedIndex(0);
        }
    }

    private void refreshTimeSlots() {
        timeComboModel.removeAllElements();

        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        String dateStr = dateField.getText().trim();

        if (doctor == null || dateStr.isEmpty()) {
            return; // nothing to populate yet
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            return; // invalid date in field; keep combo empty
        }

        List<LocalTime> slots = manager.getAvailableTimeSlots(doctor.getId(), date);
        for (LocalTime t : slots) {
            timeComboModel.addElement(t);
        }

        if (timeComboModel.getSize() > 0) {
            timeCombo.setSelectedIndex(0);
        }
    }


    private void openDatePicker(JTextField targetField) {
        LocalDate base = LocalDate.now();
        String txt = targetField.getText().trim();
        if (!txt.isEmpty()) {
            try {
                base = LocalDate.parse(txt);
            } catch (Exception ignore) {
            }
        }
        DatePicker picker = new DatePicker(this, base);
        picker.setVisible(true);
        LocalDate picked = picker.getSelectedDate();
        if (picked != null) {
            targetField.setText(picked.toString());
        }
    }

    private void handleAddPatient() {
        String name = newPatientNameField.getText().trim();
        String contact = newPatientContactField.getText().trim();
        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter both name and contact to add a patient.",
                    "Missing data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Patient p = manager.addPatient(name, contact);
        refreshPatientCombo();
        patientCombo.setSelectedItem(p);
        newPatientNameField.setText("");
        newPatientContactField.setText("");
        JOptionPane.showMessageDialog(this,
                "Added patient " + p.getName() + " with ID " + p.getId() + ".",
                "Patient added", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleSchedule() {
        Patient patient = (Patient) patientCombo.getSelectedItem();
        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        String dateStr = dateField.getText().trim();
        LocalTime time = (LocalTime) timeCombo.getSelectedItem();

        if (patient == null || doctor == null || dateStr.isEmpty() || time == null) {
            JOptionPane.showMessageDialog(this,
                    "Select patient, doctor, date, and time slot first.",
                    "Missing data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date. Use the Select Date button.",
                    "Invalid date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Block past date/time at GUI level too
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime slot = LocalDateTime.of(date, time);
        if (slot.isBefore(now)) {
            JOptionPane.showMessageDialog(this,
                    "You cannot book an appointment in the past.",
                    "Past time", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = manager.scheduleAppointment(
                doctor.getId(), date, time, patient.getId());

        if (!ok) {
            JOptionPane.showMessageDialog(this,
                    "Could not schedule. Either the doctor is busy at that time or the time is in the past.",
                    "Schedule failed", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Appointment scheduled.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dateField.setText("");
            timeComboModel.removeAllElements();
        }
    }

    private void handleViewSchedule() {
        Doctor doctor = (Doctor) doctorCombo2.getSelectedItem();
        String dateStr = dateField2.getText().trim();

        if (doctor == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a doctor first.",
                    "Missing data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Appointment> list;

        if (dateStr.isEmpty()) {
            // No date chosen → show all appointments for this doctor
            list = manager.getAppointmentsForDoctor(doctor.getId());
        } else {
            // Date chosen → filter to that day
            LocalDate date;
            try {
                date = LocalDate.parse(dateStr);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date. Use the Select Date button.",
                        "Invalid date", JOptionPane.ERROR_MESSAGE);
                return;
            }
            list = manager.getAppointmentsFor(doctor.getId(), date);
        }

        apptListModel.clear();

        if (list.isEmpty()) {
            apptListModel.addElement("(No appointments)");
            cancelButton.setEnabled(false);
            apptList.putClientProperty("apptListData", null);
        } else {
            for (Appointment appt : list) {
                Patient p = manager.getPatient(appt.getPatientId());
                String patientName = (p != null ? p.getName() : appt.getPatientId());
                String line = appt.getDate().toString() + " " +
                        appt.getTime().toString() + " - " +
                        patientName + " (" + appt.getPatientId() + ")";
                apptListModel.addElement(line);
            }
            apptList.putClientProperty("apptListData", list);
            cancelButton.setEnabled(true);
        }
    }

    private void handleCancel() {
        int idx = apptList.getSelectedIndex();
        List<Appointment> list = (List<Appointment>) apptList.getClientProperty("apptListData");
        if (list == null || idx < 0 || idx >= list.size()) {
            return;
        }

        Appointment appt = list.get(idx);
        Doctor doc = manager.getDoctor(appt.getDoctorId());

        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel appointment with "
                        + (doc != null ? "Dr. " + doc.getName() : appt.getDoctorId())
                        + " on " + appt.getDate() + " at " + appt.getTime() + "?",
                "Confirm cancel", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            boolean removed = manager.cancelAppointment(appt.getId());
            if (removed) {
                JOptionPane.showMessageDialog(this, "Appointment canceled.",
                        "Canceled", JOptionPane.INFORMATION_MESSAGE);
                handleViewSchedule();
            } else {
                JOptionPane.showMessageDialog(this, "Could not cancel appointment.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
