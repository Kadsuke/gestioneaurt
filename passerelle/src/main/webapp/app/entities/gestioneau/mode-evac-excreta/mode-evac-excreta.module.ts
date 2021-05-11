import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { ModeEvacExcretaComponent } from './list/mode-evac-excreta.component';
import { ModeEvacExcretaDetailComponent } from './detail/mode-evac-excreta-detail.component';
import { ModeEvacExcretaUpdateComponent } from './update/mode-evac-excreta-update.component';
import { ModeEvacExcretaDeleteDialogComponent } from './delete/mode-evac-excreta-delete-dialog.component';
import { ModeEvacExcretaRoutingModule } from './route/mode-evac-excreta-routing.module';

@NgModule({
  imports: [SharedModule, ModeEvacExcretaRoutingModule],
  declarations: [
    ModeEvacExcretaComponent,
    ModeEvacExcretaDetailComponent,
    ModeEvacExcretaUpdateComponent,
    ModeEvacExcretaDeleteDialogComponent,
  ],
  entryComponents: [ModeEvacExcretaDeleteDialogComponent],
})
export class GestioneauModeEvacExcretaModule {}
