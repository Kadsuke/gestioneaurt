import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFicheSuiviOuvrage, FicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';
import { FicheSuiviOuvrageService } from '../service/fiche-suivi-ouvrage.service';

@Injectable({ providedIn: 'root' })
export class FicheSuiviOuvrageRoutingResolveService implements Resolve<IFicheSuiviOuvrage> {
  constructor(protected service: FicheSuiviOuvrageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFicheSuiviOuvrage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ficheSuiviOuvrage: HttpResponse<FicheSuiviOuvrage>) => {
          if (ficheSuiviOuvrage.body) {
            return of(ficheSuiviOuvrage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new FicheSuiviOuvrage());
  }
}
