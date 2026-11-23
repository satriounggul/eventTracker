package eventtracker.model;

import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public class Task implements Progressable {
    private String title;
    private String description;
    private String divisionName;
    private String notes;
    private String resultLink;
    private Set<String> pics;
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
        this.pics = new TreeSet<>();
        this.allPics = false;
        this.qualityChecked = false;
        this.description = "";
        this.notes = "";
        this.resultLink = "";
    }

    // Overloaded constructor (default prioritas SEDANG)
    public Task(String title, String divisionName, LocalDate deadline) throws InvalidDataException {
        this(title, divisionName, Priority.SEDANG, deadline);
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

    public Set<String> getPics() {
        return new TreeSet<>(pics);
    }

    public void setPics(Set<String> newPics) {
        this.pics = new TreeSet<>();
        if (newPics != null) {
            for (String p : newPics) {
                if (p != null && !p.trim().isEmpty()) {
                    this.pics.add(p.trim());
                }
            }
        }
    }

    // Overloaded setter untuk menerima array String
    public void setPics(String[] newPics) {
        Set<String> picSet = new TreeSet<>();
        if (newPics != null) {
            for (int i = 0; i < newPics.length; i++) {
                if (newPics[i] != null && !newPics[i].trim().isEmpty()) {
                    picSet.add(newPics[i].trim());
                }
            }
        }
        this.pics = picSet;
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
        if (pics == null || pics.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String pic : pics) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(pic);
            first = false;
        }
        return sb.toString();
    }

    @Override
    public int getProgress() {
        return (status == Status.SELESAI) ? 100 : 0;
    }
}