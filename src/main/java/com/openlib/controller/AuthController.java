package com.openlib.controller;

import com.openlib.model.User;
import com.openlib.util.ApiClient;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

import java.util.Map;

public class AuthController {

    private final DataStore store = DataStore.getInstance();

    /**
     * Valida credenciales contra el API REST.
     * @return mensaje de error, o null si el login fue exitoso.
     */
    public String login(String email, String password) {
        if (email == null || email.isBlank()) return "El correo es requerido.";
        if (password == null || password.isBlank()) return "La contraseña es requerida.";

        try {
            // Llamada al API: POST /api/users/login
            Map<String, String> body = Map.of("email", email.trim(), "password", password);
            User user = ApiClient.post("/users/login", body, User.class);

            store.setCurrentUser(user);
            if ("ADMIN".equals(user.getRole())) {
                SceneManager.getInstance().showAdminDashboard();
            } else {
                SceneManager.getInstance().showBuyerDashboard();
            }
            return null;

        } catch (Exception e) {
            // 401 → credenciales incorrectas
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                return "Credenciales incorrectas. Intenta de nuevo.";
            }
            return "Error de conexión con el servidor. ¿Está el backend corriendo?";
        }
    }

    /**
     * Registra un nuevo usuario a través del API.
     * @return mensaje de error, o null si el registro fue exitoso.
     */
    public String register(String name, String email, String password, String confirm) {
        if (name == null || name.isBlank()) return "El nombre es requerido.";
        if (email == null || email.isBlank()) return "El correo es requerido.";
        if (!email.contains("@")) return "Correo inválido.";
        if (password == null || password.length() < 6) return "La contraseña debe tener al menos 6 caracteres.";
        if (!password.equals(confirm)) return "Las contraseñas no coinciden.";

        try {
            Map<String, String> body = Map.of(
                    "name", name.trim(),
                    "email", email.trim(),
                    "password", password
            );
            ApiClient.post("/users", body, User.class);
            SceneManager.getInstance().showLogin();
            return null;

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("409")) {
                return "Ya existe una cuenta con ese correo.";
            }
            return "Error de conexión con el servidor. ¿Está el backend corriendo?";
        }
    }

    public void logout() {
        store.logout();
        SceneManager.getInstance().showLogin();
    }
}
