package ProyectoFinalTienda.TiendaVideojuegos.repositories;

import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.TopClientesResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.TopGenerosResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.TopJuegosResponse;
import ProyectoFinalTienda.TiendaVideojuegos.dtos.responses.TopPlataformasResponse;
import ProyectoFinalTienda.TiendaVideojuegos.model.entities.AlquilerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportesRepository extends JpaRepository<AlquilerEntity, Integer> {

    @Query("""
    SELECT COUNT(a)
    FROM AlquilerEntity a
""")
    Long contarTotalAlquileres();


    @Query("""
    SELECT COUNT(a)
    FROM AlquilerEntity a
    WHERE a.estadoAlquiler = 'EN_CURSO'
""")
    Long contarActivos();

    @Query("""
    SELECT COUNT(a)
    FROM AlquilerEntity a
    WHERE a.fechaFin = :fecha
""")
    Long contarFinalizanPronto(LocalDate fecha);

    @Query("""
    SELECT COUNT(a)
    FROM AlquilerEntity a
    WHERE a.fechaInicio BETWEEN :inicioMes AND :finMes
""")
    Long contarAlquileresMes( @Param("inicioMes") LocalDate inicioMes,
                              @Param("finMes") LocalDate finMes);

    @Query("""
    SELECT COALESCE(SUM(p.montoFinal),0)
    FROM PagoEntity p
    WHERE p.alquiler.fechaInicio BETWEEN :inicio AND :fin
""")
    BigDecimal ingresosMes(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("""
    SELECT COALESCE(SUM(p.monto),0)
    FROM PenalizacionEntity p
    WHERE p.fechaCreacion BETWEEN :inicio AND :fin
""")
    BigDecimal totalPenalizaciones(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
SELECT COALESCE(AVG(p.montoFinal),0)
FROM PagoEntity p
WHERE p.fechaCreacion BETWEEN :inicio AND :fin
""")
    BigDecimal promedioIngresoPorAlquiler(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    @Query("""
SELECT 
    v.titulo AS titulo,
    SUM(d.cantidad) AS cantidad
FROM DetalleAlquilerEntity d
JOIN d.inventarioItem i
JOIN i.videojuego v
GROUP BY v.titulo
ORDER BY SUM(d.cantidad) DESC
""")
    List<TopJuegosResponse> obtenerTopJuegos(Pageable pageable);

    @Query("""
SELECT 
    v.genero AS nombre,
    SUM(d.cantidad) AS cantidad

FROM DetalleAlquilerEntity d
JOIN d.inventarioItem i
JOIN i.videojuego v
GROUP BY v.genero
ORDER BY SUM(d.cantidad) DESC
""")
    List<TopGenerosResponse> obtenerTopGeneros(Pageable pageable);

    @Query("""
SELECT 
    i.plataforma AS nombre,
    SUM(d.cantidad) AS cantidad
FROM DetalleAlquilerEntity d
JOIN d.inventarioItem i
GROUP BY i.plataforma
ORDER BY SUM(d.cantidad) DESC
""")
    List<TopPlataformasResponse> obtenerTopPlataformas(Pageable pageable);

    @Query("""
SELECT 
    p.dni AS dni,
    p.nombre AS nombre,
    p.apellido AS apellido,
    COUNT(a.alquilerId) AS total

FROM AlquilerEntity a
JOIN a.persona p
GROUP BY p.id, p.dni, p.nombre, p.apellido
ORDER BY COUNT(a.alquilerId) DESC
""")
    List<TopClientesResponse> obtenerTopClientes(Pageable pageable);

    @Query("""
    SELECT COALESCE(SUM(d.cantidad), 0)
    FROM DetalleAlquilerEntity d
    JOIN d.alquiler a
    WHERE a.estadoAlquiler = 'EN_CURSO'
""")
    Long contarVideojuegosAlquilados();



}
