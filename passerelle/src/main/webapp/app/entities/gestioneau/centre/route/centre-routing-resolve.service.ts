import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICentre, Centre } from '../centre.model';
import { CentreService } from '../service/centre.service';

@Injectable({ providedIn: 'root' })
export class CentreRoutingResolveService implements Resolve<ICentre> {
  constructor(protected service: CentreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICentre> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((centre: HttpResponse<Centre>) => {
          if (centre.body) {
            return of(centre.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Centre());
  }
}
