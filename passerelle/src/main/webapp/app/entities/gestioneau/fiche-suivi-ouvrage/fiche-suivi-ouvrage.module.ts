import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { FicheSuiviOuvrageComponent } from './list/fiche-suivi-ouvrage.component';
import { FicheSuiviOuvrageDetailComponent } from './detail/fiche-suivi-ouvrage-detail.component';
import { FicheSuiviOuvrageUpdateComponent } from './update/fiche-suivi-ouvrage-update.component';
import { FicheSuiviOuvrageDeleteDialogComponent } from './delete/fiche-suivi-ouvrage-delete-dialog.component';
import { FicheSuiviOuvrageRoutingModule } from './route/fiche-suivi-ouvrage-routing.module';

@NgModule({
  imports: [SharedModule, FicheSuiviOuvrageRoutingModule],
  declarations: [
    FicheSuiviOuvrageComponent,
    FicheSuiviOuvrageDetailComponent,
    FicheSuiviOuvrageUpdateComponent,
    FicheSuiviOuvrageDeleteDialogComponent,
  ],
  entryComponents: [FicheSuiviOuvrageDeleteDialogComponent],
})
export class GestioneauFicheSuiviOuvrageModule {}
