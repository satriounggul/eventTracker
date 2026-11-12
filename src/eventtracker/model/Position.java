package eventtracker.model;

public enum Position {
    KETUA("Ketua"),
    WAKIL("Wakil"),
    SEKRETARIS("Sekretaris"),
    BENDAHARA("Bendahara"),
    KETUA_DIVISI("Ketua Divisi"),
    ANGGOTA("Anggota");

    private final String label;

    Position(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
