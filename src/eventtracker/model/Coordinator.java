package eventtracker.model;

import eventtracker.exception.InvalidDataException;

// Peran "Koordinator" dengan hak akses penuh.
public class Coordinator extends Member {
    // Constructor inisialisasi data "Koordinator".
    public Coordinator(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    // "Koordinator" dapat mengubah seluruh divisi.
    @Override
    public boolean canEdit(String divisionName) {
        return true;
    }

    // "Koordinator" dapat mengelola data acara.
    @Override
    public boolean canManageEvent() {
        return true;
    }

    // Method getter mengambil nama peran.
    @Override
    public String getRoleLabel() {
        return "Koordinator";
    }
}