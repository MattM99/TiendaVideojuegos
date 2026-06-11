package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.PersonaPatchRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.PersonaResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.PersonaNoEncontradaException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.PersonaMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.CuentaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.CuentaRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaMapper personaMapper;

    @Autowired
    private CuentaRepository cuentaRepository;


    public PersonaResponse crearPersona(PersonaCreateOrReplaceRequest dto) {
        PersonaEntity entity = personaMapper.convertirDTOaEntidad(dto);
        return personaMapper.convertirEntidadADTO(personaRepository.save(entity));
    }

    public Page<PersonaResponse> listarTodos(Pageable paginacion)
    {
        return personaRepository.findAll(paginacion)
                .map(personaMapper::convertirEntidadADTO);
    }


    public PersonaResponse buscarPorId(int id) {
        PersonaEntity entity = personaRepository.findById(id)
                .orElseThrow(() -> new PersonaNoEncontradaException("Persona no encontrada con id: " + id));
        return personaMapper.convertirEntidadADTO(entity);
    }

    public Page<PersonaResponse> buscarPorNombre(String nombre, Pageable paginacion) {
        Page<PersonaEntity> personas = personaRepository.findByNombre(nombre, paginacion);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el nombre: " + nombre);
        }
        return personas
                .map(personaMapper::convertirEntidadADTO);
    }

    public Page<PersonaResponse> buscarPorApellido(String apellido, Pageable paginacion) {
        Page<PersonaEntity> personas = personaRepository.findByApellido(apellido, paginacion);
        if (personas.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontraron usuarios con el apellido: " + apellido);
        }
        return personas
                .map(personaMapper::convertirEntidadADTO);
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

    public PersonaResponse actualizar(String dni, PersonaPatchRequest dto) {
        PersonaEntity persona = personaRepository.findByDni(dni)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));

        String usernameActual = SecurityContextHolder.getContext().getAuthentication().getName();
        CuentaEntity cuentaActual = cuentaRepository.findByNickname(usernameActual)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario autenticado no encontrado"));

        Roles rolActual = cuentaActual.getRol();

        if (!rolActual.equals(Roles.FOUNDER)) {

            if (rolActual.equals(Roles.ADMINISTRADOR)) {
                boolean personaTienesCuenta = persona.getCuenta() != null;
                
                if (personaTienesCuenta) {
                    Roles rolPersonaAModificar = persona.getCuenta().getRol();
                    if (rolPersonaAModificar.equals(Roles.FOUNDER)) {
                        throw new AccessDeniedException("Los administradores no pueden modificar los datos del FOUNDER");
                    }
                    if (rolPersonaAModificar.equals(Roles.ADMINISTRADOR)) {
                        throw new AccessDeniedException("Los administradores no pueden modificar los datos de otros administradores");
                    }
                }
            }

            else if (rolActual.equals(Roles.EMPLEADO)) {
                boolean personaTienesCuenta = persona.getCuenta() != null;
                
                if (personaTienesCuenta) {
                    throw new AccessDeniedException("Los empleados solo pueden modificar datos de clientes, no de usuarios con cuenta");
                }
            }
        }
        
        if (dto.getNombre() != null) persona.setNombre(dto.getNombre());
        if (dto.getApellido() != null) persona.setApellido(dto.getApellido());
        if (dto.getTelefono() != null) persona.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) persona.setEmail(dto.getEmail());

        return personaMapper.convertirEntidadADTO(personaRepository.save(persona));
    }


    public void eliminarPorDni(String dni) {
        personaRepository.deleteByDni(dni)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con DNI: " + dni));
    }


}
