package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
class PersonaRepositoryTest {

    @Autowired
    private PersonaRepository personaRepository;

    @Test
    public void savePersona(){
        PersonaEntity persona= PersonaEntity.builder()
                .nombre("Pedro")
                .apellido("Rodroguez")
                .dni("35344442")
                .email("nono@hotmail.com")
                .telefono("2234523455")
                .build();

        personaRepository.save(persona);
    }

    @Test
    @Transactional
    void findPersonaByNombre() {
        List<PersonaEntity> persona = personaRepository.findByNombre("Rodrigo");
        System.out.println("Persona = " + persona);
    }

    @Test
    @Transactional
    void findPersonaByDni() {
        PersonaEntity persona = personaRepository.findByDni("39348325").orElse(null);
        System.out.println("Persona = " + persona);
    }

    @Test
    @Transactional
    public void findAllPersonas() {
        List<PersonaEntity> personas = personaRepository.findAll();
        System.out.println("Personas = " + personas);
    }



    @Test
    @Transactional
    void findPersonasByApellido() {
        List<PersonaEntity> personas = personaRepository.findByApellido("Quesini");
        System.out.println("Personas = " + personas);
    }


    @Test
    @Transactional
    void getPersonaByEmail() {
        PersonaEntity persona = personaRepository.getPersonaByEmail("nico@hotmail.com").orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email proporcionado"));
        System.out.println("Persona = " + persona);
    }



    @Test
    @Transactional
    void deletePersonaByDni() {
        personaRepository.deleteByDni("39348325");
        List<PersonaEntity> personas = personaRepository.findAll();
        System.out.println("Personas: " + personas);
    }


}


