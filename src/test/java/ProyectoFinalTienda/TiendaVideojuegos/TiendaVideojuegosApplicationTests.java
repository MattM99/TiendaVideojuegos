package ProyectoFinalTienda.TiendaVideojuegos;

import ProyectoFinalTienda.TiendaVideojuegos.model.entities.VideojuegoEntity;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Generos;
import ProyectoFinalTienda.TiendaVideojuegos.repositories.VideojuegoRepository;
import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.Year;
import java.util.List;

@SpringBootTest
class TiendaVideojuegosApplicationTests {

	@Autowired
	private VideojuegoRepository videojuegoRepository;

	@Autowired
	private VideojuegoService videojuegoService;

	@Test
	@Transactional
	@Rollback(true)
	void contextLoads() {
		/*
		VideojuegoEntity v = new VideojuegoEntity();
		v.setTitulo("FIFA 22");
		v.setDesarrollador("EA Sports");
		v.setGenero(Generos.DEPORTES);
		v.setLanzamiento(Year.of(2022)); // Solo funciona si tenés un converter en JPA
		v.setDescripcion("Fútbol multijugador");
		v.setMultijugador(true);
		*/

		/*
		VideojuegoEntity detroit = new VideojuegoEntity();
		detroit.setTitulo("Detroit: Become Human");
		detroit.setDesarrollador("Quantic Dream");
		detroit.setGenero(Generos.AVENTURA);
		detroit.setLanzamiento(Year.of(2018));
		detroit.setDescripcion("Un juego narrativo donde tus decisiones afectan el destino de androides y humanos en un futuro cercano.");
		detroit.setMultijugador(false); // Es un juego singleplayer
		 */


		//videojuegoService.guardar(v);
		videojuegoService.eliminar(2);
		//videojuegoService.guardar(detroit);

		List<VideojuegoEntity> lista = videojuegoService.buscarMultijugadores();
		System.out.println("Multijugadores encontrados:");
		lista.forEach(System.out::println);
	}

}
