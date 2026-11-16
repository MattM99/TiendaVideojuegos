package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;


@Repository
public interface AlquilerRepository extends JpaRepository<AlquilerEntity, Integer> {

    @Query("""
       SELECT a FROM AlquilerEntity a
       WHERE a.persona.personaId = :id
       AND a.activo = true
       """)
    Page<AlquilerEntity> findByPersonaId(
            @Param("id") int id,
            Pageable paginacion
    );

    Page<AlquilerEntity> findByActivoTrue(Pageable paginacion);

    Page<AlquilerEntity> findByFechaInicioBetweenAndActivoTrue(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable paginacion
    );

    Optional<AlquilerEntity> findByAlquilerIdAndActivoTrue(int alquilerId);

}
