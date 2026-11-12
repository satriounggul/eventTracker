package eventtracker.model;

import java.time.LocalDate;
import eventtracker.exception.InvalidDataException;

public class Task implements Progressable {
    private String title;
    private String description;
    private String divisionName;
    private String notes;
    private String resultLink;
    private String[] pics;
    private boolean allPics;
    private boolean qualityChecked;
    private Priority priority;
    private Status status;
    private LocalDate deadline;

    public Task(String title, String divisionName, Priority priority, LocalDate deadline) throws InvalidDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Judul tugas tidak boleh kosong.");
        }
        if (deadline == null) {
            throw new InvalidDataException("Deadline tugas tidak boleh kosong.");
        }
        this.title = title.trim();
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
        this.priority = (priority != null) ? priority : Priority.SEDANG;
        this.deadline = deadline;
        this.status = Status.BELUM;
        this.pics = new String[0];
        this.allPics = false;
        this.qualityChecked = false;
        this.description = "";
        this.notes = "";
        this.resultLink = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Judul tugas tidak boleh kosong.");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    public String[] getPics() {
        if (pics == null) {
            return new String[0];
        }
        String[] copy = new String[pics.length];
        for (int i = 0; i < pics.length; i++) {
            copy[i] = pics[i];
        }
        return copy;
    }

    public void setPics(String[] newPics) {
        if (newPics == null) {
            this.pics = new String[0];
        } else {
            this.pics = new String[newPics.length];
            for (int i = 0; i < newPics.length; i++) {
                this.pics[i] = newPics[i];
            }
        }
    }

    public boolean isAllPics() {
        return allPics;
    }

    public void setAllPics(boolean allPics) {
        this.allPics = allPics;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        if (priority != null) {
            this.priority = priority;
        }
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) throws InvalidDataException {
        if (deadline == null) {
            throw new InvalidDataException("Deadline tugas tidak boleh kosong.");
        }
        this.deadline = deadline;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes : "";
    }

    public String getResultLink() {
        return resultLink;
    }

    public void setResultLink(String resultLink) {
        this.resultLink = (resultLink != null) ? resultLink : "";
    }

    public boolean isQualityChecked() {
        return qualityChecked;
    }

    public void setQualityChecked(boolean qualityChecked) {
        this.qualityChecked = qualityChecked;
    }

    public boolean isLate() {
        LocalDate today = LocalDate.now();
        return deadline.isBefore(today) && status != Status.SELESAI;
    }

    public String getStatusLabel() {
        if (isLate()) {
            return "Terlambat";
        }
        return status.getLabel();
    }

    public String getPicText() {
        if (allPics) {
            return "Semua anggota divisi";
        }
        if (pics == null || pics.length == 0) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pics.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(pics[i]);
        }
        return sb.toString();
    }

    public int getProgress() {
        return (status == Status.SELESAI) ? 100 : 0;
    }
}
