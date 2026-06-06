package ProyectoFinalTienda.TiendaVideojuegos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class TiendaVideojuegosApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaVideojuegosApplication.class, args);
        System.out.println("Hola Mundo");
	}

}
