package eventtracker.io;

import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Pengekspor laporan progres event ke file teks (.txt) dan file tabel (.csv).
public class ReportExporter {

    // Method mengekspor laporan progres event ke file teks.
    public static synchronized void exportTxt(Event e, File f) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write("LAPORAN PROGRES EVENT");
            writer.newLine();
            writer.write("Nama event   : " + e.getName());
            writer.newLine();
            writer.write("Tanggal event: " + TextUtil.formatDate(e.getDate()));
            writer.newLine();
            writer.write("Dicetak      : " + TextUtil.formatDate(LocalDate.now()));
            writer.newLine();
            writer.write("Progres total: " + e.getProgress() + "% ("
                    + e.getTotalDone() + " dari " 
                    + e.getTotalTasks() + " tugas selesai, "
                    + e.getTotalLate() + " terlambat)");
            writer.newLine();
            writer.write("Progres divisi: Tertinggi " + e.getHighestDivisionProgress()
                    + "%, Terendah " + e.getLowestDivisionProgress() + "%");
            writer.newLine();
            writer.newLine();

            // Ringkasan Status (Map)
            writer.write("RINGKASAN STATUS TUGAS");
            writer.newLine();
            Map<String, Integer> statusSummary = e.getStatusSummary();
            for (Map.Entry<String, Integer> entry : statusSummary.entrySet()) {
                writer.write("  " + entry.getKey() + " : " + entry.getValue() + " tugas");
                writer.newLine();
            }
            writer.newLine();

            // Divisi dan tugas
            for (int i = 0; i < e.getDivisionCount(); i++) {
                Division d = e.getDivision(i);
                writer.write("DIVISI " + d.getName().toUpperCase()
                        + " (Ketua: " + (d.getHeadName().isEmpty() ? "-" : d.getHeadName()) + ")");
                writer.newLine();
                writer.write("Progres: " + d.getProgress() + "% ("
                        + d.countDone() + " dari " + d.getTaskCount() + " tugas selesai)");
                writer.newLine();
                if (d.getTaskCount() == 0) {
                    writer.write("  (Belum ada tugas)");
                    writer.newLine();
                } else {
                    for (int j = 0; j < d.getTaskCount(); j++) {
                        Task t = d.getTask(j);
                        writer.write("  " + (j + 1) + ". [" + t.getStatusLabel() + "] " + t.getTitle()
                                + " - PIC: " + t.getPicText()
                                + " - Deadline: " + TextUtil.formatDate(t.getDeadline()));
                        writer.newLine();
                    }
                }
                writer.newLine();
            }

            // Tugas terlambat
            writer.write("TUGAS TERLAMBAT");
            writer.newLine();
            List<Task> lateTasks = e.getLateTasks();
            if (lateTasks.isEmpty()) {
                writer.write("  Tidak ada tugas terlambat.");
                writer.newLine();
            } else {
                for (int i = 0; i < lateTasks.size(); i++) {
                    Task t = lateTasks.get(i);
                    writer.write("  " + (i + 1) + ". " + t.getTitle() + " (Divisi " + t.getDivisionName() + ")"
                            + " - PIC: " + t.getPicText()
                            + " - Deadline: " + TextUtil.formatDate(t.getDeadline()));
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
                    writer.write("  " + TextUtil.formatDate(m.getDate())
                            + " (" + m.getDivisionName() + "): " + summary);
                    writer.newLine();
                }
            }
        }
    }

    // Method mengekspor daftar tugas semua divisi ke file CSV (pemisah ";", setiap kolom diberi tanda kutip).
    public static synchronized void exportCsv(Event e, File f) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
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
        }
    }

    // Method membungkus teks dengan tanda kutip untuk kolom CSV (tanda kutip di dalam teks digandakan).
    private static String escapeCsv(String text) {
        if (text == null) {
            text = "";
        }
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
