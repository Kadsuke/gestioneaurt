import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAnnee, Annee } from '../annee.model';
import { AnneeService } from '../service/annee.service';

@Injectable({ providedIn: 'root' })
export class AnneeRoutingResolveService implements Resolve<IAnnee> {
  constructor(protected service: AnneeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAnnee> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((annee: HttpResponse<Annee>) => {
          if (annee.body) {
            return of(annee.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Annee());
  }
}
