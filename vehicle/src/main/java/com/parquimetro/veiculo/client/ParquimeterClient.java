package com.parquimetro.veiculo.client;

import com.parquimetro.veiculo.dto.VehicleDto;

public interface ParquimeterClient {

    void sendParkingInformationToSave(VehicleDto vehicleDto);

}
