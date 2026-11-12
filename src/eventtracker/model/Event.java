package eventtracker.model;

import java.time.LocalDate;
import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;

public class Event implements Progressable {
    private String name;
    private LocalDate date;
    private Division[] divisions;
    private int divisionCount;
    private Member[] members;
    private int memberCount;
    private Meeting[] meetings;
    private int meetingCount;

    public Event(String name, LocalDate date) throws InvalidDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("Nama event tidak boleh kosong.");
        }
        if (date == null) {
            throw new InvalidDataException("Tanggal event tidak boleh kosong.");
        }
        this.name = name.trim();
        this.date = date;
        this.divisions = new Division[10];
        this.divisionCount = 0;
        this.members = new Member[10];
        this.memberCount = 0;
        this.meetings = new Meeting[10];
        this.meetingCount = 0;
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
        for (int i = 0; i < divisionCount; i++) {
            if (divisions[i].getName().equalsIgnoreCase(d.getName())) {
                throw new InvalidDataException("Divisi dengan nama \"" + d.getName() + "\" sudah ada.");
            }
        }
        if (divisionCount == divisions.length) {
            Division[] newDivisions = new Division[divisions.length * 2];
            for (int i = 0; i < divisions.length; i++) {
                newDivisions[i] = divisions[i];
            }
            divisions = newDivisions;
        }
        divisions[divisionCount++] = d;
    }

    public void removeDivision(Division d) throws DataNotFoundException {
        int index = -1;
        for (int i = 0; i < divisionCount; i++) {
            if (divisions[i] == d) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new DataNotFoundException("Divisi tidak ditemukan.");
        }
        // Kosongkan divisi pada anggota yang berada di divisi ini
        for (int i = 0; i < memberCount; i++) {
            if (members[i].getDivisionName().equalsIgnoreCase(d.getName())) {
                members[i].setDivisionName("");
            }
        }
        for (int i = index; i < divisionCount - 1; i++) {
            divisions[i] = divisions[i + 1];
        }
        divisionCount--;
        divisions[divisionCount] = null;
    }

    public Division getDivision(int i) {
        if (i < 0 || i >= divisionCount) {
            throw new IndexOutOfBoundsException("Indeks divisi di luar rentang.");
        }
        return divisions[i];
    }

    public int getDivisionCount() {
        return divisionCount;
    }

    public Division findDivision(String name) throws DataNotFoundException {
        if (name != null) {
            for (int i = 0; i < divisionCount; i++) {
                if (divisions[i].getName().equalsIgnoreCase(name.trim())) {
                    return divisions[i];
                }
            }
        }
        throw new DataNotFoundException("Divisi \"" + name + "\" tidak ditemukan.");
    }

    public void addMember(Member m) {
        if (m == null) {
            return;
        }
        if (memberCount == members.length) {
            Member[] newMembers = new Member[members.length * 2];
            for (int i = 0; i < members.length; i++) {
                newMembers[i] = members[i];
            }
            members = newMembers;
        }
        members[memberCount++] = m;
    }

    public void removeMember(Member m) throws DataNotFoundException {
        int index = -1;
        for (int i = 0; i < memberCount; i++) {
            if (members[i] == m) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new DataNotFoundException("Anggota tidak ditemukan.");
        }
        for (int i = index; i < memberCount - 1; i++) {
            members[i] = members[i + 1];
        }
        memberCount--;
        members[memberCount] = null;
    }

    public Member getMember(int i) {
        if (i < 0 || i >= memberCount) {
            throw new IndexOutOfBoundsException("Indeks anggota di luar rentang.");
        }
        return members[i];
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void addMeeting(Meeting m) {
        if (m == null) {
            return;
        }
        if (meetingCount == meetings.length) {
            Meeting[] newMeetings = new Meeting[meetings.length * 2];
            for (int i = 0; i < meetings.length; i++) {
                newMeetings[i] = meetings[i];
            }
            meetings = newMeetings;
        }
        meetings[meetingCount++] = m;
    }

    public void removeMeeting(Meeting m) throws DataNotFoundException {
        int index = -1;
        for (int i = 0; i < meetingCount; i++) {
            if (meetings[i] == m) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new DataNotFoundException("Notulensi rapat tidak ditemukan.");
        }
        for (int i = index; i < meetingCount - 1; i++) {
            meetings[i] = meetings[i + 1];
        }
        meetingCount--;
        meetings[meetingCount] = null;
    }

    public Meeting getMeeting(int i) {
        if (i < 0 || i >= meetingCount) {
            throw new IndexOutOfBoundsException("Indeks notulensi di luar rentang.");
        }
        return meetings[i];
    }

    public int getMeetingCount() {
        return meetingCount;
    }

    public int getTotalTasks() {
        int total = 0;
        for (int i = 0; i < divisionCount; i++) {
            total += divisions[i].getTaskCount();
        }
        return total;
    }

    public int getTotalDone() {
        int done = 0;
        for (int i = 0; i < divisionCount; i++) {
            done += divisions[i].countDone();
        }
        return done;
    }

    public int getTotalLate() {
        int late = 0;
        for (int i = 0; i < divisionCount; i++) {
            late += divisions[i].countLate();
        }
        return late;
    }

    public int getProgress() {
        int total = getTotalTasks();
        if (total == 0) {
            return 0;
        }
        int progress = getTotalDone() * 100 / total;
        assert progress >= 0 && progress <= 100 : "Progres event harus antara 0 dan 100";
        return progress;
    }

    public Task[] getLateTasks() {
        int lateCount = getTotalLate();
        Task[] lateTasks = new Task[lateCount];
        int idx = 0;
        for (int i = 0; i < divisionCount; i++) {
            Division div = divisions[i];
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                if (t.isLate()) {
                    lateTasks[idx++] = t;
                }
            }
        }
        return lateTasks;
    }

    public Task[] getUpcomingTasks(int limit) {
        if (limit <= 0) {
            return new Task[0];
        }
        LocalDate today = LocalDate.now();
        // Hitung tugas yang belum selesai dan belum lewat
        int count = 0;
        for (int i = 0; i < divisionCount; i++) {
            Division div = divisions[i];
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                if (t.getStatus() != Status.SELESAI && !t.getDeadline().isBefore(today)) {
                    count++;
                }
            }
        }

        Task[] upcoming = new Task[count];
        int idx = 0;
        for (int i = 0; i < divisionCount; i++) {
            Division div = divisions[i];
            for (int j = 0; j < div.getTaskCount(); j++) {
                Task t = div.getTask(j);
                if (t.getStatus() != Status.SELESAI && !t.getDeadline().isBefore(today)) {
                    upcoming[idx++] = t;
                }
            }
        }

        // Urutkan dengan bubble sort berdasarkan deadline terdekat
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (upcoming[j].getDeadline().isAfter(upcoming[j + 1].getDeadline())) {
                    Task temp = upcoming[j];
                    upcoming[j] = upcoming[j + 1];
                    upcoming[j + 1] = temp;
                }
            }
        }

        int resultSize = (count < limit) ? count : limit;
        Task[] result = new Task[resultSize];
        for (int i = 0; i < resultSize; i++) {
            result[i] = upcoming[i];
        }
        return result;
    }
}
