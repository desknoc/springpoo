package com.sena.springpoo.persistence;

import com.sena.springpoo.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersistenceUsuario {

    private static final Logger logger      = LoggerFactory.getLogger(PersistenceUsuario.class);
    // Logger dedicado a auditoría → escribe en logs/db-audit.log (ver logback-spring.xml)
    private static final Logger auditLogger = LoggerFactory.getLogger("DB_AUDIT");

    // ══════════════════════════════════════════════════════════════
    //  GUARDAR
    // ══════════════════════════════════════════════════════════════
    public static boolean save(Usuario usuario) {

        String sql = "INSERT INTO usuario " +
                "(primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, " +
                "tipo_documento, documento, celular, correo_electronico, contrasena, " +
                "rol, fecha_registro, ultima_actualizacion) " +
                "VALUES (?,?,?,?,?,?,?,?,?, 'ADMIN', NOW(), NOW())";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            logger.error("[PersistenceUsuario.save] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getPrimerNombre());
            ps.setString(2, usuario.getSegundoNombre());
            ps.setString(3, usuario.getPrimerApellido());
            ps.setString(4, usuario.getSegundoApellido());
            ps.setString(5, usuario.getTipoDocumento());
            ps.setLong(6,   usuario.getDocumento());
            // celular es varchar(20) en la BD → se convierte a String
            ps.setString(7, String.valueOf(usuario.getCelular()));
            ps.setString(8, usuario.getCorreo());
            ps.setString(9, usuario.getContrasena());

            boolean guardado = ps.executeUpdate() > 0;

            if (guardado) {
                auditLogger.info("INSERT | tabla=usuario | correo={} | documento={} | rol=ADMIN",
                        usuario.getCorreo(), usuario.getDocumento());
            }

            return guardado;

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.save] Error SQL: {}", e.getMessage(), e);
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
            logger.error("[PersistenceUsuario.getUsuarios] No se pudo obtener conexión a la BD.");
            return null;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getLong("id_usuario"));
                u.setPrimerNombre(rs.getString("primer_nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setDocumento(rs.getLong("documento"));
                u.setCorreo(rs.getString("correo_electronico"));
                lista.add(u);
            }

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.getUsuarios] Error SQL: {}", e.getMessage(), e);
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
            logger.error("[PersistenceUsuario.getUsuarioById] No se pudo obtener conexión a la BD.");
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
                    // BUG CORREGIDO: antes no se cargaba celular ni contrasena del ResultSet,
                    // por eso el modal los recibía vacíos (0 y null), la validación JS
                    // los marcaba como campos vacíos y cortaba el fetch antes de enviarlo.
                    u.setCelular(Long.parseLong(rs.getString("celular")));
                    u.setCorreo(rs.getString("correo_electronico"));
                    u.setContrasena(rs.getString("contrasena"));
                    return u;
                }
            }

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.getUsuarioById] Error SQL: {}", e.getMessage(), e);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════════
    //  ACTUALIZAR
    // ══════════════════════════════════════════════════════════════
    public static boolean update(Usuario usuario) {

        // 9 campos SET + 1 WHERE = 10 signos ? en total
        String sql = "UPDATE usuario " +
                "SET primer_nombre        = ?, " +   // 1
                "    segundo_nombre       = ?, " +   // 2
                "    primer_apellido      = ?, " +   // 3
                "    segundo_apellido     = ?, " +   // 4
                "    tipo_documento       = ?, " +   // 5
                "    documento            = ?, " +   // 6
                "    celular              = ?, " +   // 7
                "    correo_electronico   = ?, " +   // 8
                "    contrasena           = ?, " +   // 9
                "    ultima_actualizacion = NOW() " +
                "WHERE id_usuario = ?";              // 10

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            logger.error("[PersistenceUsuario.update] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1,  usuario.getPrimerNombre());
            ps.setString(2,  usuario.getSegundoNombre());
            ps.setString(3,  usuario.getPrimerApellido());
            ps.setString(4,  usuario.getSegundoApellido());
            ps.setString(5,  usuario.getTipoDocumento());
            ps.setLong(6,    usuario.getDocumento());
            // celular es varchar(20) en la BD → se convierte a String
            ps.setString(7,  String.valueOf(usuario.getCelular()));
            ps.setString(8,  usuario.getCorreo());
            ps.setString(9,  usuario.getContrasena());
            // ══════════════════════════════════════════════════════
            // BUG RAÍZ CORREGIDO:
            // El código anterior tenía:
            //   ps.setString(9, contrasena)   ← índice 9
            //   ps.setLong(9,   idUsuario)     ← índice 9 OTRA VEZ → pisaba la contraseña
            //
            // JDBC asigna los ? en orden secuencial (1, 2, 3...).
            // Al duplicar el índice 9, el ? número 10 (el WHERE) nunca recibía valor
            // → PreparedStatement enviaba un parámetro nulo al WHERE
            // → MySQL no encontraba ninguna fila → executeUpdate() retornaba 0
            // → el método retornaba false → el controller respondía 404
            // → el JS mostraba "No se pudo actualizar" sin haber ejecutado nada.
            // ══════════════════════════════════════════════════════
            ps.setLong(10,   usuario.getIdUsuario());   // WHERE id_usuario = ?

            int filasAfectadas = ps.executeUpdate();
            logger.info("[PersistenceUsuario.update] id={} | filas afectadas={}", usuario.getIdUsuario(), filasAfectadas);

            if (filasAfectadas > 0) {
                auditLogger.info("UPDATE | tabla=usuario | id={} | correo={} | documento={}",
                        usuario.getIdUsuario(), usuario.getCorreo(), usuario.getDocumento());
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.update] Error SQL: {}", e.getMessage(), e);
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
            logger.error("[PersistenceUsuario.delete] No se pudo obtener conexión a la BD.");
            return false;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                auditLogger.info("DELETE | tabla=usuario | id={}", id);
            }

            return filasAfectadas > 0;

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.delete] Error SQL: {}", e.getMessage(), e);
        }

        return false;
    }
}