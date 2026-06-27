import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class SecretoService {
  desbloqueado = signal(false);

  desbloquear() {
    this.desbloqueado.set(true);
  }

  bloquear() {
    this.desbloqueado.set(false);
  }

}
