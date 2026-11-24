package eventtracker.model;

import eventtracker.exception.InvalidDataException;

// Anggota beserta identitas dan hak aksesnya (Abstract).
public abstract class Member {
    private String name;
    private Position position;
    private String divisionName;

    // Constructor inisialisasi anggota baru.
    public Member(String name, Position position, String divisionName) throws InvalidDataException {
        // Validasi nama dan peran anggota
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama anggota tidak boleh kosong.");
        }
        if (position == null) {
            throw new InvalidDataException("Jabatan anggota tidak boleh kosong.");
        }
        this.name = name.trim();
        this.position = position;
        
        // Nama divisi bisa kosong dulu.
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    // Method getter mengambil nama anggota.
    public String getName() {
        return name;
    }

    // Method getter mengambil peran anggota.
    public Position getPosition() {
        return position;
    }

    // Method getter dan setter nama divisi anggota.
    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    // Method abstract mengatur hak akses anggota untuk edit pada divisi tertentu.
    public abstract boolean canEdit(String divisionName);

    // Method abstract mengatur hak akses anggota untuk mengelola data acara.
    public abstract boolean canManageEvent();

    // Method abstract getter mengambil label peran anggota.
    public abstract String getRoleLabel();

    // Method membuat objek subclass Member sesuai dengan peran anggota.
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

    // Method menampilkan format nama dan jabatan anggota.
    @Override
    public String toString() {
        return name + " (" + position.getLabel() + ")";
    }
}