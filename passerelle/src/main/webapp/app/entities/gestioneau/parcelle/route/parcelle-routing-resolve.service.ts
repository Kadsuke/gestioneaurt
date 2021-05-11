import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParcelle, Parcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';

@Injectable({ providedIn: 'root' })
export class ParcelleRoutingResolveService implements Resolve<IParcelle> {
  constructor(protected service: ParcelleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParcelle> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((parcelle: HttpResponse<Parcelle>) => {
          if (parcelle.body) {
            return of(parcelle.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Parcelle());
  }
}
