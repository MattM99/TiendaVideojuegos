import { Component, inject, signal, computed } from '@angular/core';
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
  id: string = '';

  videojuego = signal({
    titulo: '',
    sinopsis: '',
    genero: '',
    multijugador: false,
    lanzamiento: 2000,
    desarrollador: ''
  });

  // Computed signal para habilitar/deshabilitar "Guardar"
  canSave = computed(() => {
    const v = this.videojuego();
    return (
      v.titulo.trim() !== '' &&
      v.sinopsis.trim() !== '' &&
      v.genero.trim() !== '' &&
      v.desarrollador.trim() !== '' &&
      v.lanzamiento != null
    );
  });

  ngOnInit() {
    const routeId = this.route.snapshot.paramMap.get('id');
    if (routeId) {
      this.id = routeId;
      this.isEdit = true;

      this.service.getById(this.id).subscribe(v => this.videojuego.set(v));
    }
  }

  updateTitulo(value: string) { this.videojuego.update(v => ({ ...v, titulo: value })); }
  updateSinopsis(value: string) { this.videojuego.update(v => ({ ...v, sinopsis: value })); }
  updateGenero(value: string) { this.videojuego.update(v => ({ ...v, genero: value })); }
  updateDesarrollador(value: string) { this.videojuego.update(v => ({ ...v, desarrollador: value })); }
  updateMultijugador(value: boolean) { this.videojuego.update(v => ({ ...v, multijugador: value })); }
  updateLanzamiento(value: number) { this.videojuego.update(v => ({ ...v, lanzamiento: value })); }

  save() {
    if (!this.canSave()) return; // nunca guardamos si no está completo
    const data = this.videojuego();

    if (this.isEdit) {
      this.service.update(this.id, data).subscribe(() => this.router.navigate(['/videojuegos']));
    } else {
      this.service.create(data).subscribe(() => this.router.navigate(['/videojuegos']));
    }
  }

  volver() { history.back(); }
}