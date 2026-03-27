package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequestMapping("/crud")
public class CrudController {


    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "crud";
    }


    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario) {
        boolean creado = PersistenceUsuario.save(usuario);
        if (!creado) {
            return "error/500";
        }
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
            return ResponseEntity.notFound().build(); // 404
        }

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
        // LOG DE CONTROL: Mira tu consola de IntelliJ/Eclipse al presionar guardar
        System.out.println("Intentando actualizar ID: " + id);
        System.out.println("Nombre recibido: " + usuario.getPrimerNombre());
        System.out.println("Documento recibido: " + usuario.getDocumento());

        usuario.setIdUsuario(id);

        // Verificamos si la persistencia realmente falla
        boolean actualizado = PersistenceUsuario.update(usuario);
        System.out.println("Resultado de la BD: " + actualizado);

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
        boolean eliminado = PersistenceUsuario.delete(id);

        if (eliminado) {
            return ResponseEntity.ok(
                    idioma.contains("es") ? "Usuario eliminado" : "User deleted"
            );
        } else {
            return ResponseEntity.status(404).body(
                    idioma.contains("es") ? "Usuario no encontrado" : "User not found"
            );
        }
    }


    @GetMapping("/eliminar/{id}")
    public String eliminarVista(@PathVariable long id) {
        PersistenceUsuario.delete(id);
        return "redirect:/crud";
    }


    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam long id, Model model) {
        Usuario usuario = PersistenceUsuario.getUsuarioById(id);

        if (usuario == null) {
            return "error/404";
        }

        model.addAttribute("usuarioEncontrado", usuario);
        model.addAttribute("usuarios", PersistenceUsuario.getUsuarios());

        return "crud";
    }

}