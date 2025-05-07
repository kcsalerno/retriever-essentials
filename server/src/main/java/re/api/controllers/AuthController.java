package re.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import re.api.models.AppUser;
import re.api.security.JwtConverter;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtConverter jwtConverter;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtConverter jwtConverter,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtConverter = jwtConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>("Email and password are required.", HttpStatus.BAD_REQUEST);
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            if (authentication.isAuthenticated()) {
                AppUser user = (AppUser) authentication.getPrincipal();
                String jwt = jwtConverter.getTokenFromUser(user);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("token", jwt);
                responseBody.put("email", user.getUsername());
                responseBody.put("role", user.getAuthorities().iterator().next().getAuthority());

                return ResponseEntity.ok(responseBody);
            }
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>("Invalid credentials.", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PostMapping("/re-auth")
    public ResponseEntity<?> reAuthenticate(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(email, password);

        try {
            authenticationManager.authenticate(token);
            return ResponseEntity.ok().build();
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@AuthenticationPrincipal AppUser user) {
        String jwt = jwtConverter.getTokenFromUser(user);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", jwt);
        responseBody.put("email", user.getUsername());
        responseBody.put("role", user.getAuthorities().iterator().next().getAuthority());

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/encode")
    public ResponseEntity<Void> encode(@RequestBody Map<String, String> body) {
        String raw = body.get("value");
        String hashed = passwordEncoder.encode(raw);
        System.out.println("\nENCODED: " + hashed + "\n");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
