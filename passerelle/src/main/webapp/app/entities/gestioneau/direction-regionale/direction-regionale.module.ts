import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { DirectionRegionaleComponent } from './list/direction-regionale.component';
import { DirectionRegionaleDetailComponent } from './detail/direction-regionale-detail.component';
import { DirectionRegionaleUpdateComponent } from './update/direction-regionale-update.component';
import { DirectionRegionaleDeleteDialogComponent } from './delete/direction-regionale-delete-dialog.component';
import { DirectionRegionaleRoutingModule } from './route/direction-regionale-routing.module';

@NgModule({
  imports: [SharedModule, DirectionRegionaleRoutingModule],
  declarations: [
    DirectionRegionaleComponent,
    DirectionRegionaleDetailComponent,
    DirectionRegionaleUpdateComponent,
    DirectionRegionaleDeleteDialogComponent,
  ],
  entryComponents: [DirectionRegionaleDeleteDialogComponent],
})
export class GestioneauDirectionRegionaleModule {}
