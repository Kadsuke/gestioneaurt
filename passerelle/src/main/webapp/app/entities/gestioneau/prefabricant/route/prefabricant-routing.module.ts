import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PrefabricantComponent } from '../list/prefabricant.component';
import { PrefabricantDetailComponent } from '../detail/prefabricant-detail.component';
import { PrefabricantUpdateComponent } from '../update/prefabricant-update.component';
import { PrefabricantRoutingResolveService } from './prefabricant-routing-resolve.service';

const prefabricantRoute: Routes = [
  {
    path: '',
    component: PrefabricantComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PrefabricantDetailComponent,
    resolve: {
      prefabricant: PrefabricantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PrefabricantUpdateComponent,
    resolve: {
      prefabricant: PrefabricantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PrefabricantUpdateComponent,
    resolve: {
      prefabricant: PrefabricantRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(prefabricantRoute)],
  exports: [RouterModule],
})
export class PrefabricantRoutingModule {}
