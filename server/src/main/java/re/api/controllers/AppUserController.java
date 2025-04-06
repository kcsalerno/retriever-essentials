package re.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import re.api.security.AppUserService;
import re.api.domain.Result;
import re.api.models.AppUser;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class AppUserController {

    private final AppUserService service;

    public AppUserController(AppUserService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppUser> findAll() {
        return service.findAll();
    }

    @GetMapping("/{appUserId}")
    public ResponseEntity<AppUser> findById(@PathVariable int appUserId) {
        AppUser user = service.findById(appUserId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        user.setPassword(""); // Do not expose password
        return ResponseEntity.ok(user);
    }

    @PutMapping("/password")
    public ResponseEntity<Object> changePassword(
            @RequestBody HashMap<String, String> body,
            @AuthenticationPrincipal AppUser principal) {

        if (!body.containsKey("password")) {
            return new ResponseEntity<>("Password is required.", HttpStatus.BAD_REQUEST);
        }

        String newPassword = body.get("password");
        principal.setPassword(newPassword);

        Result<AppUser> result = service.changePassword(principal);
        if (!result.isSuccess()) {
            return new ResponseEntity<>(result.getMessages(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/enable/{appUserId}")
    public ResponseEntity<Void> enableById(@PathVariable int appUserId) {
        Result<AppUser> result = service.enableById(appUserId);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/disable/{appUserId}")
    public ResponseEntity<Void> disableById(@PathVariable int appUserId) {
        Result<AppUser> result = service.disableById(appUserId);

        if (!result.isSuccess()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
