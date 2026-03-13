package com.sena.springpoo.persistence;

import com.sena.springpoo.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class PersistenceUsuario {

    public static boolean save(Usuario usuario){

        String sql = "INSERT INTO usuario (primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, tipo_documento, documento, celular, correo_electronico, contrasena, rol, fecha_registro, ultima_actualizacion) VALUES (?,?,?,?,?,?,?,?,?, 'ADMIN', NOW(), NOW())";

        try {

            Connection conn = Conexion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getPrimerNombre());
            ps.setString(2, usuario.getSegundoNombre());
            ps.setString(3, usuario.getPrimerApellido());
            ps.setString(4, usuario.getSegundoApellido());
            ps.setString(5, usuario.getTipoDocumento());
            ps.setLong(6, usuario.getDocumento());
            ps.setLong(7, usuario.getCelular());
            ps.setString(8, usuario.getCorreo());
            ps.setString(9, usuario.getContrasena());

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static List<Usuario> getUsuarios(){

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuario";

        try {

            Connection conn = Conexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){

                Usuario u = new Usuario();

                u.setIdUsuario(rs.getLong("id_usuario"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setDocumento(rs.getLong("documento"));
                u.setCorreo(rs.getString("correo_electronico"));

                lista.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;

    }

    public static Usuario getUsuarioById(long id){

        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try{

            Connection conn = Conexion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                Usuario u = new Usuario();

                u.setIdUsuario(rs.getLong("id_usuario"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setDocumento(rs.getLong("documento"));
                u.setCorreo(rs.getString("correo_electronico"));

                return u;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public static boolean update(Usuario usuario){

        String sql = "UPDATE usuario SET primer_nombre=?, primer_apellido=?, documento=?, correo_electronico=?, ultima_actualizacion=NOW() WHERE id_usuario=?";

        try{

            Connection conn = Conexion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getPrimerNombre());
            ps.setString(2, usuario.getPrimerApellido());
            ps.setLong(3, usuario.getDocumento());
            ps.setString(4, usuario.getCorreo());
            ps.setLong(5, usuario.getIdUsuario());

            ps.executeUpdate();

            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean delete(long id){

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try {

            Connection conn = Conexion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setLong(1, id);

            ps.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

}