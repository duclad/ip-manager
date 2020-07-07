package ro.duclad.ipmngr.repositories.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class IpPool {
    @Id
    private Long id;
    private String description;
    private Long lowerBound;
    private Long upperBound;
}
