package eventtracker.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Task;

public class ReportExporter {

    public static void exportTxt(Event e, File f) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
        try {
            writer.write("LAPORAN PROGRES EVENT");
            writer.newLine();
            writer.write("Nama event   : " + e.getName());
            writer.newLine();
            writer.write("Tanggal event: " + TextUtil.formatDate(e.getDate()));
            writer.newLine();
            writer.write("Dicetak      : " + TextUtil.formatDate(LocalDate.now()));
            writer.newLine();
            writer.write("Progres total: " + e.getProgress() + "% (" + e.getTotalDone() + " dari " + e.getTotalTasks() + " tugas selesai, " + e.getTotalLate() + " terlambat)");
            writer.newLine();
            writer.newLine();

            // Divisi dan tugas
            for (int i = 0; i < e.getDivisionCount(); i++) {
                Division d = e.getDivision(i);
                writer.write("DIVISI " + d.getName().toUpperCase() + " (Ketua: " + (d.getHeadName().isEmpty() ? "-" : d.getHeadName()) + ")");
                writer.newLine();
                writer.write("Progres: " + d.getProgress() + "% (" + d.countDone() + " dari " + d.getTaskCount() + " tugas selesai)");
                writer.newLine();
                if (d.getTaskCount() == 0) {
                    writer.write("  (Belum ada tugas)");
                    writer.newLine();
                } else {
                    for (int j = 0; j < d.getTaskCount(); j++) {
                        Task t = d.getTask(j);
                        writer.write("  " + (j + 1) + ". [" + t.getStatusLabel() + "] " + t.getTitle() + " - PIC: " + t.getPicText() + " - Deadline: " + TextUtil.formatDate(t.getDeadline()));
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            // Tugas terlambat
            writer.write("TUGAS TERLAMBAT");
            writer.newLine();
            Task[] lateTasks = e.getLateTasks();
            if (lateTasks.length == 0) {
                writer.write("  Tidak ada tugas terlambat.");
                writer.newLine();
            } else {
                for (int i = 0; i < lateTasks.length; i++) {
                    Task t = lateTasks[i];
                    writer.write("  " + (i + 1) + ". " + t.getTitle() + " (Divisi " + t.getDivisionName() + ") - PIC: " + t.getPicText() + " - Deadline: " + TextUtil.formatDate(t.getDeadline()));
                    writer.newLine();
                }
            }
            writer.newLine();

            // Notulensi rapat
            writer.write("NOTULENSI RAPAT");
            writer.newLine();
            if (e.getMeetingCount() == 0) {
                writer.write("  Belum ada notulensi rapat.");
                writer.newLine();
            } else {
                for (int i = 0; i < e.getMeetingCount(); i++) {
                    Meeting m = e.getMeeting(i);
                    String summary = m.getContent();
                    if (!m.getDecision().isEmpty()) {
                        summary += " | Keputusan: " + m.getDecision();
                    }
                    writer.write("  " + TextUtil.formatDate(m.getDate()) + " (" + m.getDivisionName() + "): " + summary);
                    writer.newLine();
                }
            }
        } finally {
            writer.close();
        }
    }

    public static void exportCsv(Event e, File f) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
        try {
            // Header
            writer.write("Divisi;Judul;PIC;Prioritas;Deadline;Status;QC;Catatan;Link");
            writer.newLine();

            for (int i = 0; i < e.getDivisionCount(); i++) {
                Division d = e.getDivision(i);
                for (int j = 0; j < d.getTaskCount(); j++) {
                    Task t = d.getTask(j);
                    writer.write(escapeCsv(t.getDivisionName()) + ";"
                            + escapeCsv(t.getTitle()) + ";"
                            + escapeCsv(t.getPicText()) + ";"
                            + escapeCsv(t.getPriority().getLabel()) + ";"
                            + escapeCsv(TextUtil.formatDate(t.getDeadline())) + ";"
                            + escapeCsv(t.getStatusLabel()) + ";"
                            + escapeCsv(t.isQualityChecked() ? "Ya" : "Tidak") + ";"
                            + escapeCsv(t.getNotes()) + ";"
                            + escapeCsv(t.getResultLink()));
                    writer.newLine();
                }
            }
        } finally {
            writer.close();
        }
    }

    private static String escapeCsv(String text) {
        if (text == null) {
            text = "";
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
