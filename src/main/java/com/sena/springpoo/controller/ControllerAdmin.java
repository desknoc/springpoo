package com.sena.springpoo.controller;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.persistence.PersistenceUsuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ControllerAdmin {

    @GetMapping("/")
    public String inicio(){
        return "Registro";
    }

    // ✅ CREAR USUARIO CON RESPUESTA HTTP + HEADER
    @PostMapping("/guardarUsuario")
    @ResponseBody
    public ResponseEntity<String> registrarUsuario(
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

        // 📡 CÓDIGOS HTTP
        if(guardado){
            return new ResponseEntity<>(mensaje, HttpStatus.CREATED); // 201
        } else {
            return new ResponseEntity<>(mensaje, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }


    @DeleteMapping("/eliminarUsuario/{id}")
    @ResponseBody
    public ResponseEntity<String> eliminarUsuario(
            @PathVariable long id,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String idioma
    ){

        boolean eliminado = PersistenceUsuario.delete(id);

        String mensaje;

        if(idioma.contains("es")){
            mensaje = eliminado ? "Usuario eliminado" : "Usuario no encontrado";
        } else {
            mensaje = eliminado ? "User deleted" : "User not found";
        }

        if(eliminado){
            return new ResponseEntity<>(mensaje, HttpStatus.OK); // 200
        } else {
            return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND); // 404
        }
    }


    @GetMapping("/usuariosAPI")
    @ResponseBody
    public ResponseEntity<List<Usuario>> verUsuarios(){

        List<Usuario> usuarios = PersistenceUsuario.getUsuarios();

        if(usuarios.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        }

        return new ResponseEntity<>(usuarios, HttpStatus.OK); // 200
    }
}