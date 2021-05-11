import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SourceApprovEpComponent } from './list/source-approv-ep.component';
import { SourceApprovEpDetailComponent } from './detail/source-approv-ep-detail.component';
import { SourceApprovEpUpdateComponent } from './update/source-approv-ep-update.component';
import { SourceApprovEpDeleteDialogComponent } from './delete/source-approv-ep-delete-dialog.component';
import { SourceApprovEpRoutingModule } from './route/source-approv-ep-routing.module';

@NgModule({
  imports: [SharedModule, SourceApprovEpRoutingModule],
  declarations: [
    SourceApprovEpComponent,
    SourceApprovEpDetailComponent,
    SourceApprovEpUpdateComponent,
    SourceApprovEpDeleteDialogComponent,
  ],
  entryComponents: [SourceApprovEpDeleteDialogComponent],
})
export class GestioneauSourceApprovEpModule {}
