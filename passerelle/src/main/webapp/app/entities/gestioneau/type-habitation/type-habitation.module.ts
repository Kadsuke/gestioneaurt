import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { TypeHabitationComponent } from './list/type-habitation.component';
import { TypeHabitationDetailComponent } from './detail/type-habitation-detail.component';
import { TypeHabitationUpdateComponent } from './update/type-habitation-update.component';
import { TypeHabitationDeleteDialogComponent } from './delete/type-habitation-delete-dialog.component';
import { TypeHabitationRoutingModule } from './route/type-habitation-routing.module';

@NgModule({
  imports: [SharedModule, TypeHabitationRoutingModule],
  declarations: [
    TypeHabitationComponent,
    TypeHabitationDetailComponent,
    TypeHabitationUpdateComponent,
    TypeHabitationDeleteDialogComponent,
  ],
  entryComponents: [TypeHabitationDeleteDialogComponent],
})
export class GestioneauTypeHabitationModule {}
