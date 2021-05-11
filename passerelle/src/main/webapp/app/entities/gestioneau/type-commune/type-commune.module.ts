import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { TypeCommuneComponent } from './list/type-commune.component';
import { TypeCommuneDetailComponent } from './detail/type-commune-detail.component';
import { TypeCommuneUpdateComponent } from './update/type-commune-update.component';
import { TypeCommuneDeleteDialogComponent } from './delete/type-commune-delete-dialog.component';
import { TypeCommuneRoutingModule } from './route/type-commune-routing.module';

@NgModule({
  imports: [SharedModule, TypeCommuneRoutingModule],
  declarations: [TypeCommuneComponent, TypeCommuneDetailComponent, TypeCommuneUpdateComponent, TypeCommuneDeleteDialogComponent],
  entryComponents: [TypeCommuneDeleteDialogComponent],
})
export class GestioneauTypeCommuneModule {}
