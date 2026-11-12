package eventtracker.io;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import eventtracker.exception.InvalidDataException;

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

    public static String joinPics(String[] pics) {
        if (pics == null || pics.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pics.length; i++) {
            if (i > 0) {
                sb.append(";");
            }
            sb.append(pics[i]);
        }
        return sb.toString();
    }

    public static String joinPicsWithComma(String[] pics) {
        if (pics == null || pics.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pics.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(pics[i]);
        }
        return sb.toString();
    }

    public static String[] splitPics(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new String[0];
        }
        // Pisahkan berdasarkan titik koma atau koma
        String delimiter = text.contains(";") ? ";" : ",";
        String[] raw = text.split(delimiter, -1);

        int validCount = 0;
        for (int i = 0; i < raw.length; i++) {
            if (!raw[i].trim().isEmpty()) {
                validCount++;
            }
        }

        String[] result = new String[validCount];
        int idx = 0;
        for (int i = 0; i < raw.length; i++) {
            String trimmed = raw[i].trim();
            if (!trimmed.isEmpty()) {
                result[idx++] = trimmed;
            }
        }
        return result;
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
