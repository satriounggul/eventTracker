package eventtracker.model;

import java.time.LocalDate;
import eventtracker.exception.InvalidDataException;

public class Meeting {
    private LocalDate date;
    private String divisionName;
    private String content;
    private String decision;

    public Meeting(LocalDate date, String divisionName, String content, String decision) throws InvalidDataException {
        if (date == null) {
            throw new InvalidDataException("Tanggal rapat tidak boleh kosong.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidDataException("Isi rapat tidak boleh kosong.");
        }
        this.date = date;
        this.divisionName = (divisionName != null && !divisionName.trim().isEmpty()) ? divisionName.trim() : "Semua";
        this.content = content.trim();
        this.decision = (decision != null) ? decision.trim() : "";
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getContent() {
        return content;
    }

    public String getDecision() {
        return decision;
    }
}
