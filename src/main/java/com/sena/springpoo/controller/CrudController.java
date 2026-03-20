package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller
@RequestMapping("/crud")
public class CrudController {


    @GetMapping
    public String listar(Model model){

        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();
        model.addAttribute("usuarios", usuarios);

        return "crud";
    }


    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario){
        boolean creado = PersistenceUsuario.save(usuario);
        if(!creado){
            // Esto buscará templates/error/500.html automáticamente
            return "error/500";
        }
        return "redirect:/crud";
    }


    @DeleteMapping("/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ){

        boolean eliminado = PersistenceUsuario.delete(id);

        if(eliminado){
            return ResponseEntity.ok(
                    idioma.contains("es") ? "Usuario eliminado" : "User deleted"
            );
        }else{
            return ResponseEntity.status(404).body(
                    idioma.contains("es") ? "Usuario no encontrado" : "User not found"
            );
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarVista(@PathVariable long id){

        PersistenceUsuario.delete(id);

        return "redirect:/crud";
    }


    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable long id,
            @RequestBody Usuario usuario,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ){

        usuario.setIdUsuario(id);

        boolean actualizado = PersistenceUsuario.update(usuario);

        if(actualizado){
            return ResponseEntity.ok(
                    idioma.contains("es") ? "Usuario actualizado" : "User updated"
            );
        }else{
            return ResponseEntity.status(404).body(
                    idioma.contains("es") ? "Usuario no encontrado" : "User not found"
            );
        }
    }


    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam long id, Model model){

        Usuario usuario = PersistenceUsuario.getUsuarioById(id);

        if(usuario == null){
            return "error/404";
        }

        model.addAttribute("usuarioEncontrado", usuario);
        model.addAttribute("usuarios", PersistenceUsuario.getUsuarios());

        return "crud";
    }

}