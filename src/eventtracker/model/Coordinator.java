package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public class Coordinator extends Member {
    public Coordinator(String name, Position position, String divisionName) throws InvalidDataException {
        super(name, position, divisionName);
    }

    @Override
    public boolean canEdit(String divisionName) {
        return true;
    }

    @Override
    public boolean canManageEvent() {
        return true;
    }

    @Override
    public String getRoleLabel() {
        return "Koordinator";
    }
}