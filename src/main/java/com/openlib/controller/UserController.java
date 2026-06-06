package com.openlib.controller;

import com.openlib.model.User;
import com.openlib.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** GET /api/users — todos los usuarios (admin) */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /** GET /api/users/{id} — usuario por ID */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/users/login — autenticar usuario.
     * Body: { "email": "...", "password": "..." }
     * Respuesta: el objeto User si OK, 401 si credenciales incorrectas.
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest req) {
        return userService.authenticate(req.getEmail(), req.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    /**
     * POST /api/users — registrar nuevo usuario BUYER.
     * Body: { "name": "...", "email": "...", "password": "..." }
     * Respuesta: User creado, o 409 si el email ya existe.
     */
    @PostMapping
    public ResponseEntity<User> register(@RequestBody RegisterRequest req) {
        return userService.register(req.getName(), req.getEmail(), req.getPassword())
                .map(u -> ResponseEntity.status(201).body(u))
                .orElse(ResponseEntity.status(409).build());
    }

    /** DELETE /api/users/{id} — eliminar usuario (admin) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /** GET /api/users/stats — estadísticas de usuarios */
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return Map.of(
                "totalBuyers", userService.countBuyers(),
                "total", (long) userService.getAllUsers().size()
        );
    }

    // ---- Request DTOs ----

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
    }
}
