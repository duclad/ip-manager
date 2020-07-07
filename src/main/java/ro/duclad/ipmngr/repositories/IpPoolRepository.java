package ro.duclad.ipmngr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.duclad.ipmngr.repositories.model.IpPool;

@Repository
public interface IpPoolRepository extends JpaRepository<IpPool, Long> {
}
