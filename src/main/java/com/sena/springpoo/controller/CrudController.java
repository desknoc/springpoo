package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.models.UsuarioSesion;
import com.sena.springpoo.persistence.PersistenceUsuario;
import com.sena.springpoo.service.N8nService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Controller
@RequestMapping("/crud")
public class CrudController {

    private static final Logger logger = LoggerFactory.getLogger(CrudController.class);
    private final N8nService n8nService;

    public CrudController(N8nService n8nService) {
        this.n8nService = n8nService;
    }


    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();

        // Si la conexión falló, PersistenceUsuario.getUsuarios()
        // podría devolver una lista vacía o null dependiendo de la lógica.
        // Basado en el código, si conn == null, devuelve una lista vacía.
        // Para detectar error de conexión real, validaremos si la conexión es nula.
        if (usuarios == null) {
            logger.error("[CrudController.listar] No se pudo obtener la lista de usuarios. Fallo de conexión a la BD.");
            return "error/500";
        }

        model.addAttribute("usuarios", usuarios);

        // ══════════════════════════════════════════════════════════════
        //  NUEVO: Cargar usuarios con sesión activa (INNER JOIN).
        //
        //  ¿Qué hace?
        //  Ejecuta un INNER JOIN entre las tablas "usuario" y "sesion"
        //  para obtener SOLO los usuarios que tienen al menos un registro
        //  de inicio de sesión. El resultado se inyecta al modelo de
        //  Thymeleaf como "usuariosSesion" para renderizar la tabla
        //  en crud.html.
        //
        //  Si la consulta falla, la lista será null y Thymeleaf mostrará
        //  el mensaje "No hay usuarios con sesión activa" en vez de la tabla.
        // ══════════════════════════════════════════════════════════════
        List<UsuarioSesion> sesiones = PersistenceUsuario.getUsuariosConSesion();
        model.addAttribute("usuariosSesion", sesiones);

        logger.info("[CrudController.listar] Listado cargado correctamente. {} usuarios encontrados.", usuarios.size());
        return "crud";
    }


    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        
        // Hashear la contraseña antes de guardar en la BD
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        
        boolean creado = PersistenceUsuario.save(usuario);
        if (!creado) {
            logger.error("[CrudController.crearUsuario] Error al insertar el usuario en la BD.");
            return "error/500";
        }
        
        // Notificar a n8n
        n8nService.notificarCambioAsync("CREATE", usuario);

        logger.info("[CrudController.crearUsuario] Usuario '{}' creado correctamente.", usuario.getCorreo());
        return "redirect:/crud";
    }


    // ── Devuelve un usuario como JSON para pre-llenar el modal de edición ──
    @GetMapping("/usuario/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> obtenerUsuario(
            @PathVariable long id
    ) {
        Usuario usuario = PersistenceUsuario.getUsuarioById(id);

        if (usuario == null) {
            logger.warn("[CrudController.obtenerUsuario] No se encontró usuario con ID {}.", id);
            return ResponseEntity.notFound().build(); // 404
        }

        logger.info("[CrudController.obtenerUsuario] Usuario ID {} encontrado.", id);
        return ResponseEntity.ok(usuario); // 200 + JSON
    }


    // ── Actualiza los datos de un usuario desde el modal ──
    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable long id,
            @RequestBody Usuario usuario,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ) {
        // LOG DE CONTROL
        logger.info("[CrudController.actualizarUsuario] Intentando actualizar ID: {}", id);
        logger.info("[CrudController.actualizarUsuario] Nombre recibido: {}", usuario.getPrimerNombre());
        logger.info("[CrudController.actualizarUsuario] Documento recibido: {}", usuario.getDocumento());

        usuario.setIdUsuario(id);
        
        // Hashear la contraseña SOLAMENTE si cambió (es decir, si no es igual al BCrypt que ya estaba en la BD)
        Usuario existente = PersistenceUsuario.getUsuarioById(id);
        if (existente != null && usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()) {
            if (!usuario.getContrasena().equals(existente.getContrasena())) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                usuario.setContrasena(encoder.encode(usuario.getContrasena()));
                logger.info("[CrudController.actualizarUsuario] La contraseña fue modificada y re-hasheada.");
            }
        }

        boolean actualizado = PersistenceUsuario.update(usuario);
        logger.info("[CrudController.actualizarUsuario] Resultado de la BD: {}", actualizado);

        if (!actualizado) {
            logger.error("[CrudController.actualizarUsuario] Fallo al actualizar el usuario con ID {}.", id);
        } else {
            // Notificar a n8n
            n8nService.notificarCambioAsync("UPDATE", usuario);
        }

        String mensaje = idioma.contains("es")
                ? (actualizado ? "Usuario actualizado correctamente" : "Error en BD")
                : (actualizado ? "User updated" : "DB Error");

        return actualizado
                ? ResponseEntity.ok(mensaje)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensaje);
    }


    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ) {
        Usuario usuario = PersistenceUsuario.getUsuarioById(id);
        boolean eliminado = PersistenceUsuario.delete(id);

        if (eliminado) {
            logger.info("[CrudController.eliminarUsuario] Usuario con ID {} eliminado correctamente.", id);
            if (usuario != null) {
                n8nService.notificarCambioAsync("DELETE", usuario);
            }
            return ResponseEntity.ok(
                    idioma.contains("es") ? "Usuario eliminado" : "User deleted"
            );
        } else {
            logger.warn("[CrudController.eliminarUsuario] No se encontró el usuario con ID {} para eliminar.", id);
            return ResponseEntity.status(404).body(
                    idioma.contains("es") ? "Usuario no encontrado" : "User not found"
            );
        }
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarVista(@PathVariable long id) {
        Usuario usuario = PersistenceUsuario.getUsuarioById(id);
        boolean eliminado = PersistenceUsuario.delete(id);
        if (!eliminado) {
            logger.error("[CrudController.eliminarVista] No se pudo eliminar el usuario con ID {} desde la vista.", id);
            return "error/500";
        }
        if (usuario != null) {
            n8nService.notificarCambioAsync("DELETE", usuario);
        }
        logger.info("[CrudController.eliminarVista] Usuario ID {} eliminado correctamente desde la vista.", id);
        return "redirect:/crud";
    }


    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam long id, Model model) {
        Usuario usuario = PersistenceUsuario.getUsuarioById(id);

        if (usuario == null) {
            logger.warn("[CrudController.buscarUsuario] No se encontró usuario con ID {}.", id);
            return "error/500";
        }

        logger.info("[CrudController.buscarUsuario] Usuario ID {} encontrado.", id);
        model.addAttribute("usuarioEncontrado", usuario);
        model.addAttribute("usuarios", PersistenceUsuario.getUsuarios());

        return "crud";
    }

}