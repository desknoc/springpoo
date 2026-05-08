package com.sena.springpoo.persistence;

import com.sena.springpoo.models.Usuario;
import com.sena.springpoo.models.UsuarioSesion;
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
                    // BUG CORREGIDO: manejar 'N/A' o vacíos para que no crashee con NumberFormatException
                    String celStr = rs.getString("celular");
                    try {
                        u.setCelular(Long.parseLong(celStr));
                    } catch (NumberFormatException e) {
                        u.setCelular(0L); // Valor por defecto si no es numérico
                    }

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

    // ══════════════════════════════════════════════════════════════
    //  BUSCAR POR TIPO_DOCUMENTO + DOCUMENTO (para autenticación)
    // ══════════════════════════════════════════════════════════════
    public static Usuario getUsuarioByTipoYDocumento(String tipoDocumento, long documento) {

        String sql = "SELECT * FROM usuario WHERE tipo_documento = ? AND documento = ?";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            logger.error("[PersistenceUsuario.getUsuarioByTipoYDocumento] No se pudo obtener conexión a la BD.");
            return null;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento);
            ps.setLong(2, documento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getLong("id_usuario"));
                    u.setTipoDocumento(rs.getString("tipo_documento"));
                    u.setDocumento(rs.getLong("documento"));
                    u.setCorreo(rs.getString("correo_electronico"));
                    u.setContrasena(rs.getString("contrasena"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.getUsuarioByTipoYDocumento] Error SQL: {}", e.getMessage(), e);
        }

        return null;
    }

    // ══════════════════════════════════════════════════════════════
    //  REGISTRAR SESIÓN (INSERT en tabla sesion al hacer login)
    // ══════════════════════════════════════════════════════════════
    /**
     * Inserta un registro en la tabla "sesion" cuando un usuario inicia sesión.
     *
     * ¿Cuándo se llama?
     * ─────────────────
     * Desde CustomAuthProvider.authenticate(), justo DESPUÉS de verificar
     * que las credenciales son correctas (BCrypt matches) y ANTES de
     * devolver el token autenticado a Spring Security.
     *
     * ¿Qué datos inserta?
     * ────────────────────
     * - tipo_documento:      El tipo de documento del usuario (CC, TI)
     * - documento:           El número de documento
     * - contrasena:          El hash BCrypt almacenado en la BD (NO la contraseña plana)
     * - fecha_registro:      NOW() → la fecha/hora exacta del login
     * - ultima_actualizacion: NOW() → igual que fecha_registro al momento de insertar
     * - usuario_id_usuario:  La FK que apunta al id_usuario en la tabla usuario
     *
     * ¿Por qué se usa PreparedStatement?
     * ──────────────────────────────────
     * Para prevenir ataques de SQL Injection. Los valores del usuario
     * se pasan como parámetros (?) y nunca se concatenan en el String SQL.
     *
     * @param usuario El objeto Usuario con los datos del usuario autenticado
     * @return true si el INSERT fue exitoso, false si falló
     */
    public static boolean registrarSesion(Usuario usuario) {

        // Sentencia SQL para insertar en la tabla sesion.
        // NOW() genera la fecha/hora actual del servidor MariaDB.
        String sql = "INSERT INTO sesion " +
                "(tipo_documento, documento, contrasena, fecha_registro, " +
                "ultima_actualizacion, usuario_id_usuario) " +
                "VALUES (?, ?, ?, NOW(), NOW(), ?)";

        // Obtener conexión a la BD mediante la clase Conexion
        Connection conn = Conexion.getConnection();
        if (conn == null) {
            logger.error("[PersistenceUsuario.registrarSesion] No se pudo obtener conexión a la BD.");
            return false;
        }

        // try-with-resources: cierra automáticamente conn y ps al salir del bloque
        try (conn; PreparedStatement ps = conn.prepareStatement(sql)) {

            // Mapeo de parámetros: cada ? corresponde a un índice secuencial
            ps.setString(1, usuario.getTipoDocumento());  // ? #1 → tipo_documento
            ps.setLong(2,   usuario.getDocumento());      // ? #2 → documento
            ps.setString(3, usuario.getContrasena());     // ? #3 → contrasena (hash BCrypt)
            ps.setLong(4,   usuario.getIdUsuario());      // ? #4 → usuario_id_usuario (FK)

            boolean registrado = ps.executeUpdate() > 0;

            // Auditoría: registrar en el log especializado de auditoría
            if (registrado) {
                auditLogger.info("INSERT | tabla=sesion | usuario_id={} | documento={}",
                        usuario.getIdUsuario(), usuario.getDocumento());
            }

            return registrado;

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.registrarSesion] Error SQL: {}", e.getMessage(), e);
        }

        return false;
    }

    // ══════════════════════════════════════════════════════════════
    //  LISTAR USUARIOS CON SESIÓN ACTIVA (INNER JOIN)
    // ══════════════════════════════════════════════════════════════
    /**
     * Consulta que une las tablas "usuario" y "sesion" mediante INNER JOIN.
     *
     * ¿Qué es un INNER JOIN?
     * ──────────────────────
     * Es un tipo de JOIN que devuelve SOLO las filas donde existe una
     * coincidencia en AMBAS tablas. En este caso:
     *   - Si un usuario tiene 3 sesiones → aparece 3 veces en el resultado
     *   - Si un usuario tiene 0 sesiones → NO aparece (se excluye)
     *
     * ¿Por qué INNER JOIN y no LEFT JOIN?
     * ────────────────────────────────────
     * Porque el requerimiento es mostrar ÚNICAMENTE los usuarios que
     * SÍ tienen sesión activa. Con LEFT JOIN aparecerían TODOS los
     * usuarios (incluso los que nunca han iniciado sesión) con valores
     * NULL en las columnas de sesion.
     *
     * La cláusula de unión: ON u.id_usuario = s.usuario_id_usuario
     * ─────────────────────────────────────────────────────────────
     * Conecta cada registro de sesion con su usuario correspondiente
     * a través de la Foreign Key (FK) usuario_id_usuario.
     *
     * ORDER BY s.fecha_registro DESC
     * ──────────────────────────────
     * Ordena los resultados por fecha de sesión más reciente primero,
     * para que el administrador vea los logins más recientes arriba.
     *
     * @return Lista de UsuarioSesion con los datos del JOIN, o null si falla la conexión
     */
    public static List<UsuarioSesion> getUsuariosConSesion() {

        List<UsuarioSesion> lista = new ArrayList<>();

        // Consulta SQL con INNER JOIN entre usuario (alias "u") y sesion (alias "s")
        String sql = "SELECT u.id_usuario, " +
                "       u.primer_nombre, " +
                "       u.primer_apellido, " +
                "       u.tipo_documento, " +
                "       u.documento, " +
                "       u.correo_electronico, " +
                "       s.id_sesion, " +
                "       s.fecha_registro    AS fecha_sesion, " +
                "       s.ultima_actualizacion AS ultima_actualizacion_sesion " +
                "FROM   usuario u " +
                "       INNER JOIN sesion s ON u.id_usuario = s.usuario_id_usuario " +
                "ORDER BY s.fecha_registro DESC";

        Connection conn = Conexion.getConnection();
        if (conn == null) {
            logger.error("[PersistenceUsuario.getUsuariosConSesion] No se pudo obtener conexión a la BD.");
            return null;
        }

        try (conn; PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Recorrer cada fila del ResultSet y mapear manualmente
            // las columnas del JOIN al POJO UsuarioSesion
            while (rs.next()) {
                UsuarioSesion us = new UsuarioSesion();

                // Columnas de la tabla usuario (prefijo "u." en el SQL)
                us.setIdUsuario(rs.getLong("id_usuario"));
                us.setPrimerNombre(rs.getString("primer_nombre"));
                us.setPrimerApellido(rs.getString("primer_apellido"));
                us.setTipoDocumento(rs.getString("tipo_documento"));
                us.setDocumento(rs.getLong("documento"));
                us.setCorreo(rs.getString("correo_electronico"));

                // Columnas de la tabla sesion (alias definidos en el SQL)
                us.setIdSesion(rs.getLong("id_sesion"));
                us.setFechaSesion(rs.getString("fecha_sesion"));
                us.setUltimaActualizacionSesion(rs.getString("ultima_actualizacion_sesion"));

                lista.add(us);
            }

        } catch (SQLException e) {
            logger.error("[PersistenceUsuario.getUsuariosConSesion] Error SQL: {}", e.getMessage(), e);
        }

        return lista;
    }
}