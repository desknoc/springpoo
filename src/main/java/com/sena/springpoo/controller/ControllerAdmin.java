package com.sena.springpoo.controller;


import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;


@Controller
public class ControllerAdmin {

    @GetMapping("/")
    public String inicio(){
        return "Registro";
    }

    // ✅ CREAR USUARIO CON RESPUESTA HTTP + HEADER
    @PostMapping("/guardarUsuario")
    public String registrarUsuario(
            @RequestParam String primerNombre,
            @RequestParam String segundoNombre,
            @RequestParam String primerApellido,
            @RequestParam String segundoApellido,
            @RequestParam String tipoDocumento,
            @RequestParam long documento,
            @RequestParam long celular,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ){
        Usuario usuario = new Usuario();

        usuario.setPrimerNombre(primerNombre);
        usuario.setSegundoNombre(segundoNombre);
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        usuario.setTipoDocumento(tipoDocumento);
        usuario.setDocumento(documento);
        usuario.setCelular(celular);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);

        boolean guardado = PersistenceUsuario.save(usuario);

        // 🌍 MENSAJES SEGÚN IDIOMA
        String mensaje;

        if(idioma.contains("es")){
            mensaje = guardado ? "Usuario guardado correctamente" : "Error al guardar usuario";
        } else {
            mensaje = guardado ? "User saved successfully" : "Error saving user";
        }

        return "Registro";
    }

    @GetMapping("/eliminarUsuario")
    public String eliminarUsuario(@RequestParam long id){

        PersistenceUsuario.delete(id);

        return "redirect:/usuarios";

    }


    @GetMapping("/usuarios")
    public String verUsuarios(Model model){

        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();

        model.addAttribute("usuarios", usuarios);

        return "usuarios";
    }

}
