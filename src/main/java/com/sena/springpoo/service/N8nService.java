package com.sena.springpoo.service;

import com.sena.springpoo.models.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class N8nService {

    private static final Logger logger = LoggerFactory.getLogger(N8nService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${n8n.webhook.url}")
    private String webhookUrl;

    /**
     * Envía una notificación de cambio de usuario a n8n de forma asíncrona.
     * Purifica el payload excluyendo campos sensibles como la contraseña.
     *
     * @param accion  Tipo de acción realizada: CREATE, UPDATE, DELETE
     * @param usuario Datos del usuario involucrado
     */
    public void notificarCambioAsync(String accion, Usuario usuario) {
        CompletableFuture.runAsync(() -> {
            try {
                if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.contains("tu-instancia")) {
                    logger.warn("[N8nService] URL de Webhook no configurada o es de plantilla ({}). Omitiendo envío HTTP.", webhookUrl);
                    return;
                }

                // Purificación del payload: Excluimos la contraseña (contrasena) por seguridad
                Map<String, Object> payload = new HashMap<>();
                payload.put("action", accion);
                payload.put("idUsuario", usuario.getIdUsuario());
                payload.put("primerNombre", usuario.getPrimerNombre());
                payload.put("segundoNombre", usuario.getSegundoNombre());
                payload.put("primerApellido", usuario.getPrimerApellido());
                payload.put("segundoApellido", usuario.getSegundoApellido());
                payload.put("tipoDocumento", usuario.getTipoDocumento());
                payload.put("documento", usuario.getDocumento());
                payload.put("celular", usuario.getCelular());
                payload.put("correo", usuario.getCorreo());

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

                logger.info("[N8nService] Enviando notificación asíncrona a n8n. Acción: {} | Usuario: {}", accion, usuario.getCorreo());
                restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
                logger.info("[N8nService] Notificación enviada correctamente a n8n.");

            } catch (Exception e) {
                logger.error("[N8nService] Error al enviar notificación a n8n de forma asíncrona: {}", e.getMessage(), e);
            }
        });
    }
}
