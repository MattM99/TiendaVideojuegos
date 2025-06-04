package ProyectoFinalTienda.TiendaVideojuegos.auth;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String rol;
}
