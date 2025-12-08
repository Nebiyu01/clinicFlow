import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentManagerTest {
    public static void main(String[] args) {
        AppointmentManager manager = new AppointmentManager();

        System.out.println("Login admin/password: " + manager.login("admin", "password"));
        System.out.println("Login admin/wrong: " + manager.login("admin", "wrong"));

        // Use an existing patient or add one
        List<Patient> pats = manager.getAllPatients();
        Patient p;
        if (pats.isEmpty()) {
            p = manager.addPatient("Test Patient", "555-0000");
        } else {
            p = pats.get(0);
        }
        System.out.println("Using patient: " + p);

        // Use an existing doctor
        List<Doctor> docs = manager.getAllDoctors();
        if (docs.isEmpty()) {
            System.out.println("No doctors set up.");
            return;
        }
        Doctor d = docs.get(0);
        System.out.println("Using doctor: " + d);

        LocalDate date = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(10, 0);

        boolean first = manager.scheduleAppointment(d.getId(), date, time, p.getId());
        System.out.println("First schedule (should be true): " + first);

        boolean conflict = manager.scheduleAppointment(d.getId(), date, time, p.getId());
        System.out.println("Second schedule same slot (should be false): " + conflict);

        List<Appointment> list = manager.getAppointmentsFor(d.getId(), date);
        System.out.println("Appointments on " + date + ": " + list);

        if (!list.isEmpty()) {
            int id = list.get(0).getId();
            boolean canceled = manager.cancelAppointment(id);
            System.out.println("Canceled appointment " + id + ": " + canceled);
        }
    }
}
