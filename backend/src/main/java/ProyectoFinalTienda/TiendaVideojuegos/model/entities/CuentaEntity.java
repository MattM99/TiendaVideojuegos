package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Estado;
import ProyectoFinalTienda.TiendaVideojuegos.model.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "La cuenta debe estar asociada a una persona")
    private PersonaEntity persona;


    @Column(
            name = "nickname",
            nullable = false,
            unique = true,
            length = 15
    )
    @NotBlank(message = "El nickname es obligatorio")
    @Size(min = 3, max = 50, message = "El nickname debe tener entre 3 y 50 caracteres")
    private String nickname;

    @Column(
            name = "password",
            nullable = false
    )
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "rol",
            nullable = false
    )
    @NotNull(message = "El rol es obligatorio")
    private Roles rol;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false
    )
    @NotNull(message = "El estado es obligatorio")
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
