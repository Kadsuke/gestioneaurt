import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { PrevisionComponent } from './list/prevision.component';
import { PrevisionDetailComponent } from './detail/prevision-detail.component';
import { PrevisionUpdateComponent } from './update/prevision-update.component';
import { PrevisionDeleteDialogComponent } from './delete/prevision-delete-dialog.component';
import { PrevisionRoutingModule } from './route/prevision-routing.module';

@NgModule({
  imports: [SharedModule, PrevisionRoutingModule],
  declarations: [PrevisionComponent, PrevisionDetailComponent, PrevisionUpdateComponent, PrevisionDeleteDialogComponent],
  entryComponents: [PrevisionDeleteDialogComponent],
})
export class GestioneauPrevisionModule {}
