package ProyectoFinalTienda.TiendaVideojuegos;

import ProyectoFinalTienda.TiendaVideojuegos.services.VideojuegoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TiendaVideojuegosApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TiendaVideojuegosApplication.class, args);

		VideojuegoService servicio = context.getBean(VideojuegoService.class);
		System.out.println(servicio.buscarMultijugadores());
	}

}
