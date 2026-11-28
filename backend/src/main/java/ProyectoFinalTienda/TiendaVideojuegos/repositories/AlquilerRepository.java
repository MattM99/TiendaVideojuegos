package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.EstadoAlquiler;
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

    @Query("""
            SELECT a FROM AlquilerEntity a
            LEFT JOIN a.persona p
            WHERE (:incluirInactivos = true OR a.activo = true)
            AND (:estado IS NULL OR a.estadoAlquiler = :estado)
            AND (:fechaInicio IS NULL OR :fechaFin IS NULL OR a.fechaInicio BETWEEN :fechaInicio AND :fechaFin)
            AND (:persona IS NULL
                OR LOCATE(LOWER(:persona), LOWER(p.nombre)) > 0
                OR LOCATE(LOWER(:persona), LOWER(p.apellido)) > 0)
            """)
    Page<AlquilerEntity> buscarListado(
            @Param("incluirInactivos") boolean incluirInactivos,
            @Param("estado") EstadoAlquiler estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("persona") String persona,
            Pageable paginacion
    );

    Optional<AlquilerEntity> findByAlquilerIdAndActivoTrue(int alquilerId);

}
