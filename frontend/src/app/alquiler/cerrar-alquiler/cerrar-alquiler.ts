import { Component, Input, OnInit } from '@angular/core';
import { MetodoPago } from '../../models/metodo-pago.model';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Alquiler } from '../alquiler';
import { CerrarAlquilerRequest } from '../../models/cerrar-alquiler-request.model';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-cerrar-alquiler',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './cerrar-alquiler.html',
  styleUrl: './cerrar-alquiler.css',
})
export class CerrarAlquiler implements OnInit {

  alquilerId!: number;

  MetodoPago = MetodoPago;
  metodosPago = Object.values(MetodoPago);
  form!: FormGroup;


  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private alquilerService: Alquiler,
  ) { }


  ngOnInit(): void {

    this.alquilerId = Number(this.route.snapshot.params['id']);

    if (isNaN(this.alquilerId)) {
    throw new Error('ID de alquiler inválido');
  }

    this.form = this.fb.group({
      metodoPago: [
        null,
        Validators.required
      ],
      descuento: [
        0,
        [Validators.min(0), Validators.max(100)]
      ],
      penalizaciones: this.fb.array([])
    });

    this.alquilerService.obtenerAlquiler(this.alquilerId.toString())
  .subscribe({

    next: alquiler => {

      if (alquiler.estadoAlquiler === 'FINALIZADO') {

        alert('Este alquiler ya fue finalizado.');

        this.router.navigate(['/alquileres']);

        return;
      }

    },

    error: err => {

        console.error(err);

        alert('No se pudo obtener la información del alquiler.');

        this.router.navigate(['/alquileres']);

      }

  });
  }

  get penalizaciones(): FormArray {
    return this.form.get('penalizaciones') as FormArray;
  }

  agregarPenalizacion(): void {

    this.penalizaciones.push(
      this.fb.group({
        motivo: ['', Validators.required],
        monto: [0, [Validators.required, Validators.min(0), Validators.max(999999)]]
      })
    );
  }

  eliminarPenalizacion(index: number): void {
    this.penalizaciones.removeAt(index);
  }

  cerrarAlquiler(): void {

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.value;
    const request: CerrarAlquilerRequest = { ...this.form.value, descuento: formValue.descuento / 100 };

    this.alquilerService
      .cerrarAlquiler(this.alquilerId, request)
      .subscribe({

        next: response => {
          alert('Alquiler finalizado correctamente');
        },

        error: err => {

          console.error(err);

          alert('Ocurrió un error al cerrar el alquiler');
        }
      });
  }
}
