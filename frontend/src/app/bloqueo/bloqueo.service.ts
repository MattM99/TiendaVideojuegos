import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { BloqueoCreateRequest } from "./bloqueo-create-request";
import { BloqueoModel } from "./bloqueo.model";

@Injectable({
  providedIn: 'root'
})
export class BloqueoService {

    private http = inject(HttpClient);

    private apiUrl = 'http://localhost:8080/api/blacklist';

    obtenerVigentes() {
        return this.http.get<BloqueoModel[]>(`${this.apiUrl}/vigentes`);
    }

    obtenerHistorico() {
        return this.http.get<BloqueoModel[]>(`${this.apiUrl}/historico`);
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

}