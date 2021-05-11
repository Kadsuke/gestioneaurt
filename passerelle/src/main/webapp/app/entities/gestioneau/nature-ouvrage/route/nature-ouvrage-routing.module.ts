import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NatureOuvrageComponent } from '../list/nature-ouvrage.component';
import { NatureOuvrageDetailComponent } from '../detail/nature-ouvrage-detail.component';
import { NatureOuvrageUpdateComponent } from '../update/nature-ouvrage-update.component';
import { NatureOuvrageRoutingResolveService } from './nature-ouvrage-routing-resolve.service';

const natureOuvrageRoute: Routes = [
  {
    path: '',
    component: NatureOuvrageComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NatureOuvrageDetailComponent,
    resolve: {
      natureOuvrage: NatureOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NatureOuvrageUpdateComponent,
    resolve: {
      natureOuvrage: NatureOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NatureOuvrageUpdateComponent,
    resolve: {
      natureOuvrage: NatureOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(natureOuvrageRoute)],
  exports: [RouterModule],
})
export class NatureOuvrageRoutingModule {}
