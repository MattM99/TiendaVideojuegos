package ProyectoFinalTienda.TiendaVideojuegos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegisterRequest {
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min=4, max=15, message = "El nombre de usuario debe tener entre 4 y 15 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]{4,15}$", message = "El nickname solo puede contener letras, números, guiones y guiones bajos (4-15 caracteres)")
    private String nickname;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min=5, max=15, message = "La contraseña debe tener entre 5 y 15 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,15}$",
            message = "La contraseña debe tener entre 5 y 15 caracteres, con al menos una letra y un número"
    )
    private String password;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String rol;
}
