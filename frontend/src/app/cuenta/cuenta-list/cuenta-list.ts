import { CuentaModel } from '../cuenta.model';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/auth-service/auth';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CuentaService } from '../cuenta.service';

@Component({
  selector: 'app-cuenta-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './cuenta-list.html',
  styleUrls: ['./cuenta-list.css']
})
export class CuentaListComponent implements OnInit {
usuario = computed(() => this.auth.currentUser());

  private service = inject(CuentaService);
  cuentas = signal<CuentaModel[]>([]);

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCuentas();
  }

  loadCuentas() {
    this.service.getAll().subscribe(data => this.cuentas.set(data));
  }

  deleteCuenta(cuenta: CuentaModel) {
    if (!confirm(`¿Eliminar cuenta de ${cuenta.nombreUsuario}?`)) return;

    this.http.delete(`http://localhost:3000/cuentas/${cuenta.id}`).subscribe({
      next: () => this.loadCuentas(),
      error: err => console.error(err)
    });
  }

  editCuenta(cuenta: CuentaModel) {
    this.router.navigate(['/cuentas/editar', cuenta.id]);
  }

  crearEmpleado() {
    this.router.navigate(['/cuentas/nuevo']);
  }

}
