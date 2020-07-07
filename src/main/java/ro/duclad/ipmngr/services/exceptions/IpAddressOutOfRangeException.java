package ro.duclad.ipmngr.services.exceptions;

public class IpAddressOutOfRangeException extends RuntimeException {
    public IpAddressOutOfRangeException(String ipAddress, String lowerBound, String upperBound) {
        super("Ip Address " + ipAddress + " out of range " + lowerBound + " - " + upperBound);
    }
}
