package ProyectoFinalTienda.TiendaVideojuegos.model.enums;

import java.util.Set;

public enum Roles {
    EMPLEADO,
    ADMINISTRADOR,
    FOUNDER;

    public Set<Roles> getRolesIncluidos() {
        return switch (this) {
            case FOUNDER -> Set.of(FOUNDER, ADMINISTRADOR, EMPLEADO);
            case ADMINISTRADOR -> Set.of(ADMINISTRADOR, EMPLEADO);
            case EMPLEADO -> Set.of(EMPLEADO);
        };
    }
}
