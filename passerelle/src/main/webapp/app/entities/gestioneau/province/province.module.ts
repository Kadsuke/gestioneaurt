import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { ProvinceComponent } from './list/province.component';
import { ProvinceDetailComponent } from './detail/province-detail.component';
import { ProvinceUpdateComponent } from './update/province-update.component';
import { ProvinceDeleteDialogComponent } from './delete/province-delete-dialog.component';
import { ProvinceRoutingModule } from './route/province-routing.module';

@NgModule({
  imports: [SharedModule, ProvinceRoutingModule],
  declarations: [ProvinceComponent, ProvinceDetailComponent, ProvinceUpdateComponent, ProvinceDeleteDialogComponent],
  entryComponents: [ProvinceDeleteDialogComponent],
})
export class GestioneauProvinceModule {}
