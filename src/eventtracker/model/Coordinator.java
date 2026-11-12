package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public class Coordinator extends Member {
    public Coordinator(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    public boolean canEdit(String divisionName) {
        return true;
    }

    public boolean canManageEvent() {
        return true;
    }

    public String getRoleLabel() {
        return "Koordinator";
    }
}
