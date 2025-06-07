package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
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
        name = "cuenta"
)

public class CuentaEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "cuenta_id",
            nullable = false
    )
    private int cuenta_id;

    @OneToOne
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private PersonaEntity persona;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min=4, max=15, message = "El nombre de usuario debe tener entre 4 y 15 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]{4,15}$", message = "El nickname solo puede contener letras, números, guiones y guiones bajos (4-15 caracteres)")
    @Column(
            name = "nickname",
            nullable = false,
            unique = true,
            length = 15
    )
    private String nickname;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min=4, max=15, message = "La contraseña debe tener entre 4 y 15 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,15}$",
            message = "La contraseña debe tener entre 5 y 15 caracteres, con al menos una letra y un número"
    )
    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "rol",
            nullable = false
    )
    private Roles rol;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false
    )
    private Estado estado;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRol().name()));
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; //es el token el que indica si la cuenta ha expirado o no
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
        return this.estado == Estado.ACTIVO;
    }


}
