import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventarioItemFormComponent } from './inventario-item-form.component';

describe('InventarioItemFormComponent', () => {
  let component: InventarioItemFormComponent;
  let fixture: ComponentFixture<InventarioItemFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventarioItemFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InventarioItemFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
