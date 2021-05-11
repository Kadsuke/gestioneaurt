import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { NatureOuvrageComponent } from './list/nature-ouvrage.component';
import { NatureOuvrageDetailComponent } from './detail/nature-ouvrage-detail.component';
import { NatureOuvrageUpdateComponent } from './update/nature-ouvrage-update.component';
import { NatureOuvrageDeleteDialogComponent } from './delete/nature-ouvrage-delete-dialog.component';
import { NatureOuvrageRoutingModule } from './route/nature-ouvrage-routing.module';

@NgModule({
  imports: [SharedModule, NatureOuvrageRoutingModule],
  declarations: [NatureOuvrageComponent, NatureOuvrageDetailComponent, NatureOuvrageUpdateComponent, NatureOuvrageDeleteDialogComponent],
  entryComponents: [NatureOuvrageDeleteDialogComponent],
})
export class GestioneauNatureOuvrageModule {}
