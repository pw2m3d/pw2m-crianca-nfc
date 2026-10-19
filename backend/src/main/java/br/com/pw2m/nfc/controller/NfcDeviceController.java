package br.com.pw2m.nfc.controller;

import br.com.pw2m.nfc.dto.NfcDtos;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.service.CurrentUserService;
import br.com.pw2m.nfc.service.NfcDeviceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/children/{childId}/devices")
public class NfcDeviceController {

    private final NfcDeviceService devices;
    private final CurrentUserService currentUser;

    public NfcDeviceController(
            NfcDeviceService devices,
            CurrentUserService currentUser
    ) {
        this.devices = devices;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<NfcDtos.DeviceResponse> list(
            @PathVariable Long childId,
            Authentication authentication
    ) {
        UserAccount owner = currentUser.require(authentication);
        return devices.list(childId, owner);
    }

    @PatchMapping("/{deviceId}/toggle")
    public NfcDtos.DeviceResponse toggle(
            @PathVariable Long childId,
            @PathVariable Long deviceId,
            Authentication authentication
    ) {
        return devices.toggle(
                childId,
                deviceId,
                currentUser.require(authentication)
        );
    }
}
