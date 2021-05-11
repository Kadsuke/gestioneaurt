import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SecteurComponent } from '../list/secteur.component';
import { SecteurDetailComponent } from '../detail/secteur-detail.component';
import { SecteurUpdateComponent } from '../update/secteur-update.component';
import { SecteurRoutingResolveService } from './secteur-routing-resolve.service';

const secteurRoute: Routes = [
  {
    path: '',
    component: SecteurComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SecteurDetailComponent,
    resolve: {
      secteur: SecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SecteurUpdateComponent,
    resolve: {
      secteur: SecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SecteurUpdateComponent,
    resolve: {
      secteur: SecteurRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(secteurRoute)],
  exports: [RouterModule],
})
export class SecteurRoutingModule {}
