import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { AnneeComponent } from './list/annee.component';
import { AnneeDetailComponent } from './detail/annee-detail.component';
import { AnneeUpdateComponent } from './update/annee-update.component';
import { AnneeDeleteDialogComponent } from './delete/annee-delete-dialog.component';
import { AnneeRoutingModule } from './route/annee-routing.module';

@NgModule({
  imports: [SharedModule, AnneeRoutingModule],
  declarations: [AnneeComponent, AnneeDetailComponent, AnneeUpdateComponent, AnneeDeleteDialogComponent],
  entryComponents: [AnneeDeleteDialogComponent],
})
export class GestioneauAnneeModule {}
