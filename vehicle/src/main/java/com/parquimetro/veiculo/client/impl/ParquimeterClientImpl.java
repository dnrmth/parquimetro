package com.parquimetro.veiculo.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.parquimetro.veiculo.client.ParquimeterClient;
import com.parquimetro.veiculo.dto.VehicleDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class ParquimeterClientImpl implements ParquimeterClient {

    public void sendParkingInformationToSave(VehicleDto vehicleDto) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            objectMapper.findAndRegisterModules();

            String vehicleDtoAsString = objectMapper.writeValueAsString(vehicleDto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/parking"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(vehicleDtoAsString, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível fazer a requisição");
        }
    }

}
