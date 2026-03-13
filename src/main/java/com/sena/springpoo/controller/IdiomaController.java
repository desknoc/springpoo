package com.sena.springpoo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class IdiomaController {

    @GetMapping("/saludo")
    public String saludo(@RequestHeader("Accept-Language") String idioma){

        if(idioma.startsWith("es")){
            return "Hola usuario";
        }else{
            return "Hello user";
        }

    }

    @GetMapping("/mensaje")
    public String mensaje(@RequestParam String nombre){

        return "Hola " + nombre;

    }

}