package ProyectoFinalTienda.TiendaVideojuegos.services;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.requests.AlquilerCreateOrReplaceRequest;
import ProyectoFinalTienda.TiendaVideojuegos.exception.AlquilerNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.exception.UsuarioNoEncontradoException;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.PersonaEntity;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.AlquilerRepository;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;
    @Autowired
    private PersonaRepository personaRepository;

    public AlquilerEntity guardar(AlquilerCreateOrReplaceRequest request) {
        PersonaEntity persona = personaRepository.findById(request.getPersonaID())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado."));

        AlquilerEntity entity = request.toEntity(persona);

        return alquilerRepository.save(entity);
    }

    public void eliminar(int id){
        if (!alquilerRepository.existsById(id)) {
            throw new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado.");
        }
        alquilerRepository.deleteById(id);
    }

    // Obtener todos con validación de lista vacía
    public List<AlquilerEntity> obtenerTodos(){
        List<AlquilerEntity> alquileres = alquilerRepository.findAll();
        if (alquileres.isEmpty()) {
            throw new AlquilerNoEncontradoException("No se encontró ningún alquiler en el sistema.");
        }
        return alquileres;
    }

    // Buscar por id con excepción si no existe
    public AlquilerEntity buscarPorId(int id){
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new AlquilerNoEncontradoException("Alquiler con id: " + id + " no encontrado."));
    }

    // Buscar por usuario con excepción si lista vacía
    public List<AlquilerEntity> buscarPorUsuario(int personaId){
        List<AlquilerEntity> alquileres = alquilerRepository.findByPersonaId(personaId);
        if (alquileres.isEmpty()) {
            throw new UsuarioNoEncontradoException("No se encontró ningún alquiler con el usuario de id: " + personaId);
        }
        return alquileres;
    }

}
