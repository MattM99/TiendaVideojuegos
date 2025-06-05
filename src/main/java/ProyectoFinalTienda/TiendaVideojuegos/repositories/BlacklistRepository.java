package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Integer> {

    @Query("SELECT b FROM BlackListEntity b WHERE b.persona.id = ?1 AND CURRENT_DATE >= b.fechaInicio AND (b.fechaFin IS NULL OR CURRENT_DATE <= b.fechaFin)")
    Optional<BlacklistEntity> findVigenteByPersona(int personaId);

    @Query("SELECT b FROM BlackListEntity b WHERE b.fechaInicio <= CURRENT_DATE AND (b.fechaFin IS NULL OR CURRENT_DATE <= b.fechaFin)")
    List<BlacklistEntity> findPersonasEnListaNegraVigente();
}
