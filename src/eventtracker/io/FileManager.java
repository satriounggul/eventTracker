package eventtracker.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import eventtracker.exception.DataNotFoundException;
import eventtracker.exception.InvalidDataException;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Member;
import eventtracker.model.Position;
import eventtracker.model.Priority;
import eventtracker.model.Status;
import eventtracker.model.Task;

public class FileManager {

    public static void save(Event e, File f) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
        try {
            // EVENT|nama|tanggal
            writer.write("EVENT|" + TextUtil.escape(e.getName()) + "|" + TextUtil.formatDate(e.getDate()));
            writer.newLine();

            // DIVISION|nama|ketua
            for (int i = 0; i < e.getDivisionCount(); i++) {
                Division d = e.getDivision(i);
                writer.write("DIVISION|" + TextUtil.escape(d.getName()) + "|" + TextUtil.escape(d.getHeadName()));
                writer.newLine();
            }

            // MEMBER|nama|JABATAN|divisi
            for (int i = 0; i < e.getMemberCount(); i++) {
                Member m = e.getMember(i);
                writer.write("MEMBER|" + TextUtil.escape(m.getName()) + "|" + m.getPosition().name() + "|" + TextUtil.escape(m.getDivisionName()));
                writer.newLine();
            }

            // TASK|divisi|judul|deskripsi|pic1;pic2|allPics|PRIORITAS|deadline|STATUS|catatan|link|qc
            for (int i = 0; i < e.getDivisionCount(); i++) {
                Division d = e.getDivision(i);
                for (int j = 0; j < d.getTaskCount(); j++) {
                    Task t = d.getTask(j);
                    writer.write("TASK|"
                            + TextUtil.escape(t.getDivisionName()) + "|"
                            + TextUtil.escape(t.getTitle()) + "|"
                            + TextUtil.escape(t.getDescription()) + "|"
                            + TextUtil.escape(TextUtil.joinPics(t.getPics())) + "|"
                            + t.isAllPics() + "|"
                            + t.getPriority().name() + "|"
                            + TextUtil.formatDate(t.getDeadline()) + "|"
                            + t.getStatus().name() + "|"
                            + TextUtil.escape(t.getNotes()) + "|"
                            + TextUtil.escape(t.getResultLink()) + "|"
                            + t.isQualityChecked());
                    writer.newLine();
                }
            }

            // MEETING|tanggal|divisi|isi|keputusan
            for (int i = 0; i < e.getMeetingCount(); i++) {
                Meeting m = e.getMeeting(i);
                writer.write("MEETING|"
                        + TextUtil.formatDate(m.getDate()) + "|"
                        + TextUtil.escape(m.getDivisionName()) + "|"
                        + TextUtil.escape(m.getContent()) + "|"
                        + TextUtil.escape(m.getDecision()));
                writer.newLine();
            }
        } finally {
            writer.close();
        }
    }

    public static Event load(File f) throws IOException, InvalidDataException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
        Event event = null;
        int lineNumber = 0;
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", -1);
                String tag = parts[0];

                try {
                    if ("EVENT".equals(tag)) {
                        if (parts.length != 3) {
                            throw new InvalidDataException("Jumlah kolom data EVENT tidak sesuai (harus 3 kolom).");
                        }
                        String name = TextUtil.unescape(parts[1]);
                        LocalDate date = TextUtil.parseDate(parts[2]);
                        event = new Event(name, date);

                    } else if ("DIVISION".equals(tag)) {
                        if (event == null) {
                            throw new InvalidDataException("Data EVENT harus mendahului data DIVISION.");
                        }
                        if (parts.length != 3) {
                            throw new InvalidDataException("Jumlah kolom data DIVISION tidak sesuai (harus 3 kolom).");
                        }
                        String name = TextUtil.unescape(parts[1]);
                        String headName = TextUtil.unescape(parts[2]);
                        event.addDivision(new Division(name, headName));

                    } else if ("MEMBER".equals(tag)) {
                        if (event == null) {
                            throw new InvalidDataException("Data EVENT harus mendahului data MEMBER.");
                        }
                        if (parts.length != 4) {
                            throw new InvalidDataException("Jumlah kolom data MEMBER tidak sesuai (harus 4 kolom).");
                        }
                        String name = TextUtil.unescape(parts[1]);
                        Position pos;
                        try {
                            pos = Position.valueOf(parts[2]);
                        } catch (IllegalArgumentException ex) {
                            throw new InvalidDataException("Jabatan tidak dikenal: " + parts[2]);
                        }
                        String divisionName = TextUtil.unescape(parts[3]);
                        event.addMember(Member.create(name, pos, divisionName));

                    } else if ("TASK".equals(tag)) {
                        if (event == null) {
                            throw new InvalidDataException("Data EVENT harus mendahului data TASK.");
                        }
                        if (parts.length != 12) {
                            throw new InvalidDataException("Jumlah kolom data TASK tidak sesuai (harus 12 kolom).");
                        }
                        String divName = TextUtil.unescape(parts[1]);
                        String title = TextUtil.unescape(parts[2]);
                        String desc = TextUtil.unescape(parts[3]);
                        String picsText = TextUtil.unescape(parts[4]);
                        boolean allPics = Boolean.parseBoolean(parts[5]);
                        Priority priority;
                        try {
                            priority = Priority.valueOf(parts[6]);
                        } catch (IllegalArgumentException ex) {
                            throw new InvalidDataException("Prioritas tidak dikenal: " + parts[6]);
                        }
                        LocalDate deadline = TextUtil.parseDate(parts[7]);
                        Status status;
                        try {
                            status = Status.valueOf(parts[8]);
                        } catch (IllegalArgumentException ex) {
                            throw new InvalidDataException("Status tidak dikenal: " + parts[8]);
                        }
                        String notes = TextUtil.unescape(parts[9]);
                        String link = TextUtil.unescape(parts[10]);
                        boolean qc = Boolean.parseBoolean(parts[11]);

                        Division div = event.findDivision(divName);
                        Task task = new Task(title, div.getName(), priority, deadline);
                        task.setDescription(desc);
                        task.setPics(TextUtil.splitPics(picsText));
                        task.setAllPics(allPics);
                        task.setStatus(status);
                        task.setNotes(notes);
                        task.setResultLink(link);
                        task.setQualityChecked(qc);
                        div.addTask(task);

                    } else if ("MEETING".equals(tag)) {
                        if (event == null) {
                            throw new InvalidDataException("Data EVENT harus mendahului data MEETING.");
                        }
                        if (parts.length != 5) {
                            throw new InvalidDataException("Jumlah kolom data MEETING tidak sesuai (harus 5 kolom).");
                        }
                        LocalDate mDate = TextUtil.parseDate(parts[1]);
                        String divName = TextUtil.unescape(parts[2]);
                        String content = TextUtil.unescape(parts[3]);
                        String decision = TextUtil.unescape(parts[4]);
                        event.addMeeting(new Meeting(mDate, divName, content, decision));

                    } else {
                        throw new InvalidDataException("Tipe data tidak dikenal: " + tag);
                    }
                } catch (DataNotFoundException ex) {
                    throw new InvalidDataException("Baris " + lineNumber + ": " + ex.getMessage());
                } catch (InvalidDataException ex) {
                    throw new InvalidDataException("Baris " + lineNumber + ": " + ex.getMessage());
                } catch (Exception ex) {
                    throw new InvalidDataException("Baris " + lineNumber + ": " + ex.getMessage());
                }
            }
        } finally {
            reader.close();
        }

        if (event == null) {
            throw new InvalidDataException("File tidak memiliki data EVENT.");
        }
        return event;
    }
}
