package eventtracker.model;

public enum Status {
    BELUM("Belum"),
    PROSES("Proses"),
    SELESAI("Selesai");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
