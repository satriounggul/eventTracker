package eventtracker.model;

import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Event utama beserta divisi, anggota, rapat, dan statistik tugas.
public class Event implements Progressable {
    private String name;
    private LocalDate date;
    private List<Division> divisions;
    private List<Member> members;
    private List<Meeting> meetings;

    // Constructor inisialisasi acara baru.
    public Event(String name, LocalDate date) throws InvalidDataException {
        // Validasi nama dan tanggal acara.
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama event tidak boleh kosong.");
        }
        if (date == null) {
            throw new InvalidDataException("Tanggal event tidak boleh kosong.");
        }
        // Mengatur nama dan tanggal acara.
        this.name = name.trim();
        this.date = date;
        // Membuat array untuk menampung daftar divisi, anggota, dan rapat.
        this.divisions = new ArrayList<>();
        this.members = new ArrayList<>();
        this.meetings = new ArrayList<>();
    }

    // Method getter dan setter nama acara.
    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama event tidak boleh kosong.");
        }
        this.name = name.trim();
    }

    // Method getter dan setter tanggal acara.
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) throws InvalidDataException {
        if (date == null) {
            throw new InvalidDataException("Tanggal event tidak boleh kosong.");
        }
        this.date = date;
    }

    // Method memasukkan divisi ke acara.
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

    // Method menghapus divisi dari acara.
    public void removeDivision(Division d) throws DataNotFoundException {
        boolean removed = divisions.remove(d);
        if (!removed) {
            throw new DataNotFoundException("Divisi tidak ditemukan.");
        }
        // Mengosongkan elemen nama divisi pada anggota.
        for (Member m : members) {
            if (m.getDivisionName().equalsIgnoreCase(d.getName())) {
                m.setDivisionName("");
            }
        }
    }

    // Method mengambil objek divisi berdasarkan indeks.
    public Division getDivision(int i) {
        if (i < 0 || i >= divisions.size()) {
            throw new IndexOutOfBoundsException("Indeks divisi di luar rentang.");
        }
        return divisions.get(i);
    }

    // Method menghitung total divisi pada acara.
    public int getDivisionCount() {
        return divisions.size();
    }

    // Method mengambil objek divisi berdasarkan nama.
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

    // Method memasukkan anggota ke dalam daftar anggota acara.
    public void addMember(Member m) {
        if (m == null) {
            return;
        }
        members.add(m);
    }

    // Method menghapus anggota dari daftar anggota acara.
    public void removeMember(Member m) throws DataNotFoundException {
        boolean removed = members.remove(m);
        if (!removed) {
            throw new DataNotFoundException("Anggota tidak ditemukan.");
        }
    }

    // Method mengambil objek anggota berdasarkan indeks.
    public Member getMember(int i) {
        if (i < 0 || i >= members.size()) {
            throw new IndexOutOfBoundsException("Indeks anggota di luar rentang.");
        }
        return members.get(i);
    }

    // Method menghitung jumlah anggota pada acara.
    public int getMemberCount() {
        return members.size();
    }

    // Method memasukkan catatan rapat ke dalam daftar notulensi acara.
    public void addMeeting(Meeting m) {
        if (m == null) {
            return;
        }
        meetings.add(m);
    }

    // Method menghapus catatan rapat dari daftar notulensi acara.
    public void removeMeeting(Meeting m) throws DataNotFoundException {
        boolean removed = meetings.remove(m);
        if (!removed) {
            throw new DataNotFoundException("Notulensi rapat tidak ditemukan.");
        }
    }

    // Method mengambil objek notulensi rapat berdasarkan indeks.
    public Meeting getMeeting(int i) {
        if (i < 0 || i >= meetings.size()) {
            throw new IndexOutOfBoundsException("Indeks notulensi di luar rentang.");
        }
        return meetings.get(i);
    }

    // Method menghitung jumlah catatan rapat.
    public int getMeetingCount() {
        return meetings.size();
    }

    // Method menghitung seluruh tugas dari semua divisi.
    public int getTotalTasks() {
        int total = 0;
        for (Division d : divisions) {
            total += d.getTaskCount();
        }
        return total;
    }

    // Method menghitung seluruh tugas dari semua divisi dengan status "SELESAI".
    public int getTotalDone() {
        int done = 0;
        for (Division d : divisions) {
            done += d.countDone();
        }
        return done;
    }

    // Method menghitung seluruh tugas dari semua divisi yang melebihi batas waktu.
    public int getTotalLate() {
        int late = 0;
        for (Division d : divisions) {
            late += d.countLate();
        }
        return late;
    }

    // Method menghitung persentase tugas yang sudah selesai dari semua divisi.
    @Override
    public int getProgress() {
        int total = getTotalTasks();
        if (total == 0) {
            return 0;
        }
        return getTotalDone() * 100 / total;
    }

    // Method mencari persentase progres divisi tertinggi.
    // Memanfaatkan Collections.max().
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

    // Method mencari persentase progres divisi terendah.
    // Memanfaatkan Collections.min().
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

    // Method meringkas jumlah tugas per status.
    // Memanfaatkan HashMap.
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

    // Method mengumpulkan tugas dari seluruh divisi yang melebihi batas waktu menggunakan List.
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

    // Method menampilkan tugas terdekat (belum selesai & belum lewat batas waktu).
    public List<Task> getUpcomingTasks(int limit) {
        List<Task> result = new ArrayList<>();
        if (limit <= 0) {
            return result;
        }
        
        // Mengumpulkan tugas yang belum selesai & belum lewat batas waktu.
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

        // Mengurutkan berdasarkan batas waktu terdekat.
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

        // Menampilkan sesuai dengan jumlah limit.
        int resultSize = (count < limit) ? count : limit;
        for (int i = 0; i < resultSize; i++) {
            result.add(upcoming.get(i));
        }
        return result;
    }
    
    // Method Overloading (getUpcomingTasks).
    // Menetapkan limit default 5.
    public List<Task> getUpcomingTasks() {
        return getUpcomingTasks(5);
    }

}