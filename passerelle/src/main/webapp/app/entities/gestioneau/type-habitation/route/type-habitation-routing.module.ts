import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TypeHabitationComponent } from '../list/type-habitation.component';
import { TypeHabitationDetailComponent } from '../detail/type-habitation-detail.component';
import { TypeHabitationUpdateComponent } from '../update/type-habitation-update.component';
import { TypeHabitationRoutingResolveService } from './type-habitation-routing-resolve.service';

const typeHabitationRoute: Routes = [
  {
    path: '',
    component: TypeHabitationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TypeHabitationDetailComponent,
    resolve: {
      typeHabitation: TypeHabitationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TypeHabitationUpdateComponent,
    resolve: {
      typeHabitation: TypeHabitationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TypeHabitationUpdateComponent,
    resolve: {
      typeHabitation: TypeHabitationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(typeHabitationRoute)],
  exports: [RouterModule],
})
export class TypeHabitationRoutingModule {}
