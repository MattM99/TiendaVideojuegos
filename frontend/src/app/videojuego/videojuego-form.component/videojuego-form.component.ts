import { Component, inject, signal, computed } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VideojuegoService } from '../videojuego.service';
import { FormsModule } from '@angular/forms';
import { VideojuegoModel } from '../videojuego.model';

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

  videojuego = signal<VideojuegoModel>({
  titulo: '',
  descripcion: '',
  genero: '',
  multijugador: false,
  lanzamiento: 0,
  desarrollador: ''
});

  tituloTouched = signal(false);
generoTouched = signal(false);
desarrolladorTouched = signal(false);
lanzamientoTouched = signal(false);
descripcionTouched = signal(false);


  // ---- VALIDACIONES POR CAMPO ----
  tituloError = computed(() => {
      if (!this.tituloTouched()) return null;   // <--- AGREGAR ESTO

    const v = this.videojuego().titulo.trim();
    if (v === '') return 'El título es obligatorio';
    if (v.length < 3) return 'El título debe tener al menos 3 caracteres';
    if (v.length > 40) return 'Máximo 40 caracteres';
    return null;
  });

  descripcionError = computed(() => {
      if (!this.descripcionTouched()) return null;

    const v = this.videojuego().descripcion.trim();
    if (v === '') return 'La descripción es obligatoria';
    if (v.length < 10) return 'Debe tener mínimo 10 caracteres';
    return null;
  });

  generoError = computed(() => {
      if (!this.generoTouched()) return null;

    const v = this.videojuego().genero.trim();
    if (v === '') return 'Debe ingresar un género';
    return null;
  });

  desarrolladorError = computed(() => {
      if (!this.desarrolladorTouched()) return null;
    const v = this.videojuego().desarrollador.trim();
    if (v === '') return 'El desarrollador es obligatorio';
    return null;
  });

  lanzamientoError = computed(() => {
      if (!this.lanzamientoTouched()) return null;

    const year = this.videojuego().lanzamiento;
    if (!year) return 'Debe ingresar un año';
    if (year < 1960) return 'Hay un debate sobre cual es el primer videojuego de la historia, si fue Tennis for Two(1958), OXO(1952) o Spacewar! (1962), entre otros. Pero dudo mucho que estés intentando registrar un videojuego tan antiguo.';
    if (year > new Date().getFullYear() + 1) return 'Año inválido';
    return null;
  });

  // Computed signal para habilitar/deshabilitar "Guardar"
  canSave = computed(() => {
  const v = this.videojuego();
  const actual = new Date().getFullYear();

  return (
    v.titulo.trim().length >= 3 &&
    v.descripcion.trim().length >= 10 &&
    v.genero.trim() !== '' &&
    v.desarrollador.trim() !== '' &&
    v.lanzamiento >= 1960 &&
    v.lanzamiento <= actual + 1
  );
});


  ngOnInit() {
    const routeId = this.route.snapshot.paramMap.get('id');
    if (routeId) {
      this.id = routeId;
      this.isEdit = true;

      this.service.getById(this.id).subscribe(v => {

      this.videojuego.set({
        ...v,
        multijugador:
          v.multijugador === true ||
          v.multijugador === 'Sí'
        });

      });
    }
  }

  updateTitulo(value: string) { this.videojuego.update(v => ({ ...v, titulo: value })); }
  updateDescripcion(value: string) { this.videojuego.update(v => ({ ...v, descripcion: value })); }
  updateGenero(value: string) { this.videojuego.update(v => ({ ...v, genero: value })); }
  updateDesarrollador(value: string) { this.videojuego.update(v => ({ ...v, desarrollador: value })); }
  updateMultijugador(value: boolean) { this.videojuego.update(v => ({ ...v, multijugador: value })); }
  updateLanzamiento(value: number) { this.videojuego.update(v => ({ ...v, lanzamiento: value })); }

  save() {
  console.log('CLICK GUARDAR');
  console.log('canSave:', this.canSave());
  console.log('data:', this.videojuego());

  if (!this.canSave()) {
    alert('Faltan datos o hay campos inválidos');
    return;
  }

  const data = this.videojuego();

  if (this.isEdit) {
    this.service.update(this.id, data).subscribe({
      next: () => this.router.navigate(['/videojuegos']),
      error: (err) => {
        console.error(err);
        alert('Error al actualizar videojuego');
      }
    });
  } else {
    this.service.create(data).subscribe({
      next: () => this.router.navigate(['/videojuegos']),
      error: (err) => {
        console.error(err);
        alert('Error al crear videojuego');
      }
    });
  }
}

  volver() { history.back(); }

  markTouched(field: string) {
    if (field === 'titulo') this.tituloTouched.set(true);
    if (field === 'genero') this.generoTouched.set(true);
    if (field === 'desarrollador') this.desarrolladorTouched.set(true);
    if (field === 'lanzamiento') this.lanzamientoTouched.set(true);
    if (field === 'descripcion') this.descripcionTouched.set(true);
  }
}
