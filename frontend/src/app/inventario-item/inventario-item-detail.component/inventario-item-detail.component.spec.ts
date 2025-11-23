import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventarioItemDetailComponent } from './inventario-item-detail.component';

describe('InventarioItemDetailComponent', () => {
  let component: InventarioItemDetailComponent;
  let fixture: ComponentFixture<InventarioItemDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventarioItemDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InventarioItemDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
