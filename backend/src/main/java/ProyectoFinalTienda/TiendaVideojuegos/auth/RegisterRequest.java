package ProyectoFinalTienda.TiendaVideojuegos.auth;

import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Solo letras y espacios")
    private String nombre;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener entre 7 y 8 dígitos")
    private String dni;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "^\\d{7,15}$", message = "El teléfono debe contener solo números")
    private String telefono;

    private String rol;
}
