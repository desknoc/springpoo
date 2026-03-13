package com.sena.springpoo.persistence;

public class PersistenceUsuario {

    public boolean save (){

        try {

            Connection conn = Conexion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getPrimerNombre());
            ps.setString(2, usuario.getSegundoNombre());
            ps.setString(3, usuario.getPrimerApellido());
            ps.setString(4, usuario.getSegundoApellido());
            ps.setString(5, usuario.getTipoDocumento());
            ps.setInt(6, usuario.getDocumento());
            ps.setInt(7, usuario.getCelular());
            ps.setString(8, usuario.getCorreo());
            ps.setString(9, usuario.getContrasena());

            ps.executeUpdate();

        return true;
    }

}
