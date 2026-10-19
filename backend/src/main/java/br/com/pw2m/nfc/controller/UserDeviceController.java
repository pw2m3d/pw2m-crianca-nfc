package br.com.pw2m.nfc.controller;

import br.com.pw2m.nfc.dto.DeviceClaimDtos;
import br.com.pw2m.nfc.exception.BadRequestException;
import br.com.pw2m.nfc.service.AttemptLimiter;
import br.com.pw2m.nfc.service.UserDeviceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@SecurityRequirement(name = "bearerAuth")
public class UserDeviceController {

    private final UserDeviceService service;
    private final AttemptLimiter limiter;

    public UserDeviceController(UserDeviceService service, AttemptLimiter limiter) {
        this.service = service;
        this.limiter = limiter;
    }

    @PostMapping("/claim")
    @ResponseStatus(HttpStatus.OK)
    public DeviceClaimDtos.DeviceResponse claim(
            Authentication authentication,
            HttpServletRequest httpRequest,
            @Valid @RequestBody DeviceClaimDtos.ClaimRequest request
    ) {
        String key = "claim:" + clientIp(httpRequest) + ":" + authentication.getName();
        limiter.guard(key);
        try {
            DeviceClaimDtos.DeviceResponse response = service.claim(authentication.getName(), request);
            limiter.reset(key);
            return response;
        } catch (BadRequestException ex) {
            limiter.recordFailure(key);
            throw ex;
        }
    }

    @GetMapping
    public List<DeviceClaimDtos.DeviceResponse> list(Authentication authentication) {
        return service.listMyDevices(authentication.getName());
    }

    @GetMapping("/{deviceId}")
    public DeviceClaimDtos.DeviceResponse get(
            Authentication authentication,
            @PathVariable Long deviceId
    ) {
        return service.getMyDevice(authentication.getName(), deviceId);
    }

    @PutMapping("/{deviceId}/assign")
    public DeviceClaimDtos.DeviceResponse assign(
            Authentication authentication,
            @PathVariable Long deviceId,
            @Valid @RequestBody DeviceClaimDtos.AssignRequest request
    ) {
        return service.assign(authentication.getName(), deviceId, request);
    }

    @PutMapping("/{deviceId}/unassign")
    public DeviceClaimDtos.DeviceResponse unassign(
            Authentication authentication,
            @PathVariable Long deviceId
    ) {
        return service.unassign(authentication.getName(), deviceId);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }
}
