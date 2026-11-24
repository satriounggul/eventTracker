package eventtracker.model;

// Inisialisasi enum prioritas dan label untuk penulisan.
public enum Priority {
    RENDAH("Rendah"),
    SEDANG("Sedang"),
    TINGGI("Tinggi");

    private final String label;

    // Constructor inisialisasi label untuk penulisan.
    Priority(String label) {
        this.label = label;
    }
    
    // Method getter mengambil label prioritas.
    public String getLabel() {
        return label;
    }
}
