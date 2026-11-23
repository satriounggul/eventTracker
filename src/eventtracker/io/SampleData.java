package eventtracker.io;

import eventtracker.exception.InvalidDataException;
import eventtracker.model.Division;
import eventtracker.model.Event;
import eventtracker.model.Meeting;
import eventtracker.model.Member;
import eventtracker.model.Position;
import eventtracker.model.Priority;
import eventtracker.model.Status;
import eventtracker.model.Task;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

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
            t1.setPics(new HashSet<>(Arrays.asList("Farhan Kusuma")));
            divAcara.addTask(t1);

            Task t2 = new Task("Rancang permainan pembuka", "Acara", today.plusDays(5)); // Overloaded constructor (SEDANG)
            t2.setStatus(Status.PROSES);
            t2.setPics(new HashSet<>(Arrays.asList("Yoga Prasetyo")));
            divAcara.addTask(t2);

            Task t3 = new Task("Buat skema perpindahan peserta", "Acara", Priority.TINGGI, today.minusDays(2));
            t3.setStatus(Status.PROSES);
            t3.setAllPics(true);
            divAcara.addTask(t3);

            // Tugas Divisi Humas
            Task t4 = new Task("Buat surat izin kegiatan", "Humas", Priority.TINGGI, today.minusDays(5));
            t4.setStatus(Status.SELESAI);
            t4.setPics(new HashSet<>(Arrays.asList("Intan Permata")));
            divHumas.addTask(t4);

            Task t5 = new Task("Kirim undangan pembicara", "Humas", Priority.SEDANG, today.plusDays(2));
            t5.setStatus(Status.BELUM);
            t5.setPics(new HashSet<>(Arrays.asList("Citra Anggraini")));
            divHumas.addTask(t5);

            Task t6 = new Task("Hubungi sponsor lokal", "Humas", Priority.RENDAH, today.plusDays(10));
            t6.setStatus(Status.BELUM);
            t6.setPics(new HashSet<>(Arrays.asList("Intan Permata", "Citra Anggraini")));
            divHumas.addTask(t6);

            // Tugas Divisi Dokumentasi
            Task t7 = new Task("Daftar kebutuhan alat foto", "Dokumentasi", Priority.SEDANG, today.plusDays(1));
            t7.setStatus(Status.PROSES);
            t7.setPics(new HashSet<>(Arrays.asList("Rizky Febrian")));
            divDokumentasi.addTask(t7);

            Task t8 = new Task("Konsep video pembuka", "Dokumentasi", Priority.SEDANG, today.plusDays(8));
            t8.setStatus(Status.BELUM);
            t8.setPics(new HashSet<>(Arrays.asList("Tegar Saputra")));
            divDokumentasi.addTask(t8);

            // Tugas Divisi Konsumsi
            Task t9 = new Task("Susun anggaran konsumsi", "Konsumsi", Priority.TINGGI, today.minusDays(4));
            t9.setStatus(Status.SELESAI);
            t9.setPics(new HashSet<>(Arrays.asList("Mega Lestari")));
            divKonsumsi.addTask(t9);

            Task t10 = new Task("Survei penyedia katering", "Konsumsi", Priority.SEDANG, today.minusDays(1));
            t10.setStatus(Status.BELUM);
            t10.setPics(new HashSet<>(Arrays.asList("Mega Lestari", "Wulan Sari")));
            divKonsumsi.addTask(t10);

            // Tugas Divisi Perlengkapan
            Task t11 = new Task("Data inventaris peralatan", "Perlengkapan", Priority.SEDANG, today.plusDays(3));
            t11.setStatus(Status.PROSES);
            t11.setPics(new HashSet<>(Arrays.asList("Bagas Wicaksono")));
            divPerlengkapan.addTask(t11);

            Task t12 = new Task("Pinjam sound system", "Perlengkapan", Priority.TINGGI, today.plusDays(7));
            t12.setStatus(Status.BELUM);
            t12.setPics(new HashSet<>(Arrays.asList("Dani Firmansyah")));
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
            return null;
        }
    }
}