package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PersonaRepository extends JpaRepository<PersonaEntity, Integer> {

    List<PersonaEntity> findByNombre(String nombre);

    List<PersonaEntity> findByApellido(String apellido);

    Optional<PersonaEntity> findByDni(String dni);


    @Query("SELECT p FROM PersonaEntity p WHERE p.email = ?1")
    PersonaEntity getPersonaByEmail(String email);


    @Transactional
    @Modifying
    @Query(value = "UPDATE persona SET nombre = ?1 WHERE email = ?2", nativeQuery = true)
    void updatePersonaNombreByEmail(String nombre, String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE persona SET apellido = ?1 WHERE email = ?2", nativeQuery = true)
    void updatePersonaApellidoByEmail(String apellido, String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE persona SET telefono = ?1 WHERE email = ?2", nativeQuery = true)
    void updatePersonaTelefonoByEmail(String telefono, String email);

    @Transactional
    @Modifying
    @Query(value = "UPDATE persona SET dni = ?1 WHERE email = ?2", nativeQuery = true)
    void updatePersonaDniByEmail(String dni, String email);

    @Transactional
    void deleteByDni(String dni);


}
