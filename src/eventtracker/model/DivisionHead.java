package eventtracker.model;

import eventtracker.exception.InvalidDataException;

// Peran "Ketua Divisi" dengan hak akses pada divisinya.
public class DivisionHead extends Member {
    // Constructor inisialisasi data "Ketua Divisi"
    public DivisionHead(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    // "Ketua Divisi" dapat mengubah divisinya saja.
    @Override
    public boolean canEdit(String divisionName) {
        if (divisionName == null) {
            return false;
        }
        return divisionName.equalsIgnoreCase(getDivisionName());
    }

    // "Ketua Divisi" tidak dapat mengelola data acara.
    @Override
    public boolean canManageEvent() {
        return false;
    }

    // Method getter mengambil nama peran.
    @Override
    public String getRoleLabel() {
        return "Ketua Divisi";
    }
}