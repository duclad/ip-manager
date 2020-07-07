package ro.duclad.ipmngr.services.exceptions;

public class MalformedIpAddressException extends RuntimeException {
    public MalformedIpAddressException(String ipAddress) {
        super(ipAddress + " is not a valid ip address");
    }
}
