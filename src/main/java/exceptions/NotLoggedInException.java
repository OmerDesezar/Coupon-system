package exceptions;

public class NotLoggedInException extends Exception {
    public NotLoggedInException() {
        super("you need to be logged in to do that");
    }
}
