package ro.duclad.ipmngr.services.exceptions;

public class IpAddressNotFoundInPoolException extends RuntimeException {
    public IpAddressNotFoundInPoolException(String ipAddress, Long poolId) {
        super("Ip Address " + ipAddress + " not found in pool with id " + poolId);
    }
}
