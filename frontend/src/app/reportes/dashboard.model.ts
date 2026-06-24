import { TopClientesResponse } from "./top-cliente.model";
import { TopGenerosResponse } from "./top-genero.model";
import { TopJuegoResponse } from "./top-juego.model";
import { TopPlataformasResponse } from "./top-plataformas.model";

export interface DashboardResponse {
  totalAlquileres: number;
  alquileresActivos: number;
  alquileresFinalizanManiana: number;
  alquileresMesActual: number;

  ingresosMes: number;
  ingresosPenalizacionesMes: number;
  promedioPorAlquiler: number;

  videojuegosAlquilados: number;

  juegosMasAlquilados: TopJuegoResponse[];
  generosMasAlquilados: TopGenerosResponse[];
  plataformasMasAlquiladas: TopPlataformasResponse[];

  clientesFrecuentes: TopClientesResponse[];
}
