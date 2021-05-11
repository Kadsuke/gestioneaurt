import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProvinceComponent } from '../list/province.component';
import { ProvinceDetailComponent } from '../detail/province-detail.component';
import { ProvinceUpdateComponent } from '../update/province-update.component';
import { ProvinceRoutingResolveService } from './province-routing-resolve.service';

const provinceRoute: Routes = [
  {
    path: '',
    component: ProvinceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProvinceDetailComponent,
    resolve: {
      province: ProvinceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProvinceUpdateComponent,
    resolve: {
      province: ProvinceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProvinceUpdateComponent,
    resolve: {
      province: ProvinceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(provinceRoute)],
  exports: [RouterModule],
})
export class ProvinceRoutingModule {}
