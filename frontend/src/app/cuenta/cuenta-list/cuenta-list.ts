import { CuentaModel } from '../cuenta.model';
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/auth-service/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cuenta-list',
  standalone: true,
  templateUrl: './cuenta-list.html',
  styleUrls: ['./cuenta-list.css']
})
export class CuentaListComponent implements OnInit {
  cuentas: CuentaModel[] = [];
  loading: boolean = true;

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCuentas();
  }

  loadCuentas() {
    this.loading = true;
    this.http.get<CuentaModel[]>('http://localhost:3000/cuentas').subscribe({
      next: data => {
        this.cuentas = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  deleteCuenta(cuenta: CuentaModel) {
    if (!confirm(`¿Eliminar cuenta de ${cuenta.nombreUsuario}?`)) return;

    this.http.delete(`http://localhost:3000/cuentas/${cuenta.id}`).subscribe({
      next: () => this.loadCuentas(),
      error: err => console.error(err)
    });
  }

  editCuenta(cuenta: CuentaModel) {
    // Redirige a un formulario de edición, que podés crear si querés
    this.router.navigate(['/cuentas', cuenta.id]);
  }
}
