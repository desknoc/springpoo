-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 03-03-2026 a las 13:45:20
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sena`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `contarFormularios` (IN `p_id_usuario` INT, OUT `total` INT)   BEGIN
    SELECT COUNT(*) INTO total 
    FROM Formularios_soporte 
    WHERE usuario_id_usuario = p_id_usuario;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registrarIngresoYGasto` (IN `p_id_usuario` INT, IN `p_ingreso` DECIMAL(14,2), IN `p_gasto` DECIMAL(14,2))   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;

    START TRANSACTION;

    INSERT INTO Ingresos(monto, fecha_registro, ultima_actualizacion, usuario_id_usuario)
    VALUES (p_ingreso, CURDATE(), NOW(), p_id_usuario);

    SAVEPOINT ingreso_guardado;

    INSERT INTO Gastos(categoria, monto, fecha_registro, ultima_actualizacion, usuario_id_usuario)
    VALUES ('Otros', p_gasto, CURDATE(), NOW(), p_id_usuario);

    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registrarRecuperacion` (IN `p_id_usuario` INT, IN `p_token` VARCHAR(100))   BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;

    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
    START TRANSACTION;

    SELECT id_usuario FROM Usuario WHERE id_usuario = p_id_usuario FOR UPDATE;

    INSERT INTO Recuperacion_contrasena(token, fecha_solicitud, usuario_id_usuario)
    VALUES (p_token, NOW(), p_id_usuario);

    COMMIT;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `registrarUsuario` (IN `p_nombre` VARCHAR(45), IN `p_apellido` VARCHAR(45), IN `p_tipo_documento` ENUM('CC','TI'), IN `p_documento` BIGINT, IN `p_correo` VARCHAR(100), IN `p_contrasena` VARCHAR(200), IN `p_rol` ENUM('ADMIN','USUARIO'))   BEGIN
    INSERT INTO Usuario(primer_nombre, primer_apellido, tipo_documento, documento, correo_electronico, contrasena, rol, fecha_registro, ultima_actualizacion)
    VALUES (p_nombre, p_apellido, p_tipo_documento, p_documento, p_correo, p_contrasena, IFNULL(p_rol,'USUARIO'), NOW(), NOW());
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `reporteFinanciero` (IN `p_id_usuario` INT)   BEGIN
    SELECT 
        u.primer_nombre, u.primer_apellido,
        (SELECT SUM(monto) FROM Ingresos WHERE usuario_id_usuario = p_id_usuario) AS total_ingresos,
        (SELECT SUM(monto) FROM Gastos WHERE usuario_id_usuario = p_id_usuario) AS total_gastos,
        (SELECT SUM(valor_objetivo) FROM Metas_ahorro WHERE usuario_id_usuario = p_id_usuario) AS metas
    FROM Usuario u
    WHERE u.id_usuario = p_id_usuario;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `chat_grupo`
--

CREATE TABLE `chat_grupo` (
  `id_mensaje` int(11) NOT NULL,
  `grupo_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `mensaje` text NOT NULL,
  `fecha_envio` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comunicados`
--

CREATE TABLE `comunicados` (
  `id_comunicado` int(11) NOT NULL,
  `titulo` varchar(200) NOT NULL,
  `contenido` text NOT NULL,
  `fecha_publicacion` datetime NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `documentos`
--

CREATE TABLE `documentos` (
  `id_documento` int(11) NOT NULL,
  `nombre_documento` varchar(200) NOT NULL,
  `url_archivo` text NOT NULL,
  `fecha_subida` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `exportaciones`
--

CREATE TABLE `exportaciones` (
  `id_exportacion` int(11) NOT NULL,
  `nombre_archivo` varchar(200) NOT NULL,
  `fecha_exportacion` date NOT NULL,
  `ultima_actualizacion` varchar(45) NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL,
  `tipo_exportacion` enum('Personal','Finanzas') NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `exportaciones`
--

INSERT INTO `exportaciones` (`id_exportacion`, `nombre_archivo`, `fecha_exportacion`, `ultima_actualizacion`, `usuario_id_usuario`, `tipo_exportacion`) VALUES
(1, 'exportacion_personal_2026-01-29.xlsx', '2026-01-29', '2026-01-29 18:26:05', 5, 'Personal');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `formularios`
--

CREATE TABLE `formularios` (
  `id_formulario` int(11) NOT NULL,
  `nombre_formulario` varchar(100) NOT NULL,
  `descripcion` text NOT NULL,
  `fecha_creacion` datetime NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `formularios_soporte`
--

CREATE TABLE `formularios_soporte` (
  `id_formulario` int(11) NOT NULL,
  `nombre_formulario` varchar(60) NOT NULL,
  `fecha_envio` datetime NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `gastos`
--

CREATE TABLE `gastos` (
  `id_gasto` int(11) NOT NULL,
  `categoria` enum('Salud','Alimentación','Transporte','Vivienda','Otros') NOT NULL,
  `monto` decimal(14,2) NOT NULL,
  `fecha_registro` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Disparadores `gastos`
--
DELIMITER $$
CREATE TRIGGER `auditarGastos` AFTER INSERT ON `gastos` FOR EACH ROW BEGIN
    INSERT INTO Registro_auditoria(tipo_accion, tabla_objetivo, descripcion_accion, fecha_registro, agente_usuario, ultima_actualizacion, usuario_id_usuario)
    VALUES ('INSERT','Gastos', CONCAT('Se registró gasto de ', NEW.monto), NOW(), USER(), NOW(), NEW.usuario_id_usuario);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `grupos`
--

CREATE TABLE `grupos` (
  `id_grupo` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `ultima_actualizacion` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `grupos_usuarios`
--

CREATE TABLE `grupos_usuarios` (
  `id` int(11) NOT NULL,
  `grupo_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ingresos`
--

CREATE TABLE `ingresos` (
  `id_ingreso` int(11) NOT NULL,
  `monto` decimal(14,2) NOT NULL,
  `fecha_registro` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Disparadores `ingresos`
--
DELIMITER $$
CREATE TRIGGER `auditarIngresos` AFTER INSERT ON `ingresos` FOR EACH ROW BEGIN
    INSERT INTO Registro_auditoria(tipo_accion, tabla_objetivo, descripcion_accion, fecha_registro, agente_usuario, ultima_actualizacion, usuario_id_usuario)
    VALUES ('INSERT','Ingresos', CONCAT('Se registró ingreso de ', NEW.monto), NOW(), USER(), NOW(), NEW.usuario_id_usuario);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `interacciones_bot_finanzas`
--

CREATE TABLE `interacciones_bot_finanzas` (
  `id_interaccion` int(11) NOT NULL,
  `tipo` enum('CONSEJO','FAQ') NOT NULL,
  `contenido` text NOT NULL,
  `fecha_interaccion` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `metas_ahorro`
--

CREATE TABLE `metas_ahorro` (
  `id_ahorro` int(11) NOT NULL,
  `meta` varchar(45) NOT NULL,
  `valor_objetivo` decimal(10,2) NOT NULL,
  `fecha_objetivo` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preguntas_bot_soporte`
--

CREATE TABLE `preguntas_bot_soporte` (
  `id_pregunta` int(11) NOT NULL,
  `pregunta` text NOT NULL,
  `respuesta` text NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preguntas_encuesta`
--

CREATE TABLE `preguntas_encuesta` (
  `id_pregunta` int(11) NOT NULL,
  `pregunta` text NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL,
  `formulario_id_formulario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recuperacion_contrasena`
--

CREATE TABLE `recuperacion_contrasena` (
  `id_recuperacion` int(11) NOT NULL,
  `token` varchar(100) NOT NULL,
  `fecha_solicitud` datetime NOT NULL,
  `fecha_restablecimiento` datetime DEFAULT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `recuperacion_contrasena`
--

INSERT INTO `recuperacion_contrasena` (`id_recuperacion`, `token`, `fecha_solicitud`, `fecha_restablecimiento`, `usuario_id_usuario`) VALUES
(26, '$2b$10$ItMk1DkZm3XH8tNkWUqUR.p8Og7jf7RRhIQe/zOF.bOoCRt/y1NGO', '2026-01-19 18:04:58', '2026-01-19 18:06:37', 1),
(27, '$2b$10$AaAorswI7/HOtTmBJ/jnyuzPBThLN4SrrzWkv8kG2yY5sOjjoMohG', '2026-01-19 18:17:51', '2026-01-19 18:18:16', 1),
(28, '$2b$10$eAC1fKHe.2FZoAx9wSK4.OaW.tQuOqJRwx1lLDNvBmOu3jLdRfabW', '2026-01-26 18:05:32', '2026-01-26 18:05:54', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `registro_auditoria`
--

CREATE TABLE `registro_auditoria` (
  `id_auditoria` int(11) NOT NULL,
  `tipo_accion` varchar(45) NOT NULL,
  `tabla_objetivo` varchar(45) NOT NULL,
  `descripcion_accion` text NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `agente_usuario` text NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `respuestas_encuesta`
--

CREATE TABLE `respuestas_encuesta` (
  `id_respuesta` int(11) NOT NULL,
  `respuesta` text NOT NULL,
  `fecha_respuesta` date NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL,
  `formulario_id_formulario` int(11) NOT NULL,
  `pregunta_encuesta_id_pregunta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sesion`
--

CREATE TABLE `sesion` (
  `id_sesion` int(11) NOT NULL,
  `tipo_documento` enum('CC','TI') NOT NULL,
  `documento` bigint(20) NOT NULL,
  `contrasena` varchar(200) NOT NULL,
  `fecha_registro` datetime NOT NULL,
  `ultima_actualizacion` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trazabilidad_sistema`
--

CREATE TABLE `trazabilidad_sistema` (
  `id_traza` int(11) NOT NULL,
  `accion` varchar(100) NOT NULL,
  `descripcion` varchar(100) NOT NULL,
  `fecha` datetime NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `primer_nombre` varchar(45) NOT NULL,
  `segundo_nombre` varchar(45) DEFAULT NULL,
  `primer_apellido` varchar(45) NOT NULL,
  `segundo_apellido` varchar(45) DEFAULT NULL,
  `tipo_documento` enum('CC','TI') NOT NULL,
  `documento` bigint(20) NOT NULL,
  `celular` varchar(20) NOT NULL,
  `grupo_formacion` varchar(100) DEFAULT NULL,
  `correo_electronico` varchar(100) NOT NULL,
  `contrasena` varchar(200) NOT NULL,
  `rol` enum('ADMIN','USUARIO') NOT NULL,
  `tipo_apoyo` enum('regular','alimentacion','transporte') DEFAULT NULL,
  `fecha_registro` datetime NOT NULL,
  `ultima_actualizacion` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `primer_nombre`, `segundo_nombre`, `primer_apellido`, `segundo_apellido`, `tipo_documento`, `documento`, `celular`, `grupo_formacion`, `correo_electronico`, `contrasena`, `rol`, `tipo_apoyo`, `fecha_registro`, `ultima_actualizacion`) VALUES
(1, 'Santiago', 'Andres', 'Vacca', 'Carrasquilla', 'CC', 1013109723, '3242256779', '3147211', 'Santiagovacca833@gmail.com', '$2b$10$3GLVA/UqeK6wW0b3tYMGs.SZz2J0sb27aU21NaytvEubPzDehatQe', 'USUARIO', 'alimentacion', '2026-01-15 00:12:24', '2026-01-26 18:05:54'),
(3, 'Alejandra', NULL, 'Martinez', 'Gomez', 'CC', 10987654321, '3101234567', '3147211', 'Alejandra_1092@gmail.com', '$2b$10$df5xQdyAUKawaVQTJ769I.sq9.PwNcgszQZKcQuCouIpsaGx/V81C', 'USUARIO', 'transporte', '2026-01-15 00:55:37', '2026-01-29 17:26:01'),
(5, 'Leidy', 'Johana', 'Callejas', NULL, 'CC', 1020396263, 'N/A', 'Bienestar', 'ljcallejas@sena.edu.co', '$2b$10$V9uM88QXIQS2gC76qbk1UOtQUkNmeI2pRjzfzhKB/G9XcIymXcGde', 'ADMIN', NULL, '2026-01-15 01:17:30', '2026-01-15 01:17:30');

--
-- Disparadores `usuario`
--
DELIMITER $$
CREATE TRIGGER `actualizarFechaUsuario` BEFORE UPDATE ON `usuario` FOR EACH ROW BEGIN
    SET NEW.ultima_actualizacion = NOW();
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `rolDefecto` BEFORE INSERT ON `usuario` FOR EACH ROW BEGIN
    IF NEW.rol IS NULL THEN
        SET NEW.rol = 'USUARIO';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `validarContrasena` BEFORE INSERT ON `usuario` FOR EACH ROW BEGIN
    IF LENGTH(NEW.contrasena) < 8 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La contraseña debe tener mínimo 8 caracteres';
    END IF;
END
$$
DELIMITER ;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `chat_grupo`
--
ALTER TABLE `chat_grupo`
  ADD PRIMARY KEY (`id_mensaje`),
  ADD KEY `idx_chat_grupo` (`grupo_id`),
  ADD KEY `idx_chat_usuario` (`usuario_id`),
  ADD KEY `idx_chat_fecha` (`fecha_envio`);

--
-- Indices de la tabla `comunicados`
--
ALTER TABLE `comunicados`
  ADD PRIMARY KEY (`id_comunicado`),
  ADD KEY `idx_usuario_comunicado` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_publicacion` (`fecha_publicacion`);

--
-- Indices de la tabla `documentos`
--
ALTER TABLE `documentos`
  ADD PRIMARY KEY (`id_documento`),
  ADD KEY `idx_usuario_documento` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_documento` (`fecha_subida`);

--
-- Indices de la tabla `exportaciones`
--
ALTER TABLE `exportaciones`
  ADD PRIMARY KEY (`id_exportacion`),
  ADD KEY `idx_usuario_exportacion` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_exportacion` (`fecha_exportacion`);

--
-- Indices de la tabla `formularios`
--
ALTER TABLE `formularios`
  ADD PRIMARY KEY (`id_formulario`),
  ADD KEY `idx_usuario_formulario_general` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_creacion` (`fecha_creacion`);

--
-- Indices de la tabla `formularios_soporte`
--
ALTER TABLE `formularios_soporte`
  ADD PRIMARY KEY (`id_formulario`),
  ADD KEY `idx_usuario_formulario` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_envio` (`fecha_envio`);

--
-- Indices de la tabla `gastos`
--
ALTER TABLE `gastos`
  ADD PRIMARY KEY (`id_gasto`),
  ADD KEY `idx_usuario_gasto` (`usuario_id_usuario`),
  ADD KEY `idx_categoria_gasto` (`categoria`),
  ADD KEY `idx_fecha_gasto` (`fecha_registro`);

--
-- Indices de la tabla `grupos`
--
ALTER TABLE `grupos`
  ADD PRIMARY KEY (`id_grupo`);

--
-- Indices de la tabla `grupos_usuarios`
--
ALTER TABLE `grupos_usuarios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_grupo_id` (`grupo_id`),
  ADD KEY `idx_usuario_id` (`usuario_id`);

--
-- Indices de la tabla `ingresos`
--
ALTER TABLE `ingresos`
  ADD PRIMARY KEY (`id_ingreso`),
  ADD KEY `idx_usuario_ingreso` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_ingreso` (`fecha_registro`);

--
-- Indices de la tabla `interacciones_bot_finanzas`
--
ALTER TABLE `interacciones_bot_finanzas`
  ADD PRIMARY KEY (`id_interaccion`),
  ADD KEY `idx_usuario_interaccion` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_interaccion` (`fecha_interaccion`);

--
-- Indices de la tabla `metas_ahorro`
--
ALTER TABLE `metas_ahorro`
  ADD PRIMARY KEY (`id_ahorro`),
  ADD KEY `idx_usuario_meta` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_objetivo` (`fecha_objetivo`);

--
-- Indices de la tabla `preguntas_bot_soporte`
--
ALTER TABLE `preguntas_bot_soporte`
  ADD PRIMARY KEY (`id_pregunta`),
  ADD KEY `idx_usuario_pregunta` (`usuario_id_usuario`);

--
-- Indices de la tabla `preguntas_encuesta`
--
ALTER TABLE `preguntas_encuesta`
  ADD PRIMARY KEY (`id_pregunta`),
  ADD KEY `idx_usuario_pregunta_encuesta` (`usuario_id_usuario`),
  ADD KEY `idx_formulario_pregunta` (`formulario_id_formulario`);

--
-- Indices de la tabla `recuperacion_contrasena`
--
ALTER TABLE `recuperacion_contrasena`
  ADD PRIMARY KEY (`id_recuperacion`),
  ADD KEY `idx_usuario_recuperacion` (`usuario_id_usuario`);

--
-- Indices de la tabla `registro_auditoria`
--
ALTER TABLE `registro_auditoria`
  ADD PRIMARY KEY (`id_auditoria`),
  ADD KEY `idx_usuario_auditoria` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_auditoria` (`fecha_registro`);

--
-- Indices de la tabla `respuestas_encuesta`
--
ALTER TABLE `respuestas_encuesta`
  ADD PRIMARY KEY (`id_respuesta`),
  ADD KEY `idx_usuario_respuesta` (`usuario_id_usuario`),
  ADD KEY `idx_formulario_respuesta` (`formulario_id_formulario`),
  ADD KEY `idx_pregunta_respuesta` (`pregunta_encuesta_id_pregunta`),
  ADD KEY `idx_fecha_respuesta` (`fecha_respuesta`);

--
-- Indices de la tabla `sesion`
--
ALTER TABLE `sesion`
  ADD PRIMARY KEY (`id_sesion`),
  ADD KEY `idx_usuario_sesion` (`usuario_id_usuario`),
  ADD KEY `idx_documento_sesion` (`documento`);

--
-- Indices de la tabla `trazabilidad_sistema`
--
ALTER TABLE `trazabilidad_sistema`
  ADD PRIMARY KEY (`id_traza`),
  ADD KEY `idx_usuario_traza` (`usuario_id_usuario`),
  ADD KEY `idx_fecha_traza` (`fecha`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `idx_documento` (`documento`),
  ADD UNIQUE KEY `idx_correo` (`correo_electronico`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `chat_grupo`
--
ALTER TABLE `chat_grupo`
  MODIFY `id_mensaje` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comunicados`
--
ALTER TABLE `comunicados`
  MODIFY `id_comunicado` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `documentos`
--
ALTER TABLE `documentos`
  MODIFY `id_documento` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `exportaciones`
--
ALTER TABLE `exportaciones`
  MODIFY `id_exportacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `formularios`
--
ALTER TABLE `formularios`
  MODIFY `id_formulario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `formularios_soporte`
--
ALTER TABLE `formularios_soporte`
  MODIFY `id_formulario` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `gastos`
--
ALTER TABLE `gastos`
  MODIFY `id_gasto` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `grupos`
--
ALTER TABLE `grupos`
  MODIFY `id_grupo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `grupos_usuarios`
--
ALTER TABLE `grupos_usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `ingresos`
--
ALTER TABLE `ingresos`
  MODIFY `id_ingreso` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `interacciones_bot_finanzas`
--
ALTER TABLE `interacciones_bot_finanzas`
  MODIFY `id_interaccion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `metas_ahorro`
--
ALTER TABLE `metas_ahorro`
  MODIFY `id_ahorro` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `preguntas_bot_soporte`
--
ALTER TABLE `preguntas_bot_soporte`
  MODIFY `id_pregunta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `preguntas_encuesta`
--
ALTER TABLE `preguntas_encuesta`
  MODIFY `id_pregunta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `recuperacion_contrasena`
--
ALTER TABLE `recuperacion_contrasena`
  MODIFY `id_recuperacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT de la tabla `registro_auditoria`
--
ALTER TABLE `registro_auditoria`
  MODIFY `id_auditoria` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `respuestas_encuesta`
--
ALTER TABLE `respuestas_encuesta`
  MODIFY `id_respuesta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sesion`
--
ALTER TABLE `sesion`
  MODIFY `id_sesion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `trazabilidad_sistema`
--
ALTER TABLE `trazabilidad_sistema`
  MODIFY `id_traza` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `chat_grupo`
--
ALTER TABLE `chat_grupo`
  ADD CONSTRAINT `chat_grupo_ibfk_1` FOREIGN KEY (`grupo_id`) REFERENCES `grupos` (`id_grupo`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `chat_grupo_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `comunicados`
--
ALTER TABLE `comunicados`
  ADD CONSTRAINT `comunicados_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `documentos`
--
ALTER TABLE `documentos`
  ADD CONSTRAINT `documentos_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `formularios`
--
ALTER TABLE `formularios`
  ADD CONSTRAINT `formularios_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `formularios_soporte`
--
ALTER TABLE `formularios_soporte`
  ADD CONSTRAINT `formularios_soporte_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `gastos`
--
ALTER TABLE `gastos`
  ADD CONSTRAINT `gastos_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `grupos_usuarios`
--
ALTER TABLE `grupos_usuarios`
  ADD CONSTRAINT `grupos_usuarios_ibfk_1` FOREIGN KEY (`grupo_id`) REFERENCES `grupos` (`id_grupo`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `grupos_usuarios_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `ingresos`
--
ALTER TABLE `ingresos`
  ADD CONSTRAINT `ingresos_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `interacciones_bot_finanzas`
--
ALTER TABLE `interacciones_bot_finanzas`
  ADD CONSTRAINT `interacciones_bot_finanzas_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `metas_ahorro`
--
ALTER TABLE `metas_ahorro`
  ADD CONSTRAINT `metas_ahorro_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `preguntas_bot_soporte`
--
ALTER TABLE `preguntas_bot_soporte`
  ADD CONSTRAINT `preguntas_bot_soporte_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `preguntas_encuesta`
--
ALTER TABLE `preguntas_encuesta`
  ADD CONSTRAINT `preguntas_encuesta_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `preguntas_encuesta_ibfk_2` FOREIGN KEY (`formulario_id_formulario`) REFERENCES `formularios` (`id_formulario`);

--
-- Filtros para la tabla `recuperacion_contrasena`
--
ALTER TABLE `recuperacion_contrasena`
  ADD CONSTRAINT `recuperacion_contrasena_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `registro_auditoria`
--
ALTER TABLE `registro_auditoria`
  ADD CONSTRAINT `registro_auditoria_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `respuestas_encuesta`
--
ALTER TABLE `respuestas_encuesta`
  ADD CONSTRAINT `respuestas_encuesta_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`),
  ADD CONSTRAINT `respuestas_encuesta_ibfk_2` FOREIGN KEY (`formulario_id_formulario`) REFERENCES `formularios` (`id_formulario`),
  ADD CONSTRAINT `respuestas_encuesta_ibfk_3` FOREIGN KEY (`pregunta_encuesta_id_pregunta`) REFERENCES `preguntas_encuesta` (`id_pregunta`);

--
-- Filtros para la tabla `sesion`
--
ALTER TABLE `sesion`
  ADD CONSTRAINT `sesion_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);

--
-- Filtros para la tabla `trazabilidad_sistema`
--
ALTER TABLE `trazabilidad_sistema`
  ADD CONSTRAINT `trazabilidad_sistema_ibfk_1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
