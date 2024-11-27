package common.exception;

public class UnknownMessageException extends RuntimeException {

    public UnknownMessageException() {
        super("Unknown message type");
    }

}
