package com.app.devchallenges.Controller;

import com.app.devchallenges.DTO.AuthRequest;
import com.app.devchallenges.DTO.AuthResponse;
import com.app.devchallenges.DTO.RegisterRequest;
import com.app.devchallenges.Entity.User;
import com.app.devchallenges.Security.AuthenticatedUser;
import com.app.devchallenges.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/usernames")
    public List<String> getAllUsernames() {
        return authService.getAllUsernames();
    }

    @PostMapping("/admin/provision")
    public ResponseEntity<AuthResponse> provisionUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.provisionUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/user-data")
    public Map<String, String> getMyData(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        Map<String, String> map = new HashMap<>();
        map.put("username", authenticatedUser.getUsername());
        map.put("userId", authenticatedUser.getUserId().toString());

        return map;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to provision users");
    }
}
