package ro.duclad.ipmngr.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ro.duclad.ipmngr.repositories.IpAddressRepository;
import ro.duclad.ipmngr.repositories.IpPoolRepository;
import ro.duclad.ipmngr.repositories.model.IpAddress;
import ro.duclad.ipmngr.repositories.model.IpAddressState;
import ro.duclad.ipmngr.repositories.model.IpPool;
import ro.duclad.ipmngr.services.exceptions.InsufficientPoolCapacityException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
class IpAddressManagementServiceTest {

    @Mock
    private IpAddressRepository ipAddressRepository;

    @Mock
    private IpPoolRepository ipPoolRepository;

    private IpPool pool;
    private IpPool pool2;
    private IpAddress ipAddress;
    private IpAddressManagementService ipAddressManagementService;

    @BeforeEach
    public void setUp() {
        pool = new IpPool();
        pool.setId(1L);
        pool.setDescription("test pool");
        pool.setLowerBound(281470852792320L);
        pool.setUpperBound(281470852857855L);
        pool2 = new IpPool();
        pool2.setId(1L);
        pool2.setDescription("test pool");
        pool2.setLowerBound(281470852792320L);
        pool2.setUpperBound(281470852792325L);

        ipAddress = new IpAddress();
        ipAddress.setId(281470852792323L);
        ipAddress.setValue("10.50.0.2");
        ipAddress.setState(IpAddressState.RESERVED);
        ipAddress.setPool(pool2);

        MockitoAnnotations.initMocks(this);
        ipAddressManagementService = new IpAddressManagementService(ipPoolRepository, ipAddressRepository);
    }

    @Test
    void reserveDynamicIpAddresses() {
        when(ipPoolRepository.findById(1L)).thenReturn(Optional.of(pool));

        List<String> ips = ipAddressManagementService.reserveDynamicIpAddresses(1, 10);
        ips.forEach(s -> log.info(s));
        assertEquals(10, ips.size());

    }

    @Test
    void reserveDynamicIpAddressesWillFail() {

        assertThrows(InsufficientPoolCapacityException.class, () -> {
            when(ipPoolRepository.findById(1L)).thenReturn(Optional.of(pool2));

            List<String> ips = ipAddressManagementService.reserveDynamicIpAddresses(1, 10);
            ips.forEach(s -> log.info(s));
        });

    }

    @Test
    void reserveDynamicIpAddressesFromAlreadyUsedPool() {
        when(ipPoolRepository.findById(1L)).thenReturn(Optional.of(pool2));
        when(ipAddressRepository.countAllByPool(pool2)).thenReturn(3L);
        when(ipAddressRepository.findTop1ByPoolOrderByIdDesc(pool2)).thenReturn(Optional.of(ipAddress));
        List<String> ips = ipAddressManagementService.reserveDynamicIpAddresses(1, 2);
        ips.forEach(s -> log.info(s));
        assertEquals(2, ips.size());

    }

}