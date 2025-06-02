package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlquilerRepository extends JpaRepository<AlquilerEntity, Integer> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
