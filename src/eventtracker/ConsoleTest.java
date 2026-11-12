package eventtracker;

import java.io.File;
import java.time.LocalDate;
import eventtracker.exception.InvalidDataException;
import eventtracker.io.FileManager;
import eventtracker.io.ReportExporter;
import eventtracker.io.SampleData;
import eventtracker.io.TextUtil;
import eventtracker.model.Coordinator;
import eventtracker.model.Division;
import eventtracker.model.DivisionHead;
import eventtracker.model.Event;
import eventtracker.model.Member;
// import eventtracker.model.Position;
import eventtracker.model.Priority;
import eventtracker.model.Staff;
// import eventtracker.model.Status;
import eventtracker.model.Task;

public class ConsoleTest {
    public static void main(String[] args) {
        boolean assertsEnabled = false;
        assert assertsEnabled = true; // side effect to check if -ea is enabled
        System.out.println("=== PENGUJIAN OTOMATIS EVENT PROGRESS TRACKER ===");
        System.out.println("Assertion (-ea) aktif: " + assertsEnabled);

        try {
            // 1. Uji Pembuatan Data Contoh
            Event event = SampleData.create();
            if (event == null) {
                throw new RuntimeException("Gagal membuat SampleData");
            }
            System.out.println("1. SampleData berhasil dibuat: " + event.getName());
            System.out.println("   Jumlah Divisi: " + event.getDivisionCount());
            System.out.println("   Jumlah Anggota: " + event.getMemberCount());
            System.out.println("   Jumlah Notulensi: " + event.getMeetingCount());
            System.out.println("   Total Tugas: " + event.getTotalTasks());
            System.out.println("   Total Tugas Selesai: " + event.getTotalDone());
            System.out.println("   Total Tugas Terlambat: " + event.getTotalLate());
            System.out.println("   Progres Event: " + event.getProgress() + "%");

            if (event.getDivisionCount() != 5) throw new RuntimeException("Divisi harusnya 5");
            if (event.getMemberCount() != 14) throw new RuntimeException("Anggota harusnya 14");
            if (event.getTotalTasks() != 12) throw new RuntimeException("Tugas harusnya 12");
            if (event.getMeetingCount() != 2) throw new RuntimeException("Notulensi harusnya 2");

            // 2. Uji Hak Akses (Polymorphism)
            Member coordinator = event.getMember(0);
            Member divHead = event.getMember(4); // Farhan Kusuma (Acara)
            Member staff = event.getMember(9);    // Yoga Prasetyo (Acara)

            if (!(coordinator instanceof Coordinator)) throw new RuntimeException("Koordinator salah class");
            if (!(divHead instanceof DivisionHead)) throw new RuntimeException("Ketua Divisi salah class");
            if (!(staff instanceof Staff)) throw new RuntimeException("Staff salah class");

            if (!coordinator.canManageEvent()) throw new RuntimeException("Koordinator harus canManageEvent");
            if (!coordinator.canEdit("Acara") || !coordinator.canEdit("Humas")) throw new RuntimeException("Koordinator harus canEdit semua");

            if (divHead.canManageEvent()) throw new RuntimeException("Ketua Divisi tidak boleh canManageEvent");
            if (!divHead.canEdit("Acara")) throw new RuntimeException("Ketua Divisi Acara harus canEdit Acara");
            if (divHead.canEdit("Humas")) throw new RuntimeException("Ketua Divisi Acara tidak boleh canEdit Humas");

            if (staff.canManageEvent() || staff.canEdit("Acara")) throw new RuntimeException("Staff tidak boleh edit");
            System.out.println("2. Pengujian Hak Akses (Polymorphism) BERHASIL.");

            // 3. Uji Simpan dan Muat File (.evt)
            File testEvt = new File("test_event.evt");
            FileManager.save(event, testEvt);
            System.out.println("3. File .evt berhasil disimpan.");

            Event loadedEvent = FileManager.load(testEvt);
            System.out.println("   File .evt berhasil dimuat.");
            if (!loadedEvent.getName().equals(event.getName())) throw new RuntimeException("Nama event tidak sama!");
            if (loadedEvent.getDivisionCount() != event.getDivisionCount()) throw new RuntimeException("Jumlah divisi tidak sama!");
            if (loadedEvent.getMemberCount() != event.getMemberCount()) throw new RuntimeException("Jumlah anggota tidak sama!");
            if (loadedEvent.getTotalTasks() != event.getTotalTasks()) throw new RuntimeException("Jumlah tugas tidak sama!");
            if (loadedEvent.getTotalDone() != event.getTotalDone()) throw new RuntimeException("Jumlah tugas selesai tidak sama!");
            if (loadedEvent.getTotalLate() != event.getTotalLate()) throw new RuntimeException("Jumlah tugas terlambat tidak sama!");
            if (loadedEvent.getMeetingCount() != event.getMeetingCount()) throw new RuntimeException("Jumlah notulensi tidak sama!");
            System.out.println("   Integritas data tersimpan dan termuat kembali 100% COCOK.");

            // 4. Uji Export Laporan TXT dan CSV
            File testTxt = new File("test_laporan.txt");
            ReportExporter.exportTxt(event, testTxt);
            System.out.println("4. Export TXT berhasil.");

            File testCsv = new File("test_tugas.csv");
            ReportExporter.exportCsv(event, testCsv);
            System.out.println("   Export CSV berhasil.");

            // 5. Uji Validasi & Exception
            System.out.println("5. Uji Validasi:");
            try {
                new Task("", "Acara", Priority.SEDANG, LocalDate.now());
                throw new RuntimeException("Gagal mendeteksi judul kosong");
            } catch (InvalidDataException ex) {
                System.out.println("   [OK] " + ex.getMessage());
            }

            try {
                TextUtil.parseDate("2026-12-25");
                throw new RuntimeException("Gagal mendeteksi format tanggal terbalik");
            } catch (InvalidDataException ex) {
                System.out.println("   [OK] " + ex.getMessage());
            }

            try {
                event.addDivision(new Division("Acara", "Farhan"));
                throw new RuntimeException("Gagal mendeteksi divisi kembar");
            } catch (InvalidDataException ex) {
                System.out.println("   [OK] " + ex.getMessage());
            }

            // Bersihkan file uji coba
            if (testEvt.exists()) testEvt.delete();
            if (testTxt.exists()) testTxt.delete();
            if (testCsv.exists()) testCsv.delete();

            System.out.println("\nSEMUA PENGUJIAN BERHASIL TANPA KESALAHAN!");

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
