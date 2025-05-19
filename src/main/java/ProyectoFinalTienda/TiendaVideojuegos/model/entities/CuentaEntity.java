package com.localvideojuegos.ProyectoFinal.model.entities;

import com.localvideojuegos.ProyectoFinal.model.enums.Estado;
import com.localvideojuegos.ProyectoFinal.model.enums.Roles;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class CuentaEntity {
    private int cuenta_id;
    private String nickname;
    private String password;
    private Roles rol;
    private Estado estado;
}
