package eventtracker.io;

import eventtracker.exception.InvalidDataException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TextUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        String s = text.replace("|", "/");
        s = s.replace("\r\n", "\\n");
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\n");
        return s;
    }

    public static String unescape(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\n", "\n");
    }

    public static String joinPics(Set<String> pics) {
        if (pics == null || pics.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String p : pics) {
            if (!first) {
                sb.append(";");
            }
            sb.append(p);
            first = false;
        }
        return sb.toString();
    }

    public static String joinPicsWithComma(Set<String> pics) {
        if (pics == null || pics.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String p : pics) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(p);
            first = false;
        }
        return sb.toString();
    }

    public static Set<String> splitPicsToSet(String text) {
        Set<String> result = new TreeSet<>();
        List<String> list = splitPics(text);
        for (String item : list) {
            result.add(item);
        }
        return result;
    }

    public static List<String> splitPics(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return result;
        }
        // Pisahkan berdasarkan titik koma atau koma
        String delimiter = text.contains(";") ? ";" : ",";
        String[] raw = text.split(delimiter, -1);

        for (int i = 0; i < raw.length; i++) {
            String trimmed = raw[i].trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    // Membersihkan karakter tidak aman untuk nama file menggunakan StringBuilder & charAt
    public static String sanitizeFileName(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "event";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean valid = (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '.' || c == '-';
            if (valid) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    public static LocalDate parseDate(String text) throws InvalidDataException {
        if (text == null || text.trim().isEmpty()) {
            throw new InvalidDataException("Tanggal tidak boleh kosong. Gunakan format dd-MM-yyyy (contoh: 25-12-2026).");
        }
        String trimmed = text.trim();
        try {
            return LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidDataException("Format tanggal salah: \"" + trimmed + "\". Gunakan format dd-MM-yyyy (contoh: 25-12-2026).");
        }
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    public static String padRight(String text, int length) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= length) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }
}