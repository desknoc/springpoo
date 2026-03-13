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
    public String metodoPost(){
        return "Usuario guardado";
    }

    @GetMapping("/registro")
    public String mostrarFormulario(){
        return "Registro";
    }

}
