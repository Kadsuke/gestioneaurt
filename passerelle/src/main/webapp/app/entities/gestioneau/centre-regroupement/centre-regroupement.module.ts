import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { CentreRegroupementComponent } from './list/centre-regroupement.component';
import { CentreRegroupementDetailComponent } from './detail/centre-regroupement-detail.component';
import { CentreRegroupementUpdateComponent } from './update/centre-regroupement-update.component';
import { CentreRegroupementDeleteDialogComponent } from './delete/centre-regroupement-delete-dialog.component';
import { CentreRegroupementRoutingModule } from './route/centre-regroupement-routing.module';

@NgModule({
  imports: [SharedModule, CentreRegroupementRoutingModule],
  declarations: [
    CentreRegroupementComponent,
    CentreRegroupementDetailComponent,
    CentreRegroupementUpdateComponent,
    CentreRegroupementDeleteDialogComponent,
  ],
  entryComponents: [CentreRegroupementDeleteDialogComponent],
})
export class GestioneauCentreRegroupementModule {}
