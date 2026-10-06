package br.com.pw2m.nfc.controller;

import br.com.pw2m.nfc.dto.ChildDtos;
import br.com.pw2m.nfc.service.PublicChildService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/children")
public class PublicChildController {

    private final PublicChildService children;

    public PublicChildController(PublicChildService children) {
        this.children = children;
    }

    @GetMapping("/{publicToken}")
    public ChildDtos.PublicChildResponse get(
            @PathVariable String publicToken
    ) {
        return children.get(publicToken);
    }

    @GetMapping("/{publicToken}/device/{deviceToken}")
    public ChildDtos.PublicChildResponse getByDevice(
            @PathVariable String publicToken,
            @PathVariable String deviceToken
    ) {
        return children.getByDevice(publicToken, deviceToken);
    }
}
