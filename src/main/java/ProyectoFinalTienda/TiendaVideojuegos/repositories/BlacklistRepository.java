package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Integer> {

    @Query("SELECT b FROM BlacklistEntity b WHERE b.persona.personaId = ?1 AND CURRENT_DATE >= b.fecha_inicio AND (b.fecha_fin IS NULL OR CURRENT_DATE <= b.fecha_fin)")
    Optional<BlacklistEntity> findVigenteByPersona(int personaId);

    @Query("SELECT b FROM BlacklistEntity b WHERE b.fecha_inicio <= CURRENT_DATE AND (b.fecha_fin IS NULL OR CURRENT_DATE <= b.fecha_fin)")
    List<BlacklistEntity> findPersonasEnListaNegraVigente();
}
