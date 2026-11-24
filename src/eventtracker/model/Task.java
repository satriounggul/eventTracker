package eventtracker.model;

import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

// Tugas beserta penanggung jawab, prioritas, status, dan batas waktu.
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

    // Constructor inisialisasi tugas baru.
    public Task(String title, String divisionName, Priority priority, LocalDate deadline) throws InvalidDataException {
        // Validasi judul dan batas waktu tugas.
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Judul tugas tidak boleh kosong.");
        }
        if (deadline == null) {
            throw new InvalidDataException("Deadline tugas tidak boleh kosong.");
        }
        this.title = title.trim();
        this.deadline = deadline;

        // Mengatur nilai default untuk atribut lain.
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
        this.priority = (priority != null) ? priority : Priority.SEDANG;
        this.status = Status.BELUM;
        this.pics = new TreeSet<>();
        this.allPics = false;
        this.qualityChecked = false;
        this.description = "";
        this.notes = "";
        this.resultLink = "";
    }
    
    // Constructor Overloading (Task).
    // Menambahkan tugas baru dengan prioritas default : SEDANG.
    public Task(String title, String divisionName, LocalDate deadline) throws InvalidDataException {
        this(title, divisionName, Priority.SEDANG, deadline);
    }

    // Method getter dan setter judul tugas.
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidDataException {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("Judul tugas tidak boleh kosong.");
        }
        this.title = title.trim();
    }

    // Method getter dan setter deskripsi tugas.
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    // Method getter dan setter nama divisi untuk tugas.
    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = (divisionName != null) ? divisionName.trim() : "";
    }

    // Method getter penanggung jawab tugas berupa salinan.
    public Set<String> getPics() {
        return new TreeSet<>(pics);
    }

    // Method setter PIC dengan menerima koleksi.
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

    // Setter Overloading (setPics).
    // Mengatur PIC dengan menerima array String.
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

    // Method pengecekan apakah PIC tugas adalah semua anggota.
    public boolean isAllPics() {
        return allPics;
    }

    // Method set PIC tugas adalah semua anggota.
    public void setAllPics(boolean allPics) {
        this.allPics = allPics;
    }

    // Method getter dan setter prioritas tugas.
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        if (priority != null) {
            this.priority = priority;
        }
    }

    // Method getter dan setter batas waktu tugas.
    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) throws InvalidDataException {
        if (deadline == null) {
            throw new InvalidDataException("Deadline tugas tidak boleh kosong.");
        }
        this.deadline = deadline;
    }

    // Method getter dan setter status tugas.
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }

    // Method getter dan setter catatan tambahan tugas.
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = (notes != null) ? notes : "";
    }

    // Method getter dan setter tautan hasil pengerjaan tugas.
    public String getResultLink() {
        return resultLink;
    }

    public void setResultLink(String resultLink) {
        this.resultLink = (resultLink != null) ? resultLink : "";
    }

    // Method getter dan setter pengecekan hasil pengerjaan tugas.
    public boolean isQualityChecked() {
        return qualityChecked;
    }

    public void setQualityChecked(boolean qualityChecked) {
        this.qualityChecked = qualityChecked;
    }

    // Method pengecekan apakah tugas terlambat.
    public boolean isLate() {
        LocalDate today = LocalDate.now();
        return deadline.isBefore(today) && status != Status.SELESAI;
    }

    // Method getter mengambil hasil pengecekan apakah tugas terlambat.
    public String getStatusLabel() {
        if (isLate()) {
            return "Terlambat";
        }
        return status.getLabel();
    }

    // Method menyusun daftar penanggung jawab tugas.
    // Mengunakan StringBuilder
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

    // Method menghitung persentase progress tugas.
    @Override
    public int getProgress() {
        return (status == Status.SELESAI) ? 100 : 0;
    }
}