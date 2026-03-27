package com.sena.springpoo.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase de apoyo temporal para generar hashes BCrypt.
 *
 * CÓMO USAR:
 *   1. Ejecuta este main (click derecho → Run en IntelliJ/Eclipse).
 *   2. Copia el hash generado en la consola.
 *   3. Actualiza la contraseña en la BD:
 *        UPDATE usuario SET contrasena = '<hash_copiado>' WHERE documento = <tu_documento>;
 *   4. Borra o comenta este archivo antes de ir a producción.
 *
 * IMPORTANTE: Debes hacer esto para CADA usuario existente antes de arrancar
 * la app con Spring Security activo, ya que las contraseñas en texto plano
 * no serán reconocidas por BCrypt.
 */
public class GeneradorBCrypt {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // ── Cambia estas contraseñas por las de tus usuarios reales ──
        String[] contrasenasOriginales = {
            "admin123",
            "sena2025",
            "password"
        };

        System.out.println("=== Hashes BCrypt generados ===");
        for (String pass : contrasenasOriginales) {
            String hash = encoder.encode(pass);
            System.out.println("Original : " + pass);
            System.out.println("BCrypt   : " + hash);
            System.out.println("---");
        }

        // Verifica que el hash funciona
        System.out.println("=== Verificación ===");
        String hash = encoder.encode("admin123");
        System.out.println("¿'admin123' coincide con su hash? → " + encoder.matches("admin123", hash));
    }
}
