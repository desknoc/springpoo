package com.sena.springpoo.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ControllerAdmin {

    @GetMapping("/")
    public String inicio(){
        return "Registro";
    }

    @PostMapping("/guardarUsuario")
    public String registrarUsuario(
            @RequestParam String primerNombre,
            @RequestParam String segundoNombre,
            @RequestParam String primerApellido,
            @RequestParam String segundoApellido,
            @RequestParam String tipoDocumento,
            @RequestParam String documento,
            @RequestParam String celular,
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
        usuario.setContrasena(contrasena);

        return "Registro";
    }

}
