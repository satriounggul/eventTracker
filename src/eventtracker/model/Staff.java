package eventtracker.model;

import eventtracker.exception.InvalidDataException;

// Anggota biasa tanpa hak akses ubah data.
public class Staff extends Member {
    // Constructor inisialisasi data "Anggota".
    public Staff(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    // "Anggota" dapat mengubah seluruh divisi.
    public boolean canEdit(String divisionName) {
        return false;
    }

    // "Anggota" tidak dapat mengelola data acara.
    public boolean canManageEvent() {
        return false;
    }

    // Method getter mengambil nama peran.
    public String getRoleLabel() {
        return "Anggota";
    }
}
