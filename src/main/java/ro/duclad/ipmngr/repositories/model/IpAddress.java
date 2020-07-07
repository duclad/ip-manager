package ro.duclad.ipmngr.repositories.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class IpAddress {

    @Id
    private Long id;
    private String value;
    @Enumerated(EnumType.ORDINAL)
    private IpAddressState state;
    @ManyToOne
    @JoinColumn(name = "IP_POOL_ID")
    private IpPool pool;

}
