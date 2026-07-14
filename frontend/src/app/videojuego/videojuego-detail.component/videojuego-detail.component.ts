import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideojuegoService } from '../videojuego.service';
import { ErrorService } from '../../shared/error/error';

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
  private errorService = inject(ErrorService);

  videojuego = signal<any>(null);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id'); 
    if (!id) return;

    this.service.getById(id).subscribe({
      next: (data) => this.videojuego.set(data),
      error: err => this.errorService.mostrar(err)
    });
  }

  volver() {
    this.router.navigate(['/videojuegos']);
  }

  editar() {
    const id = this.videojuego()?.videojuegoId;
    this.router.navigate(['/videojuegos/edit', id]);
  }
}
