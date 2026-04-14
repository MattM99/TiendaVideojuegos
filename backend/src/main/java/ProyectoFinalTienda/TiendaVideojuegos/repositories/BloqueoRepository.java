package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BloqueoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BloqueoRepository extends JpaRepository<BloqueoEntity, Integer> {

    @Query("SELECT b FROM BloqueoEntity b WHERE b.persona.personaId = ?1 AND CURRENT_DATE >= b.fechaInicio AND (b.fechaFin IS NULL OR CURRENT_DATE < b.fechaFin)")
    Optional<BloqueoEntity> findVigenteByPersona(int personaId);

    @Query("SELECT b FROM BloqueoEntity b WHERE b.fechaInicio <= CURRENT_DATE AND (b.fechaFin IS NULL OR CURRENT_DATE <= b.fechaFin)")
    List<BloqueoEntity> findPersonasEnListaNegraVigente();
}
