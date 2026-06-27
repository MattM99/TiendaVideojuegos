import { Component, HostListener, OnInit, signal } from '@angular/core';
import { AuthService } from '../../auth/auth-service/auth';
import { RouterModule } from '@angular/router';
import { CuentaModel } from '../../cuenta/cuenta.model';
import { SecretoService } from '../secreto/secreto-service';

@Component({
  selector: 'app-front-page',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './front-page.html',
  styleUrl: './front-page.css',
})
export class FrontPage implements OnInit {
  usuario = signal<CuentaModel | null>(null);

  constructor(private auth: AuthService, private secretoService: SecretoService) { }

  ngOnInit(): void {
    this.auth.getCurrentUser().subscribe({
      next: (response: CuentaModel) => {
        this.usuario.set(response);
      }
    });
  }

  botonSecreto = false;
  private sonidoSecreto = new Audio('assets/audio/secret-found.mp3');

  private combinacion = [
    'ArrowUp',
    'ArrowUp',
    'ArrowDown',
    'ArrowDown',
    'ArrowLeft',
    'ArrowRight',
    'ArrowLeft',
    'ArrowRight',
    'b',
    'a'
  ];

  private input: string[] = [];

  @HostListener('window:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent) {
    if (this.botonSecreto) {
      return;
    }


    const key = event.key.length === 1 ? event.key.toLowerCase() : event.key;
    this.input.push(key);
    console.log('Input:', this.input);

    if (this.input.length > this.combinacion.length) {
      this.input.shift();
    }

    if (this.input.length === this.combinacion.length) {

      const acierto = this.input.join('|') === this.combinacion.join('|');

      if (acierto) {
        this.botonSecreto = true;
        this.secretoService.desbloquear();
        this.input = [];


        this.sonidoSecreto.currentTime = 0;
        this.sonidoSecreto.play().catch(err => {
          console.error('No se pudo reproducir el sonido', err);
        });

        console.log('¡Combinación correcta! Botón secreto desbloqueado.');
      }
    }
  }

}
