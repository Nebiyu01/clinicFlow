import java.io.Serializable;

public class Patient implements Serializable {
    private String id;
    private String name;
    private String contact;

    public Patient(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    @Override
    public String toString() {
        // What the user sees in the dropdown
        return name + " (" + id + ")";
    }
}
