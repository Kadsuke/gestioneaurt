import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LocaliteComponent } from '../list/localite.component';
import { LocaliteDetailComponent } from '../detail/localite-detail.component';
import { LocaliteUpdateComponent } from '../update/localite-update.component';
import { LocaliteRoutingResolveService } from './localite-routing-resolve.service';

const localiteRoute: Routes = [
  {
    path: '',
    component: LocaliteComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LocaliteDetailComponent,
    resolve: {
      localite: LocaliteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LocaliteUpdateComponent,
    resolve: {
      localite: LocaliteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LocaliteUpdateComponent,
    resolve: {
      localite: LocaliteRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(localiteRoute)],
  exports: [RouterModule],
})
export class LocaliteRoutingModule {}
