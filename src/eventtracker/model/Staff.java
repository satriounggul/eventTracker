package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public class Staff extends Member {
    public Staff(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    public boolean canEdit(String divisionName) {
        return false;
    }

    public boolean canManageEvent() {
        return false;
    }

    public String getRoleLabel() {
        return "Anggota";
    }
}
