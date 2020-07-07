package ro.duclad.ipmngr.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ro.duclad.ipmngr.services.IpAddressManagementService;

import java.util.List;

@RestController
@RequestMapping("/pools/{poolId}/ip-addresses")
public class IpAddresses {

    @Autowired
    private IpAddressManagementService ipAddressManagementService;

    @PostMapping("/dynamic")
    @ResponseStatus(HttpStatus.OK)
    public List<String> reserveDynamicIp(@PathVariable long poolId, @RequestParam(value = "numberOfIpAddresses") int numberOfIpAddresses){
        if (numberOfIpAddresses <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must request a positive number of ip addresses");
        }

        return ipAddressManagementService.reserveDynamicIpAddresses(poolId, numberOfIpAddresses);
    }



}
