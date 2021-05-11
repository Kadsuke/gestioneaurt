import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { LocaliteComponent } from './list/localite.component';
import { LocaliteDetailComponent } from './detail/localite-detail.component';
import { LocaliteUpdateComponent } from './update/localite-update.component';
import { LocaliteDeleteDialogComponent } from './delete/localite-delete-dialog.component';
import { LocaliteRoutingModule } from './route/localite-routing.module';

@NgModule({
  imports: [SharedModule, LocaliteRoutingModule],
  declarations: [LocaliteComponent, LocaliteDetailComponent, LocaliteUpdateComponent, LocaliteDeleteDialogComponent],
  entryComponents: [LocaliteDeleteDialogComponent],
})
export class GestioneauLocaliteModule {}
