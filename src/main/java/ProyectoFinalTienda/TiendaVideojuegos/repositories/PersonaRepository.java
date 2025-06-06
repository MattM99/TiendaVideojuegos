package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Integer> {

    List<PersonaEntity> findByNombre(String nombre);

    List<PersonaEntity> findByApellido(String apellido);

    Optional<PersonaEntity> findByDni(String dni);

    @Query("SELECT p FROM PersonaEntity p WHERE p.email = ?1")
    Optional<PersonaEntity> getPersonaByEmail(String email);

    @Transactional
    Optional<PersonaEntity> deleteByDni(String dni);

}
