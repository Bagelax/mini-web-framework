package exceptions;

public class HandlerInvalidException extends Exception {

    public HandlerInvalidException(String reason) {
        super("Handler for route is invalid, " + reason);
    }

}
