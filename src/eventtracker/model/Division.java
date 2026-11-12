package eventtracker.model;

import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;

public class Division implements Progressable {
    private String name;
    private String headName;
    private Task[] tasks;
    private int taskCount;

    public Division(String name, String headName) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama divisi tidak boleh kosong.");
        }
        this.name = name.trim();
        this.headName = (headName != null) ? headName.trim() : "";
        this.tasks = new Task[10];
        this.taskCount = 0;
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
        for (int i = 0; i < taskCount; i++) {
            tasks[i].setDivisionName(this.name);
        }
    }

    public void addTask(Task t) {
        t.setDivisionName(this.name);
        if (taskCount == tasks.length) {
            Task[] newTasks = new Task[tasks.length * 2];
            for (int i = 0; i < tasks.length; i++) {
                newTasks[i] = tasks[i];
            }
            tasks = newTasks;
        }
        tasks[taskCount++] = t;
    }

    public void removeTask(Task t) throws DataNotFoundException {
        int index = -1;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i] == t) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new DataNotFoundException("Tugas tidak ditemukan di divisi " + name + ".");
        }
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        taskCount--;
        tasks[taskCount] = null;
    }

    public Task getTask(int index) {
        if (index < 0 || index >= taskCount) {
            throw new IndexOutOfBoundsException("Indeks tugas di luar rentang.");
        }
        return tasks[index];
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int countDone() {
        int done = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].getStatus() == Status.SELESAI) {
                done++;
            }
        }
        return done;
    }

    public int countLate() {
        int late = 0;
        for (int i = 0; i < taskCount; i++) {
            if (tasks[i].isLate()) {
                late++;
            }
        }
        return late;
    }

    public int getProgress() {
        if (taskCount == 0) {
            return 0;
        }
        int progress = countDone() * 100 / taskCount;
        assert progress >= 0 && progress <= 100 : "Progres divisi harus antara 0 dan 100";
        return progress;
    }
}
