package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.AlquilerResponse;
import ProyectoFinalTienda.TiendaVideojuegos.exception.AlquilerNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.BusinessException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.mappers.AlquilerMapper;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.BlacklistEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.BlacklistRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private AlquilerMapper alquilerMapper;

    public AlquilerResponse guardar(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = personaRepository.findById(request.getPersonaID()).orElseThrow();

        // Verificar si la persona está en lista negra, si está lanza excepción.
        blackListService.verificarNoEstaEnListaNegra(request.getPersonaID());

        // Validar fechas
        if (request.getFecha_retiro().isAfter(request.getFecha_devolucion())) {
            throw new BusinessException("La fecha de retiro no puede ser posterior a la fecha de devolución.");
        }

        AlquilerEntity entity = alquilerMapper.toEntity(request, persona);

        return alquilerMapper.toResponse(alquilerRepository.save(entity));
    }

    public void eliminar(int id){
        if (!alquilerRepository.existsById(id)) {
            throw new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado.");
        }
        alquilerRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<AlquilerResponse> obtenerTodos(){
        List<AlquilerEntity> alquileres = alquilerRepository.findAll();
        if (alquileres.isEmpty()) {
            throw new AlquilerNoEncontradoException("No se encontró ningún alquiler en el sistema.");
        }
        return alquilerMapper.toResponseList(alquileres);
    }

    // Buscar por id con excepción si no existe
    public AlquilerResponse buscarPorId(int id){
        AlquilerEntity entity = alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
        return alquilerMapper.toResponse(entity);
    }

    // Buscar por usuario con excepción si lista vacía
    public List<AlquilerResponse> buscarPorUsuario(int personaId){
        List<AlquilerEntity> alquileres = alquilerRepository.findByPersonaId(personaId);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return alquilerMapper.toResponseList(alquileres);
    }

}
