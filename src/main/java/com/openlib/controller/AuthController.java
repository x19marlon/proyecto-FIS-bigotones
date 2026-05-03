package com.openlib.controller;

import com.openlib.model.User;
import com.openlib.util.DataStore;
import com.openlib.util.SceneManager;

public class AuthController {

    private final DataStore store = DataStore.getInstance();

    /**
     * Validates credentials and navigates to the correct dashboard.
     * @return error message, or null if success
     */
    public String login(String email, String password) {
        if (email == null || email.isBlank()) return "El correo es requerido.";
        if (password == null || password.isBlank()) return "La contraseña es requerida.";

        return store.authenticate(email.trim(), password).map(user -> {
            store.setCurrentUser(user);
            if ("ADMIN".equals(user.getRole())) {
                SceneManager.getInstance().showAdminDashboard();
            } else {
                SceneManager.getInstance().showBuyerDashboard();
            }
            return (String) null;
        }).orElse("Credenciales incorrectas. Intenta de nuevo.");
    }

    /**
     * @return error message, or null if success
     */
    public String register(String name, String email, String password, String confirm) {
        if (name == null || name.isBlank()) return "El nombre es requerido.";
        if (email == null || email.isBlank()) return "El correo es requerido.";
        if (!email.contains("@")) return "Correo inválido.";
        if (password == null || password.length() < 6) return "La contraseña debe tener al menos 6 caracteres.";
        if (!password.equals(confirm)) return "Las contraseñas no coinciden.";

        boolean ok = store.registerUser(name.trim(), email.trim(), password);
        if (!ok) return "Ya existe una cuenta con ese correo.";

        SceneManager.getInstance().showLogin();
        return null;
    }

    public void logout() {
        store.logout();
        SceneManager.getInstance().showLogin();
    }
}
