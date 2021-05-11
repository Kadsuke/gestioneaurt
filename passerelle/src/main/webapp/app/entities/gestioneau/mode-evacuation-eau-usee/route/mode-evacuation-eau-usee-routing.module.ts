import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ModeEvacuationEauUseeComponent } from '../list/mode-evacuation-eau-usee.component';
import { ModeEvacuationEauUseeDetailComponent } from '../detail/mode-evacuation-eau-usee-detail.component';
import { ModeEvacuationEauUseeUpdateComponent } from '../update/mode-evacuation-eau-usee-update.component';
import { ModeEvacuationEauUseeRoutingResolveService } from './mode-evacuation-eau-usee-routing-resolve.service';

const modeEvacuationEauUseeRoute: Routes = [
  {
    path: '',
    component: ModeEvacuationEauUseeComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ModeEvacuationEauUseeDetailComponent,
    resolve: {
      modeEvacuationEauUsee: ModeEvacuationEauUseeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ModeEvacuationEauUseeUpdateComponent,
    resolve: {
      modeEvacuationEauUsee: ModeEvacuationEauUseeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ModeEvacuationEauUseeUpdateComponent,
    resolve: {
      modeEvacuationEauUsee: ModeEvacuationEauUseeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(modeEvacuationEauUseeRoute)],
  exports: [RouterModule],
})
export class ModeEvacuationEauUseeRoutingModule {}
