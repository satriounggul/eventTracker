package eventtracker.model;

import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Division implements Progressable {
    private String name;
    private String headName;
    private List<Task> tasks;

    public Division(String name, String headName) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama divisi tidak boleh kosong.");
        }
        this.name = name.trim();
        this.headName = (headName != null) ? headName.trim() : "";
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = (headName != null) ? headName.trim() : "";
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

    public void addTask(Task t) {
        if (t == null) {
            return;
        }
        t.setDivisionName(this.name);
        tasks.add(t);
    }

    // Method Overloading: tambah tugas langsung dari data utama
    public void addTask(String title, Priority priority, LocalDate deadline) throws InvalidDataException {
        Task t = new Task(title, this.name, priority, deadline);
        addTask(t);
    }

    public void removeTask(Task t) throws DataNotFoundException {
        boolean removed = tasks.remove(t);
        if (!removed) {
            throw new DataNotFoundException("Tugas tidak ditemukan di divisi " + name + ".");
        }
    }

    // Method Overloading: hapus tugas berdasarkan indeks
    public void removeTask(int index) throws DataNotFoundException {
        if (index < 0 || index >= tasks.size()) {
            throw new DataNotFoundException("Indeks tugas tidak ditemukan di divisi " + name + ".");
        }
        tasks.remove(index);
    }

    public Task getTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Indeks tugas di luar rentang.");
        }
        return tasks.get(index);
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public int countDone() {
        int done = 0;
        for (Task t : tasks) {
            if (t.getStatus() == Status.SELESAI) {
                done++;
            }
        }
        return done;
    }

    public int countLate() {
        int late = 0;
        for (Task t : tasks) {
            if (t.isLate()) {
                late++;
            }
        }
        return late;
    }

    @Override
    public int getProgress() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return countDone() * 100 / tasks.size();
    }
}