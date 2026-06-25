import { Component, OnInit, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

import { BloqueoModel } from "../bloqueo.model";
import { BloqueoService } from "../bloqueo.service";

@Component({
  selector: 'app-bloqueo-historico',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bloqueo-historico.html',
  styleUrl: '../bloqueo-list/bloqueo-list.css',
})
export class BloqueoHistoricoComponent implements OnInit {

  private service = inject(BloqueoService);

  bloqueados = signal<BloqueoModel[]>([]);

  titulo = "Histórico de bloqueos";
  mostrarDesbanear = false;

  ngOnInit() {
    this.service.obtenerHistorico().subscribe({
      next: lista => this.bloqueados.set(lista)
    });
  }
}