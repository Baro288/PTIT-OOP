package vn.edu.ptit.oop.nhom05.exception;

public class HighScoreException extends Exception {

    public HighScoreException(String message) {
        super(message);
    }

    public HighScoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
