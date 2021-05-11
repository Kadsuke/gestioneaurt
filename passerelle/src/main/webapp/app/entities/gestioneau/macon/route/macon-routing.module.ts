import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MaconComponent } from '../list/macon.component';
import { MaconDetailComponent } from '../detail/macon-detail.component';
import { MaconUpdateComponent } from '../update/macon-update.component';
import { MaconRoutingResolveService } from './macon-routing-resolve.service';

const maconRoute: Routes = [
  {
    path: '',
    component: MaconComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MaconDetailComponent,
    resolve: {
      macon: MaconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MaconUpdateComponent,
    resolve: {
      macon: MaconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MaconUpdateComponent,
    resolve: {
      macon: MaconRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(maconRoute)],
  exports: [RouterModule],
})
export class MaconRoutingModule {}
