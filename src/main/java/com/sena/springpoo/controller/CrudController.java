package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/crud")
public class CrudController {

    @GetMapping
    public String crud(Model model){

        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();

        model.addAttribute("usuarios", usuarios);

        return "crud";
    }

    @PostMapping("/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario){

        PersistenceUsuario.save(usuario);

        return "redirect:/crud";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable long id){

        PersistenceUsuario.delete(id);

        return "redirect:/crud";
    }

    @PutMapping("/actualizar/{id}")
    @ResponseBody
    public String actualizarUsuario(@PathVariable long id, @RequestBody Usuario usuario){

        usuario.setIdUsuario(id);

        PersistenceUsuario.update(usuario);

        return "Usuario actualizado";
    }

    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam long id, Model model){

        Usuario usuario = PersistenceUsuario.getUsuarioById(id);

        model.addAttribute("usuarioEncontrado", usuario);

        model.addAttribute("usuarios", PersistenceUsuario.getUsuarios());

        return "crud";
    }

    @GetMapping("/mensaje")
    @ResponseBody
    public String mensaje(@RequestHeader("Accept-Language") String idioma){

        if(idioma.contains("es")){
            return "Hola usuario";
        }else{
            return "Hello user";
        }
    }

}