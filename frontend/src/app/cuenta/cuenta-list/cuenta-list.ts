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
  styleUrls: ['./cuenta-list.css'],
})
export class CuentaListComponent implements OnInit {
  usuario = computed(() => this.auth.currentUser());
  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);
  sortBy = signal('nickname');
  direction = signal('desc');

  mostrarBajas = signal(false);

  cuentasFiltradas = computed(() => {

  if (this.mostrarBajas()) {
    return this.cuentas();
  }

  return this.cuentas().filter(
    cuenta => cuenta.estado !== 'BAJA'
  );

});

  private service = inject(CuentaService);
  cuentas = signal<CuentaModel[]>([]);

  constructor(private http: HttpClient, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.loadCuentas();
  }

  loadCuentas() {
    this.service.getAll(
      this.page(),
      this.size(),
      this.sortBy(),
      this.direction()
    ).subscribe((response) => {
      this.cuentas.set(response.content);
      this.totalPages.set(response.totalPages);
      this.totalElements.set(response.totalElements);
    });
  }

  crearEmpleado() {
    this.router.navigate(['/personas/nueva'], { queryParams: { crearCuenta: true } });
  }


///PAGINACION, ORDENAMIENTO Y FILTRADO
  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadCuentas();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadCuentas();
    }
  }

  changeSize(event: Event) {

    const value = Number(
      (event.target as HTMLSelectElement).value
    );

    this.size.set(value);

    this.page.set(0);

    this.loadCuentas();
  }

  changeSort(event: Event) {

    const value = (
      event.target as HTMLSelectElement
    ).value;

    this.sortBy.set(value);

    this.page.set(0);

    this.loadCuentas();
  }

  changeDirection(event: Event) {

    const value = (
      event.target as HTMLSelectElement
    ).value;

    this.direction.set(value);

    this.page.set(0);

    this.loadCuentas();
  }
}
