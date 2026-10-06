package br.com.pw2m.nfc.controller;

import br.com.pw2m.nfc.dto.ChildDtos;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.service.ChildService;
import br.com.pw2m.nfc.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildService children;
    private final CurrentUserService currentUser;

    public ChildController(
            ChildService children,
            CurrentUserService currentUser
    ) {
        this.children = children;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<ChildDtos.ChildSummaryResponse> list(Authentication authentication) {
        UserAccount owner = currentUser.require(authentication);
        return children.list(owner);
    }

    @GetMapping("/{id}")
    public ChildDtos.ChildResponse get(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return children.get(id, currentUser.require(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildDtos.ChildResponse create(
            @Valid @RequestBody ChildDtos.ChildRequest request,
            Authentication authentication
    ) {
        return children.create(request, currentUser.require(authentication));
    }

    @PutMapping("/{id}")
    public ChildDtos.ChildResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ChildDtos.ChildRequest request,
            Authentication authentication
    ) {
        return children.update(id, request, currentUser.require(authentication));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        children.delete(id, currentUser.require(authentication));
    }
}
