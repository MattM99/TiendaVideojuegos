package ProyectoFinalTienda.TiendaVideojuegos.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
}
