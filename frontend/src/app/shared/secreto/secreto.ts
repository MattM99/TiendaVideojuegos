import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
interface Obstacle {
  x: number;
  y: number;
  width: number;
  height: number;
}

@Component({
  selector: 'app-secreto',
  imports: [],
  templateUrl: './secreto.html',
  styleUrl: './secreto.css',
})

export class Secreto implements AfterViewInit {

  @ViewChild('gameCanvas', { static: true })
  canvasRef!: ElementRef<HTMLCanvasElement>;

  private ctx!: CanvasRenderingContext2D;
  private animationId!: number;
  private runImg1 = new Image();
  private runImg2 = new Image();
  private jumpImg = new Image();

  private canvasWidth = 0;
  private canvasHeight = 0;
  private groundY = 0;
  private groundOffset = 60;

  private gameSpeed = 6;
  private speedIncreaseTimer = 0;
  private obstacleSpawnRate = 90;

  private frame = 0;
  private frameCounter = 0;

  private score = 0;


  //TAMANO PANTALLA
  private resizeCanvas(): void {
    const canvas = this.canvasRef.nativeElement;

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight * 0.4;

    this.ctx = canvas.getContext('2d')!;

    this.canvasWidth = canvas.width;
    this.canvasHeight = canvas.height;

    this.groundY = this.canvasHeight - this.groundOffset;

    this.personaje.y = this.groundY - this.personaje.height;
  }

  // Personaje
  private personaje = {
    x: 50,
    y: 0,
    width: 40,
    height: 40,
    velocityY: 0,
    jumping: false
  };

  // OBSTÁCULOS
  private obstacles: Obstacle[] = [];
  private obstacleTimer = 0;

  // ESTADO
  private gameOver = false;

  ngAfterViewInit(): void {
    this.resizeCanvas();
    window.addEventListener('resize', () => {
      this.resizeCanvas();
    });

    this.runImg1.src = 'assets/secreto/sonic-run-1.png';
    this.runImg2.src = 'assets/secreto/sonic-run-2.png';
    this.jumpImg.src = 'assets/secreto/sonic-jump.png';

    window.addEventListener('keydown', this.handleKeyDown);

    this.loop();
  }

  ngOnDestroy(): void {
    window.removeEventListener('keydown', this.handleKeyDown);
    cancelAnimationFrame(this.animationId);
  }

  // ------------------------
  // INPUT
  // ------------------------
  private handleKeyDown = (e: KeyboardEvent) => {
    if (e.code === 'Space') {
      this.jump();
    }

    if (e.code === 'Enter' && this.gameOver) {
      this.restart();
    }
  };

  private jump(): void {
    if (this.personaje.jumping || this.gameOver) return;

    this.personaje.velocityY = -12;
    this.personaje.jumping = true;
  }

  // ------------------------
  // LOOP PRINCIPAL
  // ------------------------
  private loop = () => {
    this.animationId = requestAnimationFrame(this.loop);

    this.update();
    this.updateAnimation();
    this.updateObstacles();
    this.updateSpeed();
    this.moveObstacles();
    this.checkCollision();
    this.draw();
  };

  // ------------------------
  // UPDATE PERSONAJE
  // ------------------------
  private update(): void {
    if (this.gameOver) return;

    this.personaje.y += this.personaje.velocityY;
    this.personaje.velocityY += 0.6; // gravedad

    if (this.personaje.y >= this.groundY - this.personaje.height) {
      this.personaje.y = this.groundY - this.personaje.height;
      this.personaje.velocityY = 0;
      this.personaje.jumping = false;
    }
    this.score++;
  }

  private updateAnimation(): void {
    if (this.gameOver) return;

    this.frameCounter++;

    // cambia frame cada 8 ticks
    if (this.frameCounter > 8) {
      this.frame = this.frame === 0 ? 1 : 0;
      this.frameCounter = 0;
    }
  }

  // ------------------------
  // OBSTÁCULOS
  // ------------------------
  private updateObstacles(): void {
    if (this.gameOver) return;

    this.obstacleTimer++;
    const height = 20 + Math.random() * 60; // entre 20 y 80


    if (this.obstacleTimer > this.obstacleSpawnRate) {
      this.obstacles.push({
        x: this.canvasWidth,
        y: this.groundY - height,
        width: 20,
        height: height
      });

      this.obstacleTimer = 0;
    }
  }

  private moveObstacles(): void {
    if (this.gameOver) return;

    for (let obs of this.obstacles) {
      obs.x -= this.gameSpeed;
    }

    this.obstacles = this.obstacles.filter(o => o.x + o.width > 0);
  }

  private updateSpeed(): void {
    if (this.gameOver) return;

    this.speedIncreaseTimer++;

    if (this.speedIncreaseTimer > 300) {
      this.gameSpeed += 0.5;

      if (this.obstacleSpawnRate > 40) {
        this.obstacleSpawnRate -= 2;
      }

      this.speedIncreaseTimer = 0;
    }
  }

  // ------------------------
  // COLISIÓN
  // ------------------------
  private checkCollision(): void {
    for (let obs of this.obstacles) {
      if (
        this.personaje.x < obs.x + obs.width &&
        this.personaje.x + this.personaje.width > obs.x &&
        this.personaje.y < obs.y + obs.height &&
        this.personaje.y + this.personaje.height > obs.y
      ) {
        this.gameOver = true;
      }
    }
  }

  // ------------------------
  // DIBUJO
  // ------------------------

  private drawCharacter(): void {
    let img: HTMLImageElement;

    if (this.personaje.jumping) {
      img = this.jumpImg;
    } else {
      img = this.frame === 0 ? this.runImg1 : this.runImg2;
    }

    this.ctx.drawImage(
      img,
      this.personaje.x,
      this.personaje.y,
      this.personaje.width,
      this.personaje.height
    );
  }

  private draw(): void {

    this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
    this.ctx.textAlign = 'left';
    this.ctx.fillStyle = 'black';
    this.ctx.font = 'bold 20px Arial';
    this.ctx.fillText(`Score: ${Math.floor(this.score / 10)}`, 20, 30);

    // suelo
    this.ctx.fillStyle = '#333';
    this.ctx.fillRect(0, this.groundY, this.canvasWidth, 2);
    this.drawCharacter();

    // obstáculos
    this.ctx.fillStyle = 'black';
    for (let obs of this.obstacles) {
      this.ctx.fillRect(obs.x, obs.y, obs.width, obs.height);
    }

    // GAME OVER
    if (this.gameOver) {
      this.ctx.textAlign = 'center';


      this.ctx.fillStyle = 'red';
      this.ctx.font = 'bold 48px Arial';
      this.ctx.fillText('GAME OVER', this.canvasWidth / 2, this.canvasHeight / 2 - 20);


      this.ctx.fillStyle = 'black';
      this.ctx.font = 'bold 20px Arial';
      this.ctx.fillText(
        'Presiona ENTER para reiniciar',
        this.canvasWidth / 2,
        this.canvasHeight / 2 + 20
      );
    }
  }

  // ------------------------
  // REINICIO
  // ------------------------
  private restart(): void {
    this.gameOver = false;

    this.score = 0;

    this.obstacles = [];
    this.obstacleTimer = 0;

    this.gameSpeed = 6;
    this.speedIncreaseTimer = 0;
    this.obstacleSpawnRate = 90;


    this.frame = 0;
    this.frameCounter = 0;

    this.personaje.y = this.groundY - this.personaje.height;
    this.personaje.velocityY = 0;
    this.personaje.jumping = false;
  }
}
