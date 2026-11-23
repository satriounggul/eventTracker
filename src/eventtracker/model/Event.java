package eventtracker.model;

import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event implements Progressable {
    private String name;
    private LocalDate date;
    private List<Division> divisions;
    private List<Member> members;
    private List<Meeting> meetings;

    public Event(String name, LocalDate date) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama event tidak boleh kosong.");
        }
        if (date == null) {
            throw new InvalidDataException("Tanggal event tidak boleh kosong.");
        }
        this.name = name.trim();
        this.date = date;
        this.divisions = new ArrayList<>();
        this.members = new ArrayList<>();
        this.meetings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama event tidak boleh kosong.");
        }
        this.name = name.trim();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) throws InvalidDataException {
        if (date == null) {
            throw new InvalidDataException("Tanggal event tidak boleh kosong.");
        }
        this.date = date;
    }

    public void addDivision(Division d) throws InvalidDataException {
        if (d == null) {
            throw new InvalidDataException("Divisi tidak boleh null.");
        }
        for (Division existing : divisions) {
            if (existing.getName().equalsIgnoreCase(d.getName())) {
                throw new InvalidDataException("Divisi dengan nama \"" + d.getName() + "\" sudah ada.");
            }
        }
        divisions.add(d);
    }

    public void removeDivision(Division d) throws DataNotFoundException {
        boolean removed = divisions.remove(d);
        if (!removed) {
            throw new DataNotFoundException("Divisi tidak ditemukan.");
        }
        // Kosongkan divisi pada anggota yang berada di divisi ini
        for (Member m : members) {
            if (m.getDivisionName().equalsIgnoreCase(d.getName())) {
                m.setDivisionName("");
            }
        }
    }

    public Division getDivision(int i) {
        if (i < 0 || i >= divisions.size()) {
            throw new IndexOutOfBoundsException("Indeks divisi di luar rentang.");
        }
        return divisions.get(i);
    }

    public int getDivisionCount() {
        return divisions.size();
    }

    public Division findDivision(String name) throws DataNotFoundException {
        if (name != null) {
            for (Division d : divisions) {
                if (d.getName().equalsIgnoreCase(name.trim())) {
                    return d;
                }
            }
        }
        throw new DataNotFoundException("Divisi \"" + name + "\" tidak ditemukan.");
    }

    public void addMember(Member m) {
        if (m == null) {
            return;
        }
        members.add(m);
    }

    public void removeMember(Member m) throws DataNotFoundException {
        boolean removed = members.remove(m);
        if (!removed) {
            throw new DataNotFoundException("Anggota tidak ditemukan.");
        }
    }

    public Member getMember(int i) {
        if (i < 0 || i >= members.size()) {
            throw new IndexOutOfBoundsException("Indeks anggota di luar rentang.");
        }
        return members.get(i);
    }

    public int getMemberCount() {
        return members.size();
    }

    public void addMeeting(Meeting m) {
        if (m == null) {
            return;
        }
        meetings.add(m);
    }

    public void removeMeeting(Meeting m) throws DataNotFoundException {
        boolean removed = meetings.remove(m);
        if (!removed) {
            throw new DataNotFoundException("Notulensi rapat tidak ditemukan.");
        }
    }

    public Meeting getMeeting(int i) {
        if (i < 0 || i >= meetings.size()) {
            throw new IndexOutOfBoundsException("Indeks notulensi di luar rentang.");
        }
        return meetings.get(i);
    }

    public int getMeetingCount() {
        return meetings.size();
    }

    public int getTotalTasks() {
        int total = 0;
        for (Division d : divisions) {
            total += d.getTaskCount();
        }
        return total;
    }

    public int getTotalDone() {
        int done = 0;
        for (Division d : divisions) {
            done += d.countDone();
        }
        return done;
    }

    public int getTotalLate() {
        int late = 0;
        for (Division d : divisions) {
            late += d.countLate();
        }
        return late;
    }

    @Override
    public int getProgress() {
        int total = getTotalTasks();
        if (total == 0) {
            return 0;
        }
        return getTotalDone() * 100 / total;
    }

    // Memanfaatkan Collections.max() untuk progres divisi tertinggi
    public int getHighestDivisionProgress() {
        if (divisions.isEmpty()) {
            return 0;
        }
        List<Integer> progressList = new ArrayList<>();
        for (Division d : divisions) {
            progressList.add(d.getProgress());
        }
        return Collections.max(progressList);
    }

    // Memanfaatkan Collections.min() untuk progres divisi terendah
    public int getLowestDivisionProgress() {
        if (divisions.isEmpty()) {
            return 0;
        }
        List<Integer> progressList = new ArrayList<>();
        for (Division d : divisions) {
            progressList.add(d.getProgress());
        }
        return Collections.min(progressList);
    }

    // Memanfaatkan HashMap untuk ringkasan jumlah tugas per status
    public Map<String, Integer> getStatusSummary() {
        Map<String, Integer> summary = new HashMap<>();
        summary.put("Belum", 0);
        summary.put("Proses", 0);
        summary.put("Selesai", 0);
        summary.put("Terlambat", 0);

        for (Division div : divisions) {
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                String label = t.getStatusLabel();
                summary.put(label, summary.get(label) + 1);
            }
        }
        return summary;
    }

    public List<Task> getLateTasks() {
        List<Task> lateTasks = new ArrayList<>();
        for (Division div : divisions) {
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                if (t.isLate()) {
                    lateTasks.add(t);
                }
            }
        }
        return lateTasks;
    }

    // Method Overloading: default 5 tugas terdekat
    public List<Task> getUpcomingTasks() {
        return getUpcomingTasks(5);
    }

    public List<Task> getUpcomingTasks(int limit) {
        List<Task> result = new ArrayList<>();
        if (limit <= 0) {
            return result;
        }
        LocalDate today = LocalDate.now();
        List<Task> upcoming = new ArrayList<>();
        for (Division div : divisions) {
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                if (t.getStatus() != Status.SELESAI && !t.getDeadline().isBefore(today)) {
                    upcoming.add(t);
                }
            }
        }

        // Urutkan berdasarkan deadline terdekat
        int count = upcoming.size();
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (upcoming.get(j).getDeadline().isAfter(upcoming.get(j + 1).getDeadline())) {
                    Task temp = upcoming.get(j);
                    upcoming.set(j, upcoming.get(j + 1));
                    upcoming.set(j + 1, temp);
                }
            }
        }

        int resultSize = (count < limit) ? count : limit;
        for (int i = 0; i < resultSize; i++) {
            result.add(upcoming.get(i));
        }
        return result;
    }
}