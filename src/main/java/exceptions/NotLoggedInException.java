package exceptions;

public class NotLoggedInException extends Exception {
    public NotLoggedInException() {
        super("You need to be logged in to do that");
    }
}
