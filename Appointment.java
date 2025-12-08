import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment implements Serializable {
    private int id;
    private String doctorId;
    private LocalDate date;
    private LocalTime time;
    private String patientId;

    public Appointment(int id, String doctorId, LocalDate date, LocalTime time, String patientId) {
        this.id = id;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.patientId = patientId;
    }

    public int getId() {
        return id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getPatientId() {
        return patientId;
    }

    @Override
    public String toString() {
        return date.toString() + " " + time.toString()
                + " [Doctor " + doctorId + ", Patient " + patientId + "]";
    }
}
