package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Integer> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
