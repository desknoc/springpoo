package com.sena.springpoo.persistence;

import com.sena.springpoo.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersistenceUsuario {

    // ══════════════════════════════════════════════════════════════
    //  GUARDAR
    // ══════════════════════════════════════════════════════════════
    public static boolean save(Usuario usuario) {

        String sql = "INSERT INTO usuario " +
                "(primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, " +
                "tipo_documento, documento, celular, correo_electronico, contrasena, " +
                "rol, fecha_registro, ultima_actualizacion) " +
                "VALUES (?,?,?,?,?,?,?,?,?, 'ADMIN', NOW(), NOW())";

        // BUG CORREGIDO: getConnection() puede retornar null si falla la conexión.
        // Si conn es null y se usa en try-with-resources, conn.prepareStatement()
        // lanza NullPointerException (no es SQLException), que no sería capturado
        // y Spring devolvería un 500 silencioso. La verificación explícita evita eso.
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("[PersistenceUsuario.save] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getPrimerNombre());
            ps.setString(2, usuario.getSegundoNombre());
            ps.setString(3, usuario.getPrimerApellido());
            ps.setString(4, usuario.getSegundoApellido());
            ps.setString(5, usuario.getTipoDocumento());
            ps.setLong(6, usuario.getDocumento());

            // BUG CORREGIDO: celular es varchar(20) en la DB, no bigint.
            // ps.setLong() en un varchar puede fallar en MySQL con modo estricto
            // (STRICT_TRANS_TABLES). Se convierte a String explícitamente.
            ps.setString(7, String.valueOf(usuario.getCelular()));

            ps.setString(8, usuario.getCorreo());
            ps.setString(9, usuario.getContrasena());

            // Verifica que realmente se insertó 1 fila
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[PersistenceUsuario.save] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTAR TODOS
    // ══════════════════════════════════════════════════════════════
    public static List<Usuario> getUsuarios() {

        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("[PersistenceUsuario.getUsuarios] No se pudo obtener conexión a la BD.");
            return lista;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getLong("id_usuario"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setDocumento(rs.getLong("documento"));
                // correo_electronico en DB → campo correo en el modelo
                u.setCorreo(rs.getString("correo_electronico"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.err.println("[PersistenceUsuario.getUsuarios] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // ══════════════════════════════════════════════════════════════
    //  BUSCAR POR ID  (usado por el modal de edición y por buscar)
    // ══════════════════════════════════════════════════════════════
    public static Usuario getUsuarioById(long id) {

        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("[PersistenceUsuario.getUsuarioById] No se pudo obtener conexión a la BD.");
            return null;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("id_usuario"));
                    u.setPrimerNombre(rs.getString("primer_nombre"));
                    u.setSegundoNombre(rs.getString("segundo_nombre"));
                    u.setPrimerApellido(rs.getString("primer_apellido"));
                    u.setSegundoApellido(rs.getString("segundo_apellido"));
                    u.setTipoDocumento(rs.getString("tipo_documento"));
                    u.setDocumento(rs.getLong("documento"));
                    // correo_electronico en DB → campo correo en el modelo
                    // CRÍTICO: Jackson serializa este campo como "correo" (por getCorreo()),
                    // y el JS del modal lo lee como user.correo. Debe coincidir.
                    u.setCorreo(rs.getString("correo_electronico"));
                    return u;
                }
            }

        } catch (SQLException e) {
            System.err.println("[PersistenceUsuario.getUsuarioById] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════════
    //  ACTUALIZAR
    // ══════════════════════════════════════════════════════════════
    public static boolean update(Usuario usuario) {

        // Los 5 parámetros deben ir en este orden exacto para coincidir
        // con los 5 signos ? del SQL: nombre, apellido, documento, correo, id (WHERE)
        String sql = "UPDATE usuario " +
                "SET primer_nombre = ?, " +
                "    primer_apellido = ?, " +
                "    documento = ?, " +
                "    correo_electronico = ?, " +
                "    ultima_actualizacion = NOW() " +
                "WHERE id_usuario = ?";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("[PersistenceUsuario.update] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getPrimerNombre());   // primer_nombre
            ps.setString(2, usuario.getPrimerApellido()); // primer_apellido
            ps.setLong(3,   usuario.getDocumento());      // documento
            ps.setString(4, usuario.getCorreo());         // correo_electronico
            ps.setLong(5,   usuario.getIdUsuario());      // WHERE id_usuario = ?

            int filasAfectadas = ps.executeUpdate();

            System.out.println("[PersistenceUsuario.update] id=" + usuario.getIdUsuario()
                    + " | filas afectadas=" + filasAfectadas);

            // BUG CORREGIDO: el código original ignoraba el resultado de executeUpdate()
            // y siempre retornaba true. Si el id no existe, 0 filas se actualizan
            // pero el método seguía retornando true, haciendo creer que funcionó.
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[PersistenceUsuario.update] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // ══════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ══════════════════════════════════════════════════════════════
    public static boolean delete(long id) {

        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            System.err.println("[PersistenceUsuario.delete] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filasAfectadas = ps.executeUpdate();

            // BUG CORREGIDO: igual que update, el original siempre retornaba true
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[PersistenceUsuario.delete] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}