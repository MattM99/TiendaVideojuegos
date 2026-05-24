import { Component, OnInit, signal } from '@angular/core';
import { AuthService } from '../../auth/auth-service/auth';
import { RouterModule } from '@angular/router';
import { CuentaModel } from '../../cuenta/cuenta.model';

@Component({
  selector: 'app-front-page',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './front-page.html',
  styleUrl: './front-page.css',
})
export class FrontPage implements OnInit {
  usuario = signal<CuentaModel | null>(null);

  constructor(private auth: AuthService) {}

  ngOnInit(): void {
    this.auth.getCurrentUser().subscribe({
      next: (response: CuentaModel) => {
        this.usuario.set(response);
      }
    });
  }
}
