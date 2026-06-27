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

    // ajustar dinosaurio al suelo
    this.dinosaur.y = this.groundY - this.dinosaur.height;
  }

  // DINOSAURIO
  private dinosaur = {
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

    /*const canvas = this.canvasRef.nativeElement;
    this.ctx = canvas.getContext('2d')!;*/

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
    if (this.dinosaur.jumping || this.gameOver) return;

    this.dinosaur.velocityY = -12;
    this.dinosaur.jumping = true;
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
  // UPDATE DINOSAURIO
  // ------------------------
  private update(): void {
    if (this.gameOver) return;

    this.dinosaur.y += this.dinosaur.velocityY;
    this.dinosaur.velocityY += 0.6; // gravedad

    if (this.dinosaur.y >= this.groundY - this.dinosaur.height) {
      this.dinosaur.y = this.groundY - this.dinosaur.height;
      this.dinosaur.velocityY = 0;
      this.dinosaur.jumping = false;
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
        this.dinosaur.x < obs.x + obs.width &&
        this.dinosaur.x + this.dinosaur.width > obs.x &&
        this.dinosaur.y < obs.y + obs.height &&
        this.dinosaur.y + this.dinosaur.height > obs.y
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

    if (this.dinosaur.jumping) {
      img = this.jumpImg;
    } else {
      img = this.frame === 0 ? this.runImg1 : this.runImg2;
    }

    this.ctx.drawImage(
      img,
      this.dinosaur.x,
      this.dinosaur.y,
      this.dinosaur.width,
      this.dinosaur.height
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
    // dinosaurio animado
    this.drawCharacter();

    // dinosaurio
    /* this.ctx.fillStyle = 'green';
     this.ctx.fillRect(
       this.dinosaur.x,
       this.dinosaur.y,
       this.dinosaur.width,
       this.dinosaur.height
     );*/

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
    this.obstacles = [];
    this.obstacleTimer = 0;

    this.dinosaur.y = this.groundY - this.dinosaur.height;
    this.dinosaur.velocityY = 0;
    this.dinosaur.jumping = false;
  }
}
