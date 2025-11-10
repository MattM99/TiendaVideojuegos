package ProyectoFinalTienda.TiendaVideojuegos.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig  {
    @Bean
    public OpenAPI customOpenAPI(@Value("0.0.1-SNAPSHOT") String appversion) {
        return new OpenAPI()
                .info(new Info()
                        .title("Tienda de Videojuegos API")
                        .version(appversion)
                        .description("Sistema de gestión para una tienda de videojuegos")
                );
    }
}
