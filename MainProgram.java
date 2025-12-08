import javax.swing.SwingUtilities;

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
