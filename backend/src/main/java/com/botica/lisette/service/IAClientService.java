package com.botica.lisette.service;

import com.botica.lisette.ia.IA;
import com.botica.lisette.ia.IAException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/** Llama al módulo de Python. Si no responde, lanza IAException con un mensaje legible. */
@Service
public class IAClientService {

    private final RestClient cliente;

    public IAClientService(@Value("${app.ia.url}") String url,
                           @Value("${app.ia.api-key:}") String apiKey) {
        HttpClient http = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(3))
        .build();
        JdkClientHttpRequestFactory fabrica = new JdkClientHttpRequestFactory(http);
        fabrica.setReadTimeout(Duration.ofSeconds(30));
        RestClient.Builder b = RestClient.builder().baseUrl(url).requestFactory(fabrica);
        if (!apiKey.isBlank()) b.defaultHeader("X-API-Key", apiKey);
        this.cliente = b.build();
    }

    public IA.Respuesta analizar(IA.Solicitud solicitud) {
        try {
            return cliente.post().uri("/analisis").body(solicitud).retrieve().body(IA.Respuesta.class);
        } catch (Exception e) {
            throw new IAException("El módulo de predicción no está disponible. "
                    + "Verifica que el servicio de Python esté encendido.", e);
        }
    }
}