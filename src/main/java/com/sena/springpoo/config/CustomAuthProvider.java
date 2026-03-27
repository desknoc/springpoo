package com.sena.springpoo.config;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Proveedor de autenticación personalizado.
 * Autentica usando tipo_documento + documento + contraseña (BCrypt).
 * Lee el campo "tipo_documento" directamente del request HTTP.
 */
@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthProvider.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        // Spring Security envía el campo "username" → aquí llega el número de documento
        String documentoStr = authentication.getName();
        String contrasena   = authentication.getCredentials().toString();

        // Leemos tipo_documento del request HTTP actual
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String tipoDocumento = attrs.getRequest().getParameter("tipo_documento");

        // Validación básica de campos
        if (tipoDocumento == null || tipoDocumento.isBlank() || documentoStr.isBlank()) {
            logger.warn("[CustomAuthProvider] Intento de login con campos vacíos.");
            throw new BadCredentialsException("Todos los campos son obligatorios.");
        }

        long documento;
        try {
            documento = Long.parseLong(documentoStr);
        } catch (NumberFormatException e) {
            logger.warn("[CustomAuthProvider] Documento no numérico: {}", documentoStr);
            throw new BadCredentialsException("El documento debe ser numérico.");
        }

        // Buscar usuario en la BD por tipo_documento + documento
        Usuario usuario = PersistenceUsuario.getUsuarioByTipoYDocumento(tipoDocumento, documento);

        if (usuario == null) {
            logger.warn("[CustomAuthProvider] Login fallido: no existe usuario con tipo={} doc={}",
                    tipoDocumento, documento);
            throw new BadCredentialsException("Credenciales incorrectas.");
        }

        // Verificar contraseña con BCrypt
        if (!encoder.matches(contrasena, usuario.getContrasena())) {
            logger.warn("[CustomAuthProvider] Login fallido: contraseña incorrecta para doc={}", documento);
            throw new BadCredentialsException("Credenciales incorrectas.");
        }

        logger.info("[CustomAuthProvider] Login exitoso: tipo={} doc={} rol={}",
                tipoDocumento, documento, usuario.getRol());

        // Construir el token autenticado con el rol del usuario (ROLE_ es prefijo obligatorio de Spring Security)
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        return new UsernamePasswordAuthenticationToken(
                usuario.getDocumento().toString(), null, authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
