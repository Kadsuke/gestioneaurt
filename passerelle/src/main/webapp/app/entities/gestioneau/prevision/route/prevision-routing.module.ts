import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PrevisionComponent } from '../list/prevision.component';
import { PrevisionDetailComponent } from '../detail/prevision-detail.component';
import { PrevisionUpdateComponent } from '../update/prevision-update.component';
import { PrevisionRoutingResolveService } from './prevision-routing-resolve.service';

const previsionRoute: Routes = [
  {
    path: '',
    component: PrevisionComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PrevisionDetailComponent,
    resolve: {
      prevision: PrevisionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PrevisionUpdateComponent,
    resolve: {
      prevision: PrevisionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PrevisionUpdateComponent,
    resolve: {
      prevision: PrevisionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(previsionRoute)],
  exports: [RouterModule],
})
export class PrevisionRoutingModule {}
