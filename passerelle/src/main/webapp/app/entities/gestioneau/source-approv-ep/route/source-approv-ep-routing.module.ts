import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SourceApprovEpComponent } from '../list/source-approv-ep.component';
import { SourceApprovEpDetailComponent } from '../detail/source-approv-ep-detail.component';
import { SourceApprovEpUpdateComponent } from '../update/source-approv-ep-update.component';
import { SourceApprovEpRoutingResolveService } from './source-approv-ep-routing-resolve.service';

const sourceApprovEpRoute: Routes = [
  {
    path: '',
    component: SourceApprovEpComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SourceApprovEpDetailComponent,
    resolve: {
      sourceApprovEp: SourceApprovEpRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SourceApprovEpUpdateComponent,
    resolve: {
      sourceApprovEp: SourceApprovEpRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SourceApprovEpUpdateComponent,
    resolve: {
      sourceApprovEp: SourceApprovEpRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(sourceApprovEpRoute)],
  exports: [RouterModule],
})
export class SourceApprovEpRoutingModule {}
