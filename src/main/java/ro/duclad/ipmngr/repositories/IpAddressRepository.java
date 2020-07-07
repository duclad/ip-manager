package ro.duclad.ipmngr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.duclad.ipmngr.repositories.model.IpAddress;
import ro.duclad.ipmngr.repositories.model.IpPool;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {

    /**
     * Return ip addresses usage number for a pool
     * @param pool
     * @return
     */
    Long countAllByPool(IpPool pool);

    /**
     * Get an address from a specific ip pool
     * @param pool
     * @param id
     * @return
     */
    Optional<IpAddress> findByPoolAndId(IpPool pool, Long id);

    @Query(nativeQuery = true,
            value = "select " +
                    " case when next_id-id=2 then to_char(id+1) else to_char(id+1) || ':' || to_char(next_id-1) end gap " +
                    "from (select id,IP_POOL_ID, LEAD(id, 1) OVER (ORDER BY id) as next_id from IP_ADDRESS) list " +
                    " where next_id-id>1 and IP_POOL_ID=:poolId")
    List<String> findGapsInUsedIpAddressesFromPool(@Param("poolId") long poolId);

    Optional<IpAddress> findTop1ByPoolOrderByIdDesc(IpPool pool);
    Optional<IpAddress> findTop1ByPoolOrderByIdAsc(IpPool pool);

}
