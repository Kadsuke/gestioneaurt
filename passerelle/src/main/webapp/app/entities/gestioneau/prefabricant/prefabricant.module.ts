import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { PrefabricantComponent } from './list/prefabricant.component';
import { PrefabricantDetailComponent } from './detail/prefabricant-detail.component';
import { PrefabricantUpdateComponent } from './update/prefabricant-update.component';
import { PrefabricantDeleteDialogComponent } from './delete/prefabricant-delete-dialog.component';
import { PrefabricantRoutingModule } from './route/prefabricant-routing.module';

@NgModule({
  imports: [SharedModule, PrefabricantRoutingModule],
  declarations: [PrefabricantComponent, PrefabricantDetailComponent, PrefabricantUpdateComponent, PrefabricantDeleteDialogComponent],
  entryComponents: [PrefabricantDeleteDialogComponent],
})
export class GestioneauPrefabricantModule {}
