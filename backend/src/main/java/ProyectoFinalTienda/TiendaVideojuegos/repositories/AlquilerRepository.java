package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface AlquilerRepository extends JpaRepository<AlquilerEntity, Integer> {

    @Query("SELECT a FROM AlquilerEntity a WHERE a.persona.personaId = :id")
    Page<AlquilerEntity> findByPersonaId(@Param("id") int id, Pageable paginacion);

    Page<AlquilerEntity> findByFechaInicioBetween(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable
    );


}
