package br.com.pw2m.nfc.controller;

import br.com.pw2m.nfc.dto.NfcInventoryDtos;
import br.com.pw2m.nfc.service.DeviceInventoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/devices")
@SecurityRequirement(name = "bearerAuth")
public class AdminNfcDeviceController {

    private final DeviceInventoryService inventory;

    public AdminNfcDeviceController(
            DeviceInventoryService inventory
    ) {
        this.inventory = inventory;
    }

    @PostMapping("/manufacture")
    @ResponseStatus(HttpStatus.CREATED)
    public List<NfcInventoryDtos.ManufacturedDeviceResponse>
    manufacture(
            @Valid
            @RequestBody
            NfcInventoryDtos.ManufactureBatchRequest request
    ) {

        return inventory.manufactureBatch(
                request.quantity(),
                request.category()
        );
    }
}