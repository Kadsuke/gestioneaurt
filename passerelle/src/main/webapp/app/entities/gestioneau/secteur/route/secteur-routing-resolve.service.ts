import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISecteur, Secteur } from '../secteur.model';
import { SecteurService } from '../service/secteur.service';

@Injectable({ providedIn: 'root' })
export class SecteurRoutingResolveService implements Resolve<ISecteur> {
  constructor(protected service: SecteurService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISecteur> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((secteur: HttpResponse<Secteur>) => {
          if (secteur.body) {
            return of(secteur.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Secteur());
  }
}
