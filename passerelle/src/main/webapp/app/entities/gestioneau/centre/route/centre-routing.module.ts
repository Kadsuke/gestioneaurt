import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CentreComponent } from '../list/centre.component';
import { CentreDetailComponent } from '../detail/centre-detail.component';
import { CentreUpdateComponent } from '../update/centre-update.component';
import { CentreRoutingResolveService } from './centre-routing-resolve.service';

const centreRoute: Routes = [
  {
    path: '',
    component: CentreComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CentreDetailComponent,
    resolve: {
      centre: CentreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CentreUpdateComponent,
    resolve: {
      centre: CentreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CentreUpdateComponent,
    resolve: {
      centre: CentreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centreRoute)],
  exports: [RouterModule],
})
export class CentreRoutingModule {}
