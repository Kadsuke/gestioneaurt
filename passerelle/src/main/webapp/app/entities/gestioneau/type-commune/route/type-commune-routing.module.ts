import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TypeCommuneComponent } from '../list/type-commune.component';
import { TypeCommuneDetailComponent } from '../detail/type-commune-detail.component';
import { TypeCommuneUpdateComponent } from '../update/type-commune-update.component';
import { TypeCommuneRoutingResolveService } from './type-commune-routing-resolve.service';

const typeCommuneRoute: Routes = [
  {
    path: '',
    component: TypeCommuneComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TypeCommuneDetailComponent,
    resolve: {
      typeCommune: TypeCommuneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TypeCommuneUpdateComponent,
    resolve: {
      typeCommune: TypeCommuneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TypeCommuneUpdateComponent,
    resolve: {
      typeCommune: TypeCommuneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(typeCommuneRoute)],
  exports: [RouterModule],
})
export class TypeCommuneRoutingModule {}
