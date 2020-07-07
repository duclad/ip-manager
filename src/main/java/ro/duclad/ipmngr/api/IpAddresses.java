package ro.duclad.ipmngr.api;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pools/{poolId}/ip-addresses")
public class IpAddresses {

    @PostMapping("/dynamic")
    public List<String> reserveDynamicIp(@PathVariable long poolId, @RequestParam(value = "numberOfIpAddresses") int numberOfIpAddresses){
        return List.of("");
    }



}
