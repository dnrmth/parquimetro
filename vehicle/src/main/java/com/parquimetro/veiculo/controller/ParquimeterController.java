package com.parquimetro.veiculo.controller;

import com.parquimetro.veiculo.dto.ParquimeterRegisterDTO;
import com.parquimetro.veiculo.service.ParquimeterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "/parquimeter")
public class ParquimeterController {

    @Autowired
    private ParquimeterService parquimeterService;

    @Operation(
            summary = "Registro de parquímetro",
            description = """
                    A chamada deste endpoint é feita diretamente pelo serviço registro ou atualização de um parquímetro""")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parquímetro registrado"),
            @ApiResponse(responseCode = "500", description = "Erro nas informações inseridas")
    })
    @PostMapping("/register")
    public ResponseEntity<String> registerParquimeter(@RequestBody ParquimeterRegisterDTO parquimeterRegisterDTO) {
        parquimeterService.registerParquimeter(parquimeterRegisterDTO);
        return ok("Parquímetro registrado");
    }
}