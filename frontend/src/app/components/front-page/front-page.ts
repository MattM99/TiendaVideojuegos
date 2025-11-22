import { Component, computed } from '@angular/core';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-front-page',
  standalone: true,
  imports: [],
  templateUrl: './front-page.html',
  styleUrl: './front-page.css',
})
export class FrontPage {
  usuario = computed(() => this.auth.currentUser());

  constructor(private auth: AuthService) {}

}
