package com.parquimetro.veiculo.service;

import com.parquimetro.veiculo.client.ParquimeterClient;
import com.parquimetro.veiculo.dto.AdditionalHoursRegisterDTO;
import com.parquimetro.veiculo.dto.AdditionalPaymentDTO;
import com.parquimetro.veiculo.dto.VehicleDto;
import com.parquimetro.veiculo.dto.VehicleRegisterDTO;
import com.parquimetro.veiculo.enums.VehicleType;
import com.parquimetro.veiculo.model.Parquimeter;
import com.parquimetro.veiculo.model.Payment;
import com.parquimetro.veiculo.model.Vehicle;
import com.parquimetro.veiculo.repository.ParquimeterRepository;
import com.parquimetro.veiculo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VehicleService {

    @Autowired
    ParquimeterRepository parquimeterRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    ParquimeterClient parquimeterClient;

    public void registerVehicle(VehicleRegisterDTO vehicleRegisterDTO) {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType(vehicleRegisterDTO.vehicleType());
        vehicle.setPlate(vehicleRegisterDTO.plate());
        vehicle.setEntryTime(LocalDateTime.now());
        vehicle.setEstimatedHours(vehicleRegisterDTO.estimatedHours());

        Parquimeter parquimeter = parquimeterRepository.findById(vehicleRegisterDTO.parquimeterId()).get();

        Double parkingPrice = 0.0;
        parkingPrice = vehicleRegisterDTO.vehicleType().equals(VehicleType.CAR)
                ? calculatePrice(vehicleRegisterDTO.estimatedHours(), parquimeter.getCarPriceHour())
                : calculatePrice(vehicleRegisterDTO.estimatedHours(), parquimeter.getMotorcyclePriceHour());

        vehicle.setParquimeter(parquimeter);
        vehicle.setParkingPrice(parkingPrice);

        Payment payment = new Payment();
        payment.setNumberCard(vehicleRegisterDTO.paymentRegisterDTO().numberCard());
        payment.setCvv(vehicleRegisterDTO.paymentRegisterDTO().cvv());
        payment.setDate(vehicleRegisterDTO.paymentRegisterDTO().date());
        payment.setPaymentType(vehicleRegisterDTO.paymentRegisterDTO().paymentType());
        payment.setPaymentAmount(parkingPrice);

        vehicle.getPayments().add(payment);

        VehicleDto vehicleDto = new VehicleDto(vehicleRegisterDTO.plate(), LocalDateTime.now(),
                LocalDateTime.now().plusHours(vehicleRegisterDTO.estimatedHours()));
        parquimeterClient.sendParkingInformationToSave(vehicleDto);

        vehicleRepository.save(vehicle);
    }

    public AdditionalPaymentDTO payAdditionalHours(AdditionalHoursRegisterDTO additionalHoursRegisterDTO) {
        Vehicle vehicle = vehicleRepository.findByPlate(additionalHoursRegisterDTO.plate());

        LocalDateTime now = LocalDateTime.now();

        var estimatedHours = vehicle.getEntryTime().plusHours(vehicle.getEstimatedHours().longValue()).toLocalTime();

        double additionalHours = 0;
        if (estimatedHours.isBefore(now.toLocalTime())) {
            int minutes = now.getHour() * 60 + now.getMinute();

            int estimatedMinutes = estimatedHours.getHour() * 60 + estimatedHours.getMinute();

            additionalHours = Math.ceil((double) (minutes - estimatedMinutes) / 60);
        }

        Payment payment = getPayment(additionalHoursRegisterDTO, vehicle, additionalHours);

        vehicle.getPayments().add(payment);

        vehicleRepository.save(vehicle);

        return new AdditionalPaymentDTO(additionalHours, payment.getPaymentAmount());
    }

    private Payment getPayment(AdditionalHoursRegisterDTO additionalHoursRegisterDTO, Vehicle vehicle, double additionalHours) {
        Double parkingPrice;
        parkingPrice = vehicle.getVehicleType().equals(VehicleType.CAR)
                ? calculatePrice(additionalHours, vehicle.getParquimeter().getCarPriceHour())
                : calculatePrice(additionalHours, vehicle.getParquimeter().getMotorcyclePriceHour());

        Payment payment = new Payment();
        payment.setNumberCard(additionalHoursRegisterDTO.paymentRegisterDTO().numberCard());
        payment.setCvv(additionalHoursRegisterDTO.paymentRegisterDTO().cvv());
        payment.setDate(additionalHoursRegisterDTO.paymentRegisterDTO().date());
        payment.setPaymentType(additionalHoursRegisterDTO.paymentRegisterDTO().paymentType());
        payment.setPaymentAmount(parkingPrice);

        return payment;
    }

    private Double calculatePrice(Integer hours, Double vehiclePrice) {
        return hours * vehiclePrice;
    }

    private Double calculatePrice(double hours, Double vehiclePrice) {
        return hours * vehiclePrice;
    }
}