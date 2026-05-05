package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PagoRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PagoResponse;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PagoMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PagoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PagoService {
    @Autowired
    private PagoRepository pagoRepository;
    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private PagoMapper pagoMapper;

//    public PagoResponse crearPago(PagoRequest request) {
//        AlquilerEntity alquiler = alquilerRepository.findById(request.getAlquilerId())
//                .orElseThrow(() -> new RuntimeException("Alquiler no encontrado"));
//
//        BigDecimal precioBase = alquiler.getMontoDiarioAlquiler();
//        BigDecimal descuento = pagoCalculator.calcularDescuento(alquiler);
//        BigDecimal penalizacion = pagoCalculator.calcularPenalizacion(alquiler);
//
//        PagoEntity pago = PagoEntity.crear(alquiler, descuento, penalizacion, precioBase);
//
//        return pagoMapper.toResponse(pago);
//    }

}
