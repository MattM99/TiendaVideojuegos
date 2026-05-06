package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository <PagoEntity, Integer> {
}
