import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideojuegoService } from '../videojuego.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-videojuego-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './videojuego-form.component.html',
  styleUrls: ['./videojuego-form.component.css']
})
export class VideojuegoFormComponent {
  service = inject(VideojuegoService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  isEdit = false;
  id = 0;

  videojuego = signal({
    titulo: '',
    sinopsis: '',
    genero: '',
    multijugador: false,
    lanzamiento: 2000,
    desarrollador: ''
  });

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEdit = !!this.id;
    if (this.isEdit) {
      this.service.getById(this.id).subscribe(v => this.videojuego.set(v));
    }
  }

  updateTitulo(value: string) {
    this.videojuego.update(v => ({ ...v, titulo: value }));
  }

  updateSinopsis(value: string) {
    this.videojuego.update(v => ({ ...v, sinopsis: value }));
  }

  updateGenero(value: string) {
    this.videojuego.update(v => ({ ...v, genero: value }));
  }

  updateMultijugador(value: boolean) {
    this.videojuego.update(v => ({ ...v, multijugador: value }));
  }

  updateLanzamiento(value: number) {
    this.videojuego.update(v => ({ ...v, lanzamiento: value }));
  }

  save() {
    const data = this.videojuego();
    if (this.isEdit) {
      this.service.update(this.id, data).subscribe(() => this.router.navigate(['/videojuegos']));
    } else {
      this.service.create(data).subscribe(() => this.router.navigate(['/videojuegos']));
    }
  }

}

