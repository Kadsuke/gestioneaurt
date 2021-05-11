import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { ModeEvacuationEauUseeComponent } from './list/mode-evacuation-eau-usee.component';
import { ModeEvacuationEauUseeDetailComponent } from './detail/mode-evacuation-eau-usee-detail.component';
import { ModeEvacuationEauUseeUpdateComponent } from './update/mode-evacuation-eau-usee-update.component';
import { ModeEvacuationEauUseeDeleteDialogComponent } from './delete/mode-evacuation-eau-usee-delete-dialog.component';
import { ModeEvacuationEauUseeRoutingModule } from './route/mode-evacuation-eau-usee-routing.module';

@NgModule({
  imports: [SharedModule, ModeEvacuationEauUseeRoutingModule],
  declarations: [
    ModeEvacuationEauUseeComponent,
    ModeEvacuationEauUseeDetailComponent,
    ModeEvacuationEauUseeUpdateComponent,
    ModeEvacuationEauUseeDeleteDialogComponent,
  ],
  entryComponents: [ModeEvacuationEauUseeDeleteDialogComponent],
})
export class GestioneauModeEvacuationEauUseeModule {}
