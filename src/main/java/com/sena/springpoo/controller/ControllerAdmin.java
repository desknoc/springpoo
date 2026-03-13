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
        System.out.println(primerNombre);
        System.out.println(segundoNombre);
        System.out.println(primerApellido);
        System.out.println(segundoApellido);
        System.out.println(tipoDocumento);
        System.out.println(documento);
        System.out.println(celular);
        System.out.println(correo);
        System.out.println(contrasena);
        return "Registro";
    }

}
