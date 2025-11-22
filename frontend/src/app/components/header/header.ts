import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  usuario: Cuenta | null = null;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.usuario = this.auth.getCurrentUser();
  }

  logout(event: Event) {
    event.preventDefault();
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}
