package eventtracker.model;

// Inisialisasi enum peran dan label untuk penulisan.
public enum Position {
    KETUA("Ketua"),
    WAKIL("Wakil"),
    SEKRETARIS("Sekretaris"),
    BENDAHARA("Bendahara"),
    KETUA_DIVISI("Ketua Divisi"),
    ANGGOTA("Anggota");

    private final String label;

    // Constructor inisialisasi label untuk penulisan.
    Position(String label) {
        this.label = label;
    }

    // Method getter mengambil label peran.
    public String getLabel() {
        return label;
    }
}
