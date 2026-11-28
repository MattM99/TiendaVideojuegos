package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Integer> {

    @Query("""
            SELECT p FROM PersonaEntity p
            WHERE (:dniExcluido IS NULL OR p.dni <> :dniExcluido)
              AND (:busqueda IS NULL
                OR LOCATE(LOWER(:busqueda), LOWER(p.dni)) > 0
                OR LOCATE(LOWER(:busqueda), LOWER(p.nombre)) > 0
                OR LOCATE(LOWER(:busqueda), LOWER(p.apellido)) > 0
                OR LOCATE(LOWER(:busqueda), LOWER(p.email)) > 0
                OR LOCATE(LOWER(:busqueda), LOWER(p.telefono)) > 0)
            """)
    Page<PersonaEntity> buscarListado(
            @Param("dniExcluido") String dniExcluido,
            @Param("busqueda") String busqueda,
            Pageable paginacion
    );

    Page<PersonaEntity> findByNombre(String nombre, Pageable paginacion);

    Page<PersonaEntity> findByApellido(String apellido, Pageable paginacion);

    Optional<PersonaEntity> findByDni(String dni);

    @Query("SELECT p FROM PersonaEntity p WHERE p.email = ?1")
    Optional<PersonaEntity> getPersonaByEmail(String email);

    @Transactional
    Optional<PersonaEntity> deleteByDni(String dni);

}
