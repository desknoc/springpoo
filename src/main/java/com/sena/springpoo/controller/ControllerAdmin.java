package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Controller
public class ControllerAdmin {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdmin.class);

    @GetMapping("/")
    public String inicio(){
        return "Registro";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ✅ CREAR USUARIO REDIRIGIENDO AL LOGIN
    @PostMapping("/guardarUsuario")
    public String registrarUsuario(
            @RequestParam String primerNombre,
            @RequestParam(required = false) String segundoNombre,
            @RequestParam String primerApellido,
            @RequestParam(required = false) String segundoApellido,
            @RequestParam String tipoDocumento,
            @RequestParam long documento,
            @RequestParam long celular,
            @RequestParam String correo,
            @RequestParam String contrasena
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
        
        // Hashing de la contraseña antes de guardar
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setContrasena(encoder.encode(contrasena));

        boolean guardado = PersistenceUsuario.save(usuario);

        if(guardado){
            logger.info("[ControllerAdmin.registrarUsuario] Usuario '{}' guardado correctamente.", correo);
            return "redirect:/login";
        } else {
            logger.error("[ControllerAdmin.registrarUsuario] Fallo al guardar el usuario con correo '{}'.", correo);
            return "error/500";
        }
    }
}