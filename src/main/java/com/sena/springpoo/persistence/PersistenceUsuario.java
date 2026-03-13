package com.sena.springpoo.persistence;

public class PersistenceUsuario {

    public boolean save (){

        try {

            Connection conn = Conexion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, u.getPrimerNombre());
            ps.setString(2, u.getSegundoNombre());
            ps.setString(3, u.getPrimerApellido());
            ps.setString(4, u.getSegundoApellido());
            ps.setString(5, u.getTipoDocumento());
            ps.setInt(6, u.getDocumento());
            ps.setInt(7, u.getCelular());
            ps.setString(8, u.getCorreo());
            ps.setString(9, u.getContrasena());

            ps.executeUpdate();

        return true;
    }

}
