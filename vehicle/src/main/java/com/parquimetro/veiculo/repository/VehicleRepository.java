package com.parquimetro.veiculo.repository;

import com.parquimetro.veiculo.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    Vehicle findByPlate(String plate);
}
