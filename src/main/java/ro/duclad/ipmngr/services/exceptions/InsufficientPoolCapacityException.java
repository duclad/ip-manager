package ro.duclad.ipmngr.services.exceptions;

public class InsufficientPoolCapacityException extends RuntimeException {
    public InsufficientPoolCapacityException(long available, long requested) {
        super("Pool capacity  is " + available + " which is lower than requested number of ip addresses " + requested);
    }
}
