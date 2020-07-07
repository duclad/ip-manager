package ro.duclad.ipmngr;

import inet.ipaddr.IPAddressString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class IpAddressConverterTests {

    @Test
    public void convertToNumbers(){
        log.info("10.70.26.1 -> {}",new IPAddressString("10.70.26.1").getAddress().toIPv6().getValue());
        log.info("10.70.26.100 -> {}",new IPAddressString("10.70.26.100").getAddress().toIPv6().getValue());
        log.info("10.70.25.0 -> {}",new IPAddressString("10.70.25.0").getAddress().toIPv6().getValue());
        log.info("10.70.25.255 -> {}",new IPAddressString("10.70.25.255").getAddress().toIPv6().getValue());
        log.info("10.50.0.0 -> {}",new IPAddressString("10.50.0.0").getAddress().toIPv6().getValue());
        log.info("10.50.255.255 -> {}",new IPAddressString("10.50.255.255").getAddress().toIPv6().getValue());
    }
}
