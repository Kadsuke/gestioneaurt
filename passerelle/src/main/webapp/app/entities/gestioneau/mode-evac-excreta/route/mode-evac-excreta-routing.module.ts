import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ModeEvacExcretaComponent } from '../list/mode-evac-excreta.component';
import { ModeEvacExcretaDetailComponent } from '../detail/mode-evac-excreta-detail.component';
import { ModeEvacExcretaUpdateComponent } from '../update/mode-evac-excreta-update.component';
import { ModeEvacExcretaRoutingResolveService } from './mode-evac-excreta-routing-resolve.service';

const modeEvacExcretaRoute: Routes = [
  {
    path: '',
    component: ModeEvacExcretaComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ModeEvacExcretaDetailComponent,
    resolve: {
      modeEvacExcreta: ModeEvacExcretaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ModeEvacExcretaUpdateComponent,
    resolve: {
      modeEvacExcreta: ModeEvacExcretaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ModeEvacExcretaUpdateComponent,
    resolve: {
      modeEvacExcreta: ModeEvacExcretaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(modeEvacExcretaRoute)],
  exports: [RouterModule],
})
export class ModeEvacExcretaRoutingModule {}
