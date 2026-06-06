package com.openlib.service;

import com.openlib.model.User;
import com.openlib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Autentica con email + contraseña.
     * @return el User si las credenciales son correctas, empty si no.
     */
    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email.trim())
                .filter(u -> u.getPassword().equals(password));
    }

    /**
     * Registra un nuevo usuario BUYER.
     * @return el User creado, o empty si el email ya existe.
     */
    public Optional<User> register(String name, String email, String password) {
        if (userRepository.findByEmail(email.trim()).isPresent()) {
            return Optional.empty();
        }
        User user = User.builder()
                .name(name.trim())
                .email(email.trim())
                .password(password)
                .role("BUYER")
                .build();
        return Optional.of(userRepository.save(user));
    }

    /** Lista todos los usuarios. */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Busca usuario por ID. */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /** Elimina un usuario por ID. Devuelve true si existía. */
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    /** Estadísticas: total de compradores */
    public long countBuyers() {
        return userRepository.findAll().stream()
                .filter(u -> "BUYER".equals(u.getRole()))
                .count();
    }
}
