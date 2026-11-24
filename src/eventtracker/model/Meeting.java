package eventtracker.model;

import java.time.LocalDate;
import eventtracker.exception.InvalidDataException;

// Rapat beserta catatan notulensi dan hasil keputusan.
public class Meeting {
    private LocalDate date;
    private String divisionName;
    private String content;
    private String decision;

    // Constructor inisialisasi rapat baru.
    public Meeting(LocalDate date, String divisionName, String content, String decision) throws InvalidDataException {
        // Validasi tanggal dan notulensi rapat.
        if (date == null) {
            throw new InvalidDataException("Tanggal rapat tidak boleh kosong.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidDataException("Isi rapat tidak boleh kosong.");
        }
        this.date = date;
        this.content = content.trim();
        
        // Nama divisi default "Semua".
        this.divisionName = (divisionName != null && !divisionName.trim().isEmpty()) ? divisionName.trim() : "Semua";

        // Keputusan rapat boleh kosong.
        this.decision = (decision != null) ? decision.trim() : "";
    }

    // Method getter mengambil tanggal rapat.
    public LocalDate getDate() {
        return date;
    }

    // Method getter mengambil nama divisi pada rapat (Default : Semua).
    public String getDivisionName() {
        return divisionName;
    }

    // Method getter mengambil notulensi rapat.
    public String getContent() {
        return content;
    }

    // Method getter mengambil hasil keputusan rapat.
    public String getDecision() {
        return decision;
    }
}