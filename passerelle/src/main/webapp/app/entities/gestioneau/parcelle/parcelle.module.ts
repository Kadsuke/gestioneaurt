import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { ParcelleComponent } from './list/parcelle.component';
import { ParcelleDetailComponent } from './detail/parcelle-detail.component';
import { ParcelleUpdateComponent } from './update/parcelle-update.component';
import { ParcelleDeleteDialogComponent } from './delete/parcelle-delete-dialog.component';
import { ParcelleRoutingModule } from './route/parcelle-routing.module';

@NgModule({
  imports: [SharedModule, ParcelleRoutingModule],
  declarations: [ParcelleComponent, ParcelleDetailComponent, ParcelleUpdateComponent, ParcelleDeleteDialogComponent],
  entryComponents: [ParcelleDeleteDialogComponent],
})
export class GestioneauParcelleModule {}
