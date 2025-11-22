import { Component, inject, signal } from '@angular/core';
import { VideojuegoService } from '../videojuego.service';
import { VideojuegoModel } from '../videojuego.model';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-videojuego-list.component',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './videojuego-list.component.html',
  styleUrl: './videojuego-list.component.css',
})
export class VideojuegoListComponent {
  
  // Inyección moderna sin constructor
  private service = inject(VideojuegoService);

  // Signal para manejar reactividad sin Zone.js
  videojuegos = signal<VideojuegoModel[]>([]);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.service.getAll().subscribe(data => this.videojuegos.set(data));
  }

  delete(id?: number) {
    if (!id) return; // si undefined → no hacemos nada

    if (!confirm("¿Seguro que querés eliminar este videojuego?")) return;

    this.service.delete(id).subscribe({
      next: () => this.loadData(),
      error: () => alert("Error al eliminar el videojuego")
    });
  }

  
}
