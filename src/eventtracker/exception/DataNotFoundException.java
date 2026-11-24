package eventtracker.exception;

// Exception khusus untuk data yang tidak ditemukan.
public class DataNotFoundException extends Exception {
    // Constructor inisialisasi pesan error untuk data yang tidak ditemukan.
    public DataNotFoundException(String message) {
        super(message);
    }
}
