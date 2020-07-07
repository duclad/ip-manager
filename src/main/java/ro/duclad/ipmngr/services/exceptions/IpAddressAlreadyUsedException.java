package ro.duclad.ipmngr.services.exceptions;

public class IpAddressAlreadyUsedException extends RuntimeException {
    public IpAddressAlreadyUsedException(String ipAddress, String state) {
        super("Ip Address " + ipAddress + " is already " + state);
    }
}
