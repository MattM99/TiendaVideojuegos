import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { BloqueoCreateRequest } from "./bloqueo-create-request";
import { BloqueoModel } from "./bloqueo.model";
import { PageResponse } from '../models/page-response.model';
import { PersonaModel } from "../persona/persona.model";

@Injectable({
  providedIn: 'root'
})
export class BloqueoService {

    private http = inject(HttpClient);

    private apiUrl = 'http://localhost:8080/api/blacklist';

    obtenerVigentes(
        pagina: number,
        tamano: number,
        ordenarPor: string,
        direccion: string
    ) {
        return this.http.get<PageResponse<BloqueoModel>>(
            `${this.apiUrl}/vigentes?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
        );
    }

    obtenerHistorico(
        pagina: number,
        tamano: number,
        ordenarPor: string,
        direccion: string
    ) {
        return this.http.get<PageResponse<BloqueoModel>>(
            `${this.apiUrl}/historico?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
        );
    }

    crear(request: BloqueoCreateRequest) {
        return this.http.post<BloqueoModel>(`${this.apiUrl}/crear`, request);
    }

    desbanear(dni: string) {
        return this.http.put<BloqueoModel>(
            `${this.apiUrl}/desbanear/${dni}`,
            {}
        );
    }

    validarPersona(dni: string) {
        return this.http.get<PersonaModel>(
            `${this.apiUrl}/validar/${dni}`
        );
    }

}