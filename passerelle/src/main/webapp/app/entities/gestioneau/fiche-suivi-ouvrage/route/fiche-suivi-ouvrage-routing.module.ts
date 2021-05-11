import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FicheSuiviOuvrageComponent } from '../list/fiche-suivi-ouvrage.component';
import { FicheSuiviOuvrageDetailComponent } from '../detail/fiche-suivi-ouvrage-detail.component';
import { FicheSuiviOuvrageUpdateComponent } from '../update/fiche-suivi-ouvrage-update.component';
import { FicheSuiviOuvrageRoutingResolveService } from './fiche-suivi-ouvrage-routing-resolve.service';

const ficheSuiviOuvrageRoute: Routes = [
  {
    path: '',
    component: FicheSuiviOuvrageComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FicheSuiviOuvrageDetailComponent,
    resolve: {
      ficheSuiviOuvrage: FicheSuiviOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FicheSuiviOuvrageUpdateComponent,
    resolve: {
      ficheSuiviOuvrage: FicheSuiviOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FicheSuiviOuvrageUpdateComponent,
    resolve: {
      ficheSuiviOuvrage: FicheSuiviOuvrageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ficheSuiviOuvrageRoute)],
  exports: [RouterModule],
})
export class FicheSuiviOuvrageRoutingModule {}
