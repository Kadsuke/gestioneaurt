import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DirectionRegionaleComponent } from '../list/direction-regionale.component';
import { DirectionRegionaleDetailComponent } from '../detail/direction-regionale-detail.component';
import { DirectionRegionaleUpdateComponent } from '../update/direction-regionale-update.component';
import { DirectionRegionaleRoutingResolveService } from './direction-regionale-routing-resolve.service';

const directionRegionaleRoute: Routes = [
  {
    path: '',
    component: DirectionRegionaleComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DirectionRegionaleDetailComponent,
    resolve: {
      directionRegionale: DirectionRegionaleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DirectionRegionaleUpdateComponent,
    resolve: {
      directionRegionale: DirectionRegionaleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DirectionRegionaleUpdateComponent,
    resolve: {
      directionRegionale: DirectionRegionaleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(directionRegionaleRoute)],
  exports: [RouterModule],
})
export class DirectionRegionaleRoutingModule {}
