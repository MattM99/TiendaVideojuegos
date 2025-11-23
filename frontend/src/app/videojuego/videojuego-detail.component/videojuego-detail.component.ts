import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideojuegoService } from '../videojuego.service';

@Component({
  selector: 'app-videojuego-detail',
  standalone: true,
  templateUrl: './videojuego-detail.component.html',
  styleUrls: ['./videojuego-detail.component.css']
})
export class VideojuegoDetailComponent {

  service = inject(VideojuegoService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  videojuego = signal<any>(null);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id'); // ya es string
    if (!id) return;

    this.service.getById(id).subscribe({
      next: (data) => this.videojuego.set(data),
      error: () => alert("No se pudo cargar el videojuego")
    });
  }

  volver() {
    this.router.navigate(['/videojuegos']);
  }

  editar() {
    const id = this.videojuego()?.id;
    this.router.navigate(['/videojuegos/edit', id]);
  }
}
