package eventtracker.model;

import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Divisi beserta daftar tugas dan progressnya.
public class Division implements Progressable {
    private String name;
    private String headName;
    private List<Task> tasks;

    // Constructor inisialisasi divisi baru.
    public Division(String name, String headName) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama divisi tidak boleh kosong.");
        }
        this.name = name.trim();
        this.headName = (headName != null) ? headName.trim() : "";
        this.tasks = new ArrayList<>();
    }

    // Method getter dan setter nama divisi.
    public String getName() {
        return name;
    }

    public void setName(String newName) throws InvalidDataException {
        if (newName == null || newName.trim().isEmpty()) {
            throw new InvalidDataException("Nama divisi tidak boleh kosong.");
        }
        this.name = newName.trim();
        for (Task t : tasks) {
            t.setDivisionName(this.name);
        }
    }

    // Method getter dan setter nama ketua divisi.
    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = (headName != null) ? headName.trim() : "";
    }

    // Method untuk menugaskan tugas ke dalam divisi yang dipilih.
    public void addTask(Task t) {
        if (t == null) {
            return;
        }
        t.setDivisionName(this.name);
        tasks.add(t);
    }

    // Method Overloading (addTask).
    // Menambahkan tugas baru dan menugaskan ke dalam divisi yang dipilih.
    public void addTask(String title, Priority priority, LocalDate deadline) throws InvalidDataException {
        Task t = new Task(title, this.name, priority, deadline);
        addTask(t);
    }

    // Menghapus tugas berdasarkan objek.
    public void removeTask(Task t) throws DataNotFoundException {
        boolean removed = tasks.remove(t);
        if (!removed) {
            throw new DataNotFoundException("Tugas tidak ditemukan di divisi " + name + ".");
        }
    }

    // Method Overloading (removeTask).
    // Menghapus tugas berdasarkan indeks.
    public void removeTask(int index) throws DataNotFoundException {
        if (index < 0 || index >= tasks.size()) {
            throw new DataNotFoundException("Indeks tugas tidak ditemukan di divisi " + name + ".");
        }
        tasks.remove(index);
    }

    // Method mengambil tugas tertentu berdasarkan indeks.
    public Task getTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Indeks tugas di luar rentang.");
        }
        return tasks.get(index);
    }

    // Method mengambil daftar tugas divisi berbentuk salinan.
    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    // Method menghitung total tugas divisi.
    public int getTaskCount() {
        return tasks.size();
    }

    // Method menghitung total tugas divisi dengan status "SELESAI".
    public int countDone() {
        int done = 0;
        for (Task t : tasks) {
            if (t.getStatus() == Status.SELESAI) {
                done++;
            }
        }
        return done;
    }

    // Method menghitung total tugas divisi yang melebihi batas waktu.
    public int countLate() {
        int late = 0;
        for (Task t : tasks) {
            if (t.isLate()) {
                late++;
            }
        }
        return late;
    }

    // Method menghitung persentase tugas yang sudah selesai.
    @Override
    public int getProgress() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return countDone() * 100 / tasks.size();
    }
}