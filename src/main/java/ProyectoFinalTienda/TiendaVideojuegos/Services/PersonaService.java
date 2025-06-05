package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    public PersonaEntity crearPersona(PersonaEntity persona) {
        return personaRepository.save(persona);
    }

    public PersonaEntity buscarPorId(int id){
        return personaRepository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    public List<PersonaEntity> buscarPorNombre(String nombre) {
        List<PersonaEntity> personas = personaRepository.findByNombre(nombre);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el nombre: " + nombre);
        }
        return personas;
    }

    public List<PersonaEntity> buscarPorApellido(String apellido) {
        List<PersonaEntity> personas = personaRepository.findByApellido(apellido);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el apellido: " + apellido);
        }
        return personas;
    }


    public PersonaEntity buscarPorDni(String dni){
        return personaRepository.findByDni(dni).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));
    }

    public PersonaEntity buscarPorEmail(String email){
        return personaRepository.getPersonaByEmail(email).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    public PersonaEntity actualizar(String email, PersonaEntity personaActualizada) {
        PersonaEntity persona = personaRepository.getPersonaByEmail(email).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
        persona.setNombre(personaActualizada.getNombre());
        if(personaActualizada.getApellido() != null) {
            persona.setApellido(personaActualizada.getApellido());
        }
        if(personaActualizada.getTelefono() != null) {
            persona.setTelefono(personaActualizada.getTelefono());
        }
        if(personaActualizada.getDni() != null) {
            persona.setDni(personaActualizada.getDni());
        }
        if(personaActualizada.getNombre() != null) {
            persona.setNombre(personaActualizada.getNombre());
        }

        return personaRepository.save(persona);
    }


    public void eliminarPorDni(String dni) {
        personaRepository.deleteByDni(dni).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));
    }

}
