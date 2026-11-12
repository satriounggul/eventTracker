package eventtracker.model;

import eventtracker.exception.InvalidDataException;

public abstract class Member {
    private String name;
    private Position position;
    private String divisionName;

    public Member(String name, Position position, String divisionName) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama anggota tidak boleh kosong.");
        }
        if (position == null) {
            throw new InvalidDataException("Jabatan anggota tidak boleh kosong.");
        }
        this.name = name.trim();
        this.position = position;
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    public abstract boolean canEdit(String divisionName);

    public abstract boolean canManageEvent();

    public abstract String getRoleLabel();

    public static Member create(String name, Position position, String divisionName) throws InvalidDataException {
        if (position == null) {
            throw new InvalidDataException("Jabatan tidak valid.");
        }
        if (position == Position.KETUA || position == Position.WAKIL ||
            position == Position.SEKRETARIS || position == Position.BENDAHARA) {
            return new Coordinator(name, position, divisionName);
        } else if (position == Position.KETUA_DIVISI) {
            return new DivisionHead(name, position, divisionName);
        } else if (position == Position.ANGGOTA) {
            return new Staff(name, position, divisionName);
        } else {
            throw new InvalidDataException("Jabatan tidak dikenali: " + position);
        }
    }

    public String toString() {
        return name + " (" + position.getLabel() + ")";
    }
}
