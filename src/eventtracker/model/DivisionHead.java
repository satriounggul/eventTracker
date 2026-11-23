package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public class DivisionHead extends Member {
    public DivisionHead(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    @Override
    public boolean canEdit(String divisionName) {
        if (divisionName == null) {
            return false;
        }
        return divisionName.equalsIgnoreCase(getDivisionName());
    }

    @Override
    public boolean canManageEvent() {
        return false;
    }

    @Override
    public String getRoleLabel() {
        return "Ketua Divisi";
    }
}