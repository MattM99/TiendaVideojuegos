package ProyectoFinalTienda.TiendaVideojuegos.dtos.requests;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CambiarRolRequest {
    @NotBlank(message = "El rol no puede estar vacío")
    private String nuevoRol;

    public String getNuevoRol() {
        return nuevoRol;
    }
}
