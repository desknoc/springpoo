package com.sena.springpoo.models;

/**
 * POJO que representa el resultado del INNER JOIN entre las tablas "usuario" y "sesion".
 *
 * ¿Por qué un modelo separado de Usuario?
 * ─────────────────────────────────────────
 * Cuando hacemos un INNER JOIN, el ResultSet devuelve columnas de AMBAS tablas.
 * Si reutilizáramos el modelo Usuario, tendríamos que agregarle campos de sesión
 * (idSesion, fechaSesion, etc.) que NO pertenecen a la entidad Usuario.
 * Eso rompería el principio de responsabilidad única (SRP).
 *
 * Este POJO contiene SOLO los campos que necesitamos mostrar en la tabla del frontend:
 *   - Datos del usuario: id, nombre, apellido, tipo documento, documento, correo
 *   - Datos de la sesión: id de sesión, fecha de inicio, última actualización
 *
 * Sigue el patrón POJO estricto del proyecto:
 *   - Atributos privados
 *   - Constructor vacío (obligatorio para frameworks y mapeo manual)
 *   - Constructor cargado (para crear instancias rápidas en tests o lógica)
 *   - Getters y Setters para cada atributo
 */
public class UsuarioSesion {

    // ─── Campos provenientes de la tabla "usuario" ───
    private Long idUsuario;
    private String primerNombre;
    private String primerApellido;
    private String tipoDocumento;
    private Long documento;
    private String correo;

    // ─── Campos provenientes de la tabla "sesion" ───
    private Long idSesion;
    private String fechaSesion;               // sesion.fecha_registro
    private String ultimaActualizacionSesion; // sesion.ultima_actualizacion

    /**
     * Constructor vacío.
     * Necesario para que la capa de persistencia pueda crear instancias
     * y luego llenarlas con los setters al recorrer el ResultSet.
     */
    public UsuarioSesion() {
    }

    /**
     * Constructor cargado.
     * Permite crear una instancia completa en una sola línea si fuera necesario.
     *
     * @param idUsuario                 ID del usuario en la tabla usuario
     * @param primerNombre              Primer nombre del usuario
     * @param primerApellido            Primer apellido del usuario
     * @param tipoDocumento             Tipo de documento (CC o TI)
     * @param documento                 Número de documento
     * @param correo                    Correo electrónico del usuario
     * @param idSesion                  ID de la sesión en la tabla sesion
     * @param fechaSesion               Fecha de registro de la sesión
     * @param ultimaActualizacionSesion Última actualización de la sesión
     */
    public UsuarioSesion(Long idUsuario, String primerNombre, String primerApellido,
                         String tipoDocumento, Long documento, String correo,
                         Long idSesion, String fechaSesion, String ultimaActualizacionSesion) {
        this.idUsuario = idUsuario;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.correo = correo;
        this.idSesion = idSesion;
        this.fechaSesion = fechaSesion;
        this.ultimaActualizacionSesion = ultimaActualizacionSesion;
    }

    // ═══════════════════════════════════════════════════
    //  GETTERS Y SETTERS
    // ═══════════════════════════════════════════════════

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Long getDocumento() {
        return documento;
    }

    public void setDocumento(Long documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Long getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(Long idSesion) {
        this.idSesion = idSesion;
    }

    public String getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(String fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getUltimaActualizacionSesion() {
        return ultimaActualizacionSesion;
    }

    public void setUltimaActualizacionSesion(String ultimaActualizacionSesion) {
        this.ultimaActualizacionSesion = ultimaActualizacionSesion;
    }
}
