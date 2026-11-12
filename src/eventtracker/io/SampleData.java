package eventtracker.io;

import java.time.LocalDate;
import eventtracker.exception.InvalidDataException;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Member;
import eventtracker.model.Position;
import eventtracker.model.Priority;
import eventtracker.model.Status;
import eventtracker.model.Task;

public class SampleData {

    public static Event create() {
        try {
            LocalDate today = LocalDate.now();
            Event event = new Event("Gebyar Mahasiswa Baru Fakultas Rekayasa 2027", today.plusDays(60));

            // Divisi
            Division divAcara = new Division("Acara", "Farhan Kusuma");
            Division divHumas = new Division("Humas", "Intan Permata");
            Division divDokumentasi = new Division("Dokumentasi", "Rizky Febrian");
            Division divKonsumsi = new Division("Konsumsi", "Mega Lestari");
            Division divPerlengkapan = new Division("Perlengkapan", "Bagas Wicaksono");

            event.addDivision(divAcara);
            event.addDivision(divHumas);
            event.addDivision(divDokumentasi);
            event.addDivision(divKonsumsi);
            event.addDivision(divPerlengkapan);

            // Anggota - Koordinator
            event.addMember(Member.create("Dimas Aryanto", Position.KETUA, ""));
            event.addMember(Member.create("Putri Maharani", Position.WAKIL, ""));
            event.addMember(Member.create("Naufal Hakim", Position.SEKRETARIS, ""));
            event.addMember(Member.create("Salsa Ramadhani", Position.BENDAHARA, ""));

            // Anggota - Ketua Divisi
            event.addMember(Member.create("Farhan Kusuma", Position.KETUA_DIVISI, "Acara"));
            event.addMember(Member.create("Intan Permata", Position.KETUA_DIVISI, "Humas"));
            event.addMember(Member.create("Rizky Febrian", Position.KETUA_DIVISI, "Dokumentasi"));
            event.addMember(Member.create("Mega Lestari", Position.KETUA_DIVISI, "Konsumsi"));
            event.addMember(Member.create("Bagas Wicaksono", Position.KETUA_DIVISI, "Perlengkapan"));

            // Anggota - Staff
            event.addMember(Member.create("Yoga Prasetyo", Position.ANGGOTA, "Acara"));
            event.addMember(Member.create("Citra Anggraini", Position.ANGGOTA, "Humas"));
            event.addMember(Member.create("Tegar Saputra", Position.ANGGOTA, "Dokumentasi"));
            event.addMember(Member.create("Wulan Sari", Position.ANGGOTA, "Konsumsi"));
            event.addMember(Member.create("Dani Firmansyah", Position.ANGGOTA, "Perlengkapan"));

            // Tugas Divisi Acara
            Task t1 = new Task("Susun rundown hari pertama", "Acara", Priority.TINGGI, today.minusDays(3));
            t1.setStatus(Status.SELESAI);
            String[] pic1 = new String[1];
            pic1[0] = "Farhan Kusuma";
            t1.setPics(pic1);
            divAcara.addTask(t1);

            Task t2 = new Task("Rancang permainan pembuka", "Acara", Priority.SEDANG, today.plusDays(5));
            t2.setStatus(Status.PROSES);
            String[] pic2 = new String[1];
            pic2[0] = "Yoga Prasetyo";
            t2.setPics(pic2);
            divAcara.addTask(t2);

            Task t3 = new Task("Buat skema perpindahan peserta", "Acara", Priority.TINGGI, today.minusDays(2));
            t3.setStatus(Status.PROSES);
            t3.setAllPics(true);
            divAcara.addTask(t3);

            // Tugas Divisi Humas
            Task t4 = new Task("Buat surat izin kegiatan", "Humas", Priority.TINGGI, today.minusDays(5));
            t4.setStatus(Status.SELESAI);
            String[] pic4 = new String[1];
            pic4[0] = "Intan Permata";
            t4.setPics(pic4);
            divHumas.addTask(t4);

            Task t5 = new Task("Kirim undangan pembicara", "Humas", Priority.SEDANG, today.plusDays(2));
            t5.setStatus(Status.BELUM);
            String[] pic5 = new String[1];
            pic5[0] = "Citra Anggraini";
            t5.setPics(pic5);
            divHumas.addTask(t5);

            Task t6 = new Task("Hubungi sponsor lokal", "Humas", Priority.RENDAH, today.plusDays(10));
            t6.setStatus(Status.BELUM);
            String[] pic6 = new String[2];
            pic6[0] = "Intan Permata";
            pic6[1] = "Citra Anggraini";
            t6.setPics(pic6);
            divHumas.addTask(t6);

            // Tugas Divisi Dokumentasi
            Task t7 = new Task("Daftar kebutuhan alat foto", "Dokumentasi", Priority.SEDANG, today.plusDays(1));
            t7.setStatus(Status.PROSES);
            String[] pic7 = new String[1];
            pic7[0] = "Rizky Febrian";
            t7.setPics(pic7);
            divDokumentasi.addTask(t7);

            Task t8 = new Task("Konsep video pembuka", "Dokumentasi", Priority.SEDANG, today.plusDays(8));
            t8.setStatus(Status.BELUM);
            String[] pic8 = new String[1];
            pic8[0] = "Tegar Saputra";
            t8.setPics(pic8);
            divDokumentasi.addTask(t8);

            // Tugas Divisi Konsumsi
            Task t9 = new Task("Susun anggaran konsumsi", "Konsumsi", Priority.TINGGI, today.minusDays(4));
            t9.setStatus(Status.SELESAI);
            String[] pic9 = new String[1];
            pic9[0] = "Mega Lestari";
            t9.setPics(pic9);
            divKonsumsi.addTask(t9);

            Task t10 = new Task("Survei penyedia katering", "Konsumsi", Priority.SEDANG, today.minusDays(1));
            t10.setStatus(Status.BELUM);
            String[] pic10 = new String[2];
            pic10[0] = "Mega Lestari";
            pic10[1] = "Wulan Sari";
            t10.setPics(pic10);
            divKonsumsi.addTask(t10);

            // Tugas Divisi Perlengkapan
            Task t11 = new Task("Data inventaris peralatan", "Perlengkapan", Priority.SEDANG, today.plusDays(3));
            t11.setStatus(Status.PROSES);
            String[] pic11 = new String[1];
            pic11[0] = "Bagas Wicaksono";
            t11.setPics(pic11);
            divPerlengkapan.addTask(t11);

            Task t12 = new Task("Pinjam sound system", "Perlengkapan", Priority.TINGGI, today.plusDays(7));
            t12.setStatus(Status.BELUM);
            String[] pic12 = new String[1];
            pic12[0] = "Dani Firmansyah";
            t12.setPics(pic12);
            divPerlengkapan.addTask(t12);

            // Notulensi Rapat
            event.addMeeting(new Meeting(today.minusDays(10), "Semua",
                    "Rapat pleno perdana penentuan tema dan konsep besar acara",
                    "Tema disepakati, anggaran awal diajukan ke dekanat"));
            event.addMeeting(new Meeting(today.minusDays(4), "Acara",
                    "Rapat teknis rundown hari H dan kebutuhan pengisi acara",
                    "Rundown draft 1 rampung, pengisi acara dikontak minggu depan"));

            return event;
        } catch (InvalidDataException e) {
            // Seharusnya tidak terjadi pada data contoh
            return null;
        }
    }
}
