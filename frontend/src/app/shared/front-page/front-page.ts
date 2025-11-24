import { Component, computed } from '@angular/core';
import { AuthService } from '../../auth/auth-service/auth';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-front-page',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './front-page.html',
  styleUrl: './front-page.css',
})
export class FrontPage {
  usuario = computed(() => this.auth.currentUser());

  constructor(private auth: AuthService) {}
}
