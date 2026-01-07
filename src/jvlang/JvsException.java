package jvlang;

public class JvsException extends RuntimeException {

    public JvsException() {
    }

    public JvsException(String message) {
        super(message);
    }

    public JvsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JvsException(Throwable cause) {
        super(cause);
    }

    public JvsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
