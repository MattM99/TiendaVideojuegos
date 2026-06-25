import { CommonModule } from "@angular/common";
import { Component, OnInit, inject, signal } from "@angular/core";
import { RouterModule } from "@angular/router";
import { BloqueoModel } from "../bloqueo.model";
import { BloqueoService } from "../bloqueo.service";
import { ErrorService } from "../../shared/error/error";

@Component({
  selector: 'app-bloqueo-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bloqueo-list.html',
  styleUrl: './bloqueo-list.css'
})
export class BloqueoListComponent implements OnInit{

    private service = inject(BloqueoService);

    private errorService = inject(ErrorService);

    bloqueados = signal<BloqueoModel[]>([]);

    ngOnInit(){

        this.cargarBloqueados();

    }

    cargarBloqueados(){
        this.service.obtenerVigentes().subscribe({
            next:(lista)=>{
                this.bloqueados.set(lista);
            },
            error: (err) => this.errorService.mostrar(err)
        });
    }

    desbanear(dni: string) {
        if (!confirm("¿Desbanear a esta persona?"))
            return;

        this.service.desbanear(dni).subscribe({
            next: () => this.cargarBloqueados(),
            error: (err) => this.errorService.mostrar(err)
        });
    }

}
