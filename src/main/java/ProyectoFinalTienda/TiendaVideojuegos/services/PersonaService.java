package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaPatchRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaMapper personaMapper;


    public PersonaResponse crearPersona(PersonaCreateOrReplaceRequest dto) {
        PersonaEntity entity = personaMapper.convertirDTOaEntidad(dto);
        return personaMapper.convertirEntidadADTO(personaRepository.save(entity));
    }


    public PersonaResponse buscarPorId(int id) {
        PersonaEntity entity = personaRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        return personaMapper.convertirEntidadADTO(entity);
    }

    public List<PersonaResponse> buscarPorNombre(String nombre) {
        List<PersonaEntity> personas = personaRepository.findByNombre(nombre);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el nombre: " + nombre);
        }
        return personas.stream().map(personaMapper::convertirEntidadADTO).toList();
    }

    public List<PersonaResponse> buscarPorApellido(String apellido) {
        List<PersonaEntity> personas = personaRepository.findByApellido(apellido);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el apellido: " + apellido);
        }
        return personas.stream().map(personaMapper::convertirEntidadADTO).toList();
    }


    public PersonaResponse buscarPorDni(String dni) {
        PersonaEntity entity = personaRepository.findByDni(dni)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));
        return personaMapper.convertirEntidadADTO(entity);
    }


    public PersonaResponse buscarPorEmail(String email) {
        PersonaEntity entity = personaRepository.getPersonaByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
        return personaMapper.convertirEntidadADTO(entity);
    }


    public PersonaResponse actualizar(String email, PersonaPatchRequest dto) {
        PersonaEntity persona = personaRepository.getPersonaByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));

        if (dto.getNombre() != null) persona.setNombre(dto.getNombre());
        if (dto.getApellido() != null) persona.setApellido(dto.getApellido());
        if (dto.getTelefono() != null) persona.setTelefono(dto.getTelefono());
        if (dto.getDni() != null) persona.setDni(dto.getDni());

        return personaMapper.convertirEntidadADTO(personaRepository.save(persona));
    }


    public void eliminarPorDni(String dni) {
        personaRepository.deleteByDni(dni)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));
    }


}
