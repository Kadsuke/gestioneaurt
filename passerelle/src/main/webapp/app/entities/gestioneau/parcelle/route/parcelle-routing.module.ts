import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ParcelleComponent } from '../list/parcelle.component';
import { ParcelleDetailComponent } from '../detail/parcelle-detail.component';
import { ParcelleUpdateComponent } from '../update/parcelle-update.component';
import { ParcelleRoutingResolveService } from './parcelle-routing-resolve.service';

const parcelleRoute: Routes = [
  {
    path: '',
    component: ParcelleComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ParcelleDetailComponent,
    resolve: {
      parcelle: ParcelleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ParcelleUpdateComponent,
    resolve: {
      parcelle: ParcelleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ParcelleUpdateComponent,
    resolve: {
      parcelle: ParcelleRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(parcelleRoute)],
  exports: [RouterModule],
})
export class ParcelleRoutingModule {}
