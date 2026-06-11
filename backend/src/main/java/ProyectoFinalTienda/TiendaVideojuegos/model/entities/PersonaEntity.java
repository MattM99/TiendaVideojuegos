package ProyectoFinalTienda.TiendaVideojuegos.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "persona"
)

public class PersonaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "persona_id"
    )
    private int personaId;



    @JsonIgnore
    @OneToOne(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private CuentaEntity cuenta;



    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<BloqueoEntity> blacklist = new ArrayList<>();



    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<AlquilerEntity> alquiler = new ArrayList<>();

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL
    )
    private List<ReservaEntity> reservas = new ArrayList<>();


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
    @Pattern(regexp = "\\d{7,8}", message = "El DNI debe tener entre 7 y 8 dígitos")
    private String dni;



    @Column(
            nullable = false,
            unique = true
    )
    @Email(message = "El email debe ser válido")
    private String email;



    @Column(
            nullable = false
    )
    private String telefono;
}
