package com.sena.springpoo.persistence;

public class PersistenceUsuario {

    public boolean save (){

        String sql = "INSERT INTO usuario (primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_documento, documento, celular, correo_electronico, contrasena, rol, fecha_registro, ultima_actualizacion) VALUES (?,?,?,?,?,?,?,?,?, ADMIN, NOW(), NOW())";

        return true;
    }

}
