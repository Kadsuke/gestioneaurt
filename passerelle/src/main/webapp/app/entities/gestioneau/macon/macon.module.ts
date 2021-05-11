import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { MaconComponent } from './list/macon.component';
import { MaconDetailComponent } from './detail/macon-detail.component';
import { MaconUpdateComponent } from './update/macon-update.component';
import { MaconDeleteDialogComponent } from './delete/macon-delete-dialog.component';
import { MaconRoutingModule } from './route/macon-routing.module';

@NgModule({
  imports: [SharedModule, MaconRoutingModule],
  declarations: [MaconComponent, MaconDetailComponent, MaconUpdateComponent, MaconDeleteDialogComponent],
  entryComponents: [MaconDeleteDialogComponent],
})
export class GestioneauMaconModule {}
