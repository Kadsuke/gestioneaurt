import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CentreComponent } from './list/centre.component';
import { CentreDetailComponent } from './detail/centre-detail.component';
import { CentreUpdateComponent } from './update/centre-update.component';
import { CentreDeleteDialogComponent } from './delete/centre-delete-dialog.component';
import { CentreRoutingModule } from './route/centre-routing.module';

@NgModule({
  imports: [SharedModule, CentreRoutingModule],
  declarations: [CentreComponent, CentreDetailComponent, CentreUpdateComponent, CentreDeleteDialogComponent],
  entryComponents: [CentreDeleteDialogComponent],
})
export class GestioneauCentreModule {}
