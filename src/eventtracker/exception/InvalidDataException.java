package eventtracker.exception;

// Exception khusus untuk input kosong, invalid, atau duplikat.
public class InvalidDataException extends Exception {
    // Constructor inisialisasi pesan error untuk input kosong, invalid, atau duplikat.
    public InvalidDataException(String message) {
        super(message);
    }
}
