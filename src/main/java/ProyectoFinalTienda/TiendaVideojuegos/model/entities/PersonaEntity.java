package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(
        name = "persona"
)

public class PersonaEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "persona_id"
    )
    private int personaId;

    @OneToOne(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private CuentaEntity cuenta;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<BlacklistEntity> blacklist;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<AlquilerEntity> alquiler;

    @Column(
            nullable = false
    )
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min=2, max=50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El nombre solo puede contener letras y espacios")
    private String nombre;

    @Column(
            nullable = false
    )
    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min=2, max=50, message = "El apellido debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$", message = "El apellido solo puede contener letras y espacios")
    private String apellido;

    @Column(
            nullable = false,
            unique = true
    )
    @NotBlank(message = "El DNI no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener entre 7 y 8 dígitos}")
    private String dni;

    @Column(
            nullable = false,
            unique = true
    )
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @Column(
            nullable = false
    )
    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new
                SimpleGrantedAuthority((cuenta.getRol().name())));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //va a ser en funcion de la caducidad del token JWT, se usan comprueba en el service
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
