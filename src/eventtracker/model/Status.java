package eventtracker.model;

// Inisialisasi enum status dan label untuk penulisan.
public enum Status {
    BELUM("Belum"),
    PROSES("Proses"),
    SELESAI("Selesai");

    private final String label;

    // Constructor inisialisasi label untuk penulisan.
    Status(String label) {
        this.label = label;
    }
    
    // Method getter mengambil label status.
    public String getLabel() {
        return label;
    }
}
