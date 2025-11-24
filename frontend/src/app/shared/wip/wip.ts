import { Component } from '@angular/core';

@Component({
  selector: 'app-wip',
  imports: [],
  templateUrl: './wip.html',
  styleUrl: './wip.css',
})
export class Wip {
  videoVisible = false;

  playVideo() {
    this.videoVisible = true;
  }
}
