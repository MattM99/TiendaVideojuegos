import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './not-found.html',
  styleUrl: './not-found.css',
})
export class NotFoundComponent implements OnInit {

  ngOnInit(): void {
    const audio = new Audio('assets/audio/death.mp3');
    audio.volume = 0.4;

    audio.play().catch(err => {
      console.log('No se pudo reproducir el audio:', err);
    });
  }

 }
