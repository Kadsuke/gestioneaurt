import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AnneeComponent } from '../list/annee.component';
import { AnneeDetailComponent } from '../detail/annee-detail.component';
import { AnneeUpdateComponent } from '../update/annee-update.component';
import { AnneeRoutingResolveService } from './annee-routing-resolve.service';

const anneeRoute: Routes = [
  {
    path: '',
    component: AnneeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AnneeDetailComponent,
    resolve: {
      annee: AnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AnneeUpdateComponent,
    resolve: {
      annee: AnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AnneeUpdateComponent,
    resolve: {
      annee: AnneeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(anneeRoute)],
  exports: [RouterModule],
})
export class AnneeRoutingModule {}
