import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICentreRegroupement, CentreRegroupement } from '../centre-regroupement.model';
import { CentreRegroupementService } from '../service/centre-regroupement.service';

@Injectable({ providedIn: 'root' })
export class CentreRegroupementRoutingResolveService implements Resolve<ICentreRegroupement> {
  constructor(protected service: CentreRegroupementService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICentreRegroupement> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((centreRegroupement: HttpResponse<CentreRegroupement>) => {
          if (centreRegroupement.body) {
            return of(centreRegroupement.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new CentreRegroupement());
  }
}
