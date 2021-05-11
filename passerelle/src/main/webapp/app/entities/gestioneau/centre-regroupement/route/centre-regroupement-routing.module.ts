import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CentreRegroupementComponent } from '../list/centre-regroupement.component';
import { CentreRegroupementDetailComponent } from '../detail/centre-regroupement-detail.component';
import { CentreRegroupementUpdateComponent } from '../update/centre-regroupement-update.component';
import { CentreRegroupementRoutingResolveService } from './centre-regroupement-routing-resolve.service';

const centreRegroupementRoute: Routes = [
  {
    path: '',
    component: CentreRegroupementComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CentreRegroupementDetailComponent,
    resolve: {
      centreRegroupement: CentreRegroupementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CentreRegroupementUpdateComponent,
    resolve: {
      centreRegroupement: CentreRegroupementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CentreRegroupementUpdateComponent,
    resolve: {
      centreRegroupement: CentreRegroupementRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(centreRegroupementRoute)],
  exports: [RouterModule],
})
export class CentreRegroupementRoutingModule {}
