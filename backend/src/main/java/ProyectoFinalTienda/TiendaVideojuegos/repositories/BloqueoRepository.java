package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BloqueoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BloqueoRepository extends JpaRepository<BloqueoEntity, Integer> {

    @Query("SELECT b FROM BloqueoEntity b WHERE b.persona.personaId = ?1 AND CURRENT_DATE >= b.fecha_inicio AND (b.fecha_fin IS NULL OR CURRENT_DATE < b.fecha_fin)")
    Optional<BloqueoEntity> findVigenteByPersona(int personaId);

    @Query("SELECT b FROM BloqueoEntity b WHERE b.fecha_inicio <= CURRENT_DATE AND (b.fecha_fin IS NULL OR CURRENT_DATE <= b.fecha_fin)")
    List<BloqueoEntity> findPersonasEnListaNegraVigente();
}
