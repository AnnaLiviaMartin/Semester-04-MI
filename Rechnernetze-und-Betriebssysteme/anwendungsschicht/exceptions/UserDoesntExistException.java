package exceptions;

public class UserDoesntExistException extends Exception {

    public UserDoesntExistException(String m) {
        super(m);
    }
}
