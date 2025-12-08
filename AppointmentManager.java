import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AppointmentManager {

    private List<Appointment> appointments = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Doctor> doctors = new ArrayList<>();
    private Map<String, String> staffCredentials = new HashMap<>();

    private final String STAFF_FILE = "staff.txt";
    private final String PATIENT_FILE = "patients.txt";
    private final String DOCTOR_FILE = "doctors.txt";
    private final String APPOINTMENT_FILE = "appointments.txt";

    private final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public AppointmentManager() {
        loadStaff();
        loadPatients();
        loadDoctors();
        loadAppointments();
    }

    // ---------- Auth ----------

    public boolean login(String username, String password) {
        String stored = staffCredentials.get(username);
        return stored != null && stored.equals(password);
    }

    // ---------- Patient management ----------

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    public Patient getPatient(String patientId) {
        for (Patient p : patients) {
            if (p.getId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    public Patient addPatient(String name, String contact) {
        // Simple auto ID: P<number>
        int max = 99;
        for (Patient p : patients) {
            String id = p.getId();
            if (id != null && id.startsWith("P")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) {
                        max = n;
                    }
                } catch (NumberFormatException ignore) {
                }
            }
        }
        String newId = "P" + (max + 1);
        Patient p = new Patient(newId, name, contact);
        patients.add(p);
        savePatients();
        return p;
    }

    // ---------- Doctor management ----------

    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    public Doctor getDoctor(String doctorId) {
        for (Doctor d : doctors) {
            if (d.getId().equals(doctorId)) {
                return d;
            }
        }
        return null;
    }

    // ---------- Appointments ----------

    public boolean scheduleAppointment(String doctorId, LocalDate date,
                                       LocalTime time, String patientId) {

        Patient patient = getPatient(patientId);
        if (patient == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime requested = LocalDateTime.of(date, time);

        // Block any time in the past
        if (requested.isBefore(now)) {
            return false;
        }

        // Check doctor availability
        for (Appointment appt : appointments) {
            if (appt.getDoctorId().equals(doctorId)
                    && appt.getDate().equals(date)
                    && appt.getTime().equals(time)) {
                return false; // conflict
            }
        }

        int newId = 1;
        for (Appointment appt : appointments) {
            if (appt.getId() >= newId) {
                newId = appt.getId() + 1;
            }
        }

        Appointment newAppt = new Appointment(newId, doctorId, date, time, patientId);
        appointments.add(newAppt);
        saveAppointments();
        return true;
    }

    public List<Appointment> getAppointmentsFor(String doctorId, LocalDate date) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            if (appt.getDoctorId().equals(doctorId)
                    && appt.getDate().equals(date)) {
                result.add(appt);
            }
        }
        result.sort(Comparator.comparing(Appointment::getTime));
        return result;
    }

    public boolean cancelAppointment(int appointmentId) {
        Iterator<Appointment> it = appointments.iterator();
        boolean removed = false;
        while (it.hasNext()) {
            Appointment appt = it.next();
            if (appt.getId() == appointmentId) {
                it.remove();
                removed = true;
                break;
            }
        }
        if (removed) {
            saveAppointments();
        }
        return removed;
    }

    // ---------- Load / save staff ----------

    private void loadStaff() {
        File file = new File(STAFF_FILE);
        if (!file.exists()) {
            staffCredentials.put("admin", "password");
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("admin;password");
            } catch (IOException e) {
                System.err.println("Could not create staff file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 2);
                if (parts.length == 2) {
                    staffCredentials.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading staff file: " + e.getMessage());
        }
    }

    // ---------- Load / save patients ----------

    private void loadPatients() {
        File file = new File(PATIENT_FILE);
        if (!file.exists()) {
            patients.add(new Patient("P100", "John Doe", "555-1234"));
            patients.add(new Patient("P101", "Jane Smith", "555-9876"));
            savePatients();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    patients.add(new Patient(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading patients file: " + e.getMessage());
        }
    }

    private void savePatients() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PATIENT_FILE))) {
            for (Patient p : patients) {
                pw.println(p.getId() + ";" + p.getName() + ";" + p.getContact());
            }
        } catch (IOException e) {
            System.err.println("Error writing patients file: " + e.getMessage());
        }
    }

    // ---------- Load / save doctors ----------

    private void loadDoctors() {
        File file = new File(DOCTOR_FILE);
        if (!file.exists()) {
            doctors.add(new Doctor("D100", "Smith", "General"));
            doctors.add(new Doctor("D101", "Jones", "Cardiology"));
            doctors.add(new Doctor("D102", "Lee", "Pediatrics"));
            saveDoctors();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";", 3);
                if (parts.length >= 2) {
                    String id = parts[0];
                    String name = parts[1];
                    String spec = parts.length == 3 ? parts[2] : "";
                    doctors.add(new Doctor(id, name, spec));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading doctors file: " + e.getMessage());
        }
    }

    private void saveDoctors() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DOCTOR_FILE))) {
            for (Doctor d : doctors) {
                pw.println(d.getId() + ";" + d.getName() + ";" + d.getSpecialty());
            }
        } catch (IOException e) {
            System.err.println("Error writing doctors file: " + e.getMessage());
        }
    }

    // ---------- Load / save appointments ----------

    private void loadAppointments() {
        File file = new File(APPOINTMENT_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // id;patientId;doctorId;date;time
                String[] parts = line.split(";", 5);
                if (parts.length == 5) {
                    int id = Integer.parseInt(parts[0]);
                    String patientId = parts[1];
                    String doctorId = parts[2];
                    LocalDate date = LocalDate.parse(parts[3], DATE_FMT);
                    LocalTime time = LocalTime.parse(parts[4], TIME_FMT);
                    appointments.add(new Appointment(id, doctorId, date, time, patientId));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments file: " + e.getMessage());
        }
    }

    private void saveAppointments() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(APPOINTMENT_FILE))) {
            for (Appointment appt : appointments) {
                String dateStr = appt.getDate().format(DATE_FMT);
                String timeStr = appt.getTime().format(TIME_FMT);
                pw.println(appt.getId() + ";" + appt.getPatientId() + ";"
                        + appt.getDoctorId() + ";" + dateStr + ";" + timeStr);
            }
        } catch (IOException e) {
            System.err.println("Error writing appointments file: " + e.getMessage());
        }
    }
}
