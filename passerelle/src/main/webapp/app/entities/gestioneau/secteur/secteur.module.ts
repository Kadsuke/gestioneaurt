import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SecteurComponent } from './list/secteur.component';
import { SecteurDetailComponent } from './detail/secteur-detail.component';
import { SecteurUpdateComponent } from './update/secteur-update.component';
import { SecteurDeleteDialogComponent } from './delete/secteur-delete-dialog.component';
import { SecteurRoutingModule } from './route/secteur-routing.module';

@NgModule({
  imports: [SharedModule, SecteurRoutingModule],
  declarations: [SecteurComponent, SecteurDetailComponent, SecteurUpdateComponent, SecteurDeleteDialogComponent],
  entryComponents: [SecteurDeleteDialogComponent],
})
export class GestioneauSecteurModule {}
