package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.*;
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
    private String nombre;

    @Column(
            nullable = false
    )
    private String apellido;

    @Column(
            nullable = false,
            unique = true
    )
    private String dni;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            nullable = false
    )
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
