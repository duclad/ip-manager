package ro.duclad.ipmngr.services;

import inet.ipaddr.IPAddressString;
import inet.ipaddr.ipv6.IPv6Address;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.duclad.ipmngr.repositories.IpAddressRepository;
import ro.duclad.ipmngr.repositories.IpPoolRepository;
import ro.duclad.ipmngr.repositories.model.IpAddress;
import ro.duclad.ipmngr.repositories.model.IpAddressState;
import ro.duclad.ipmngr.repositories.model.IpPool;
import ro.duclad.ipmngr.services.exceptions.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IpAddressManagementService {

    private final IpPoolRepository ipPoolRepository;
    private final IpAddressRepository ipAddressRepository;

    @Transactional
    public List<String> reserveDynamicIpAddresses(long poolId, int numberOfAddresses) {
        IpPool ipPool = ipPoolRepository.findById(poolId).orElseThrow(PoolNotFoundException::new);

        long poolCapacity = ipPool.getUpperBound() - ipPool.getLowerBound();
        long poolUsage = ipAddressRepository.countAllByPool(ipPool);
        if (poolCapacity - poolUsage < numberOfAddresses) {
            throw new InsufficientPoolCapacityException(poolCapacity - poolUsage, numberOfAddresses);
        }

        Optional<IpAddress> lastUsedAddress = ipAddressRepository.findTop1ByPoolOrderByIdDesc(ipPool);

        long firstUnusedIpAddress = ipPool.getLowerBound();
        if (lastUsedAddress.isPresent()) {
            firstUnusedIpAddress = lastUsedAddress.get().getId() + 1;
        }
        List<String> allocatedIpAddresses = new ArrayList<>();
        long numberOfIterations = Math.min(numberOfAddresses, ipPool.getUpperBound() - firstUnusedIpAddress);
        for (long i = 0; i <= numberOfIterations; i++) {
            IpAddress address = new IpAddress();
            address.setPool(ipPool);
            address.setState(IpAddressState.RESERVED);
            address.setId(firstUnusedIpAddress + i);
            address.setValue(new IPv6Address(BigInteger.valueOf(firstUnusedIpAddress + i)).toIPv4().toAddressString().toString());
            ipAddressRepository.save(address);
            allocatedIpAddresses.add(address.getValue());
        }

        if (allocatedIpAddresses.size() < numberOfAddresses) {
            List<Pair<Long, Long>> gaps = new ArrayList<>();
            ipAddressRepository.findTop1ByPoolOrderByIdAsc(ipPool).ifPresent(ipAddress -> {
                if (ipPool.getLowerBound() < ipAddress.getId()) {
                    gaps.add(Pair.of(ipPool.getLowerBound(), ipAddress.getId() - 1));
                }
            });
            ipAddressRepository.findGapsInUsedIpAddressesFromPool(poolId)
                    .stream().map(s -> {
                if (s.contains(":")) {
                    return Pair.of(Long.valueOf(StringUtils.substringBefore(s, ":")), Long.valueOf(StringUtils.substringAfter(s, ":")));
                } else {
                    return Pair.of(Long.valueOf(s), Long.valueOf(s));
                }
            }).forEach(gaps::add);


            for (int i = 0; i < gaps.size() && allocatedIpAddresses.size() < numberOfAddresses; i++) {
                Pair<Long, Long> longLongPair = gaps.get(i);
                for (long j = longLongPair.getLeft(); j <= longLongPair.getRight(); j++) {
                    IpAddress address = new IpAddress();
                    address.setPool(ipPool);
                    address.setState(IpAddressState.RESERVED);
                    address.setId(firstUnusedIpAddress);
                    address.setValue(new IPv6Address(BigInteger.valueOf(firstUnusedIpAddress)).toIPv4().toAddressString().toString());
                    ipAddressRepository.save(address);
                    allocatedIpAddresses.add(address.getValue());
                    if (allocatedIpAddresses.size() == numberOfAddresses) break;
                }
            }
        }
        if (allocatedIpAddresses.size() < numberOfAddresses) {
            throw new InsufficientPoolCapacityException(poolCapacity - poolUsage, numberOfAddresses);
        }
        return allocatedIpAddresses;
    }

    @Transactional
    public void allocateStaticIpAddress(long poolId, String ipAddress, IpAddressState state) {
        IPAddressString ipAddressString = new IPAddressString(ipAddress);
        if (!ipAddressString.isIPAddress()) {
            throw new MalformedIpAddressException(ipAddress);
        }
        IpPool pool = ipPoolRepository.findById(poolId).orElseThrow(PoolNotFoundException::new);
        Long addressId = ipAddressString.getAddress().toIPv6().getValue().longValue();
        if (addressId < pool.getLowerBound() || addressId > pool.getUpperBound()) {
            throw new IpAddressOutOfRangeException(ipAddress,
                    new IPv6Address(BigInteger.valueOf(pool.getLowerBound())).toIPv4().toAddressString().toString(),
                    new IPv6Address(BigInteger.valueOf(pool.getUpperBound())).toIPv4().toAddressString().toString());
        }
        ipAddressRepository.findByPoolAndId(pool, addressId).ifPresent(ipAddress1 -> {
            throw new IpAddressAlreadyUsedException(ipAddress, ipAddress1.getState().name());
        });
        IpAddress address = new IpAddress();
        address.setPool(pool);
        address.setState(state);
        address.setId(addressId);
        address.setValue(ipAddress);
        ipAddressRepository.save(address);
    }

    @Transactional
    public void releaseIpAddress(long poolId, String ipAddress) {
        IPAddressString ipAddressString = new IPAddressString(ipAddress);
        if (!ipAddressString.isIPAddress()) {
            throw new MalformedIpAddressException(ipAddress);
        }
        IpPool pool = ipPoolRepository.findById(poolId).orElseThrow(PoolNotFoundException::new);
        Long addressId = ipAddressString.getAddress().toIPv6().getValue().longValue();
        IpAddress address = ipAddressRepository.findByPoolAndId(pool, addressId).orElseThrow(() -> new IpAddressNotFoundInPoolException(ipAddress, poolId));
        ipAddressRepository.delete(address);
    }

    @Transactional
    public IpAddress getIpAddress(long poolId, String ipAddress) {
        IPAddressString ipAddressString = new IPAddressString(ipAddress);
        if (!ipAddressString.isIPAddress()) {
            throw new MalformedIpAddressException(ipAddress);
        }
        IpPool pool = ipPoolRepository.findById(poolId).orElseThrow(PoolNotFoundException::new);
        Long addressId = ipAddressString.getAddress().toIPv6().getValue().longValue();
        if (addressId < pool.getLowerBound() || addressId > pool.getUpperBound()) {
            throw new IpAddressOutOfRangeException(ipAddress,
                    new IPv6Address(BigInteger.valueOf(pool.getLowerBound())).toIPv4().toAddressString().toString(),
                    new IPv6Address(BigInteger.valueOf(pool.getUpperBound())).toIPv4().toAddressString().toString());
        }
        IpAddress address = ipAddressRepository.findByPoolAndId(pool, addressId).orElseThrow(() -> new IpAddressNotFoundInPoolException(ipAddress, poolId));
        return address;
    }
}
