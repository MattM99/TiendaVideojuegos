package ProyectoFinalTienda.TiendaVideojuegos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@SpringBootApplication
public class TiendaVideojuegosApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaVideojuegosApplication.class, args);
		System.out.println("Hola Mundo");
	}

}
