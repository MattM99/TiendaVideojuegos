import { Routes } from '@angular/router';
import { InventarioItemListComponent } from './inventario-item-list.component/inventario-item-list.component';
import { InventarioItemDetailComponent } from './inventario-item-detail.component/inventario-item-detail.component';
import { InventarioItemFormComponent } from './inventario-item-form.component/inventario-item-form.component';

export const INVENTARIO_ITEM_ROUTES: Routes = [
  { path: '', component: InventarioItemListComponent },          // /inventarioItems
  { path: 'new', component: InventarioItemFormComponent },       // /inventarioItems/new
  { path: ':id', component: InventarioItemDetailComponent },      // /inventarioItems/123
  { path: 'edit/:id', component: InventarioItemFormComponent }    // /inventarioItems/edit/123
];

