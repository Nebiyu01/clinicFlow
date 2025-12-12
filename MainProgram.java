import javax.swing.SwingUtilities;

/**
 * ==========================
 * How to Run ClinicFlow
 * ==========================
 *
 * Default login:
 * - Open the file staff.txt in the project folder.
 * - it contains one of the accounts:
 *        admin;password
 * - Use that username(admin) and password(password) on the login screen to log in.
 *
 * After logging in, you can use the Schedule Appointment and View / Cancel tabs
 * to test the system.
 */

public class MainProgram {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppointmentManager manager = new AppointmentManager();
            SchedulerGUI gui = new SchedulerGUI(manager);
            gui.setTitle("ClinicFlow - Appointment Scheduler");
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
