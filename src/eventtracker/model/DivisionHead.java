package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public class DivisionHead extends Member {
    public DivisionHead(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    public boolean canEdit(String divisionName) {
        if (divisionName == null) {
            return false;
        }
        return divisionName.equalsIgnoreCase(getDivisionName());
    }

    public boolean canManageEvent() {
        return false;
    }

    public String getRoleLabel() {
        return "Ketua Divisi";
    }
}
