import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMacon, Macon } from '../macon.model';
import { MaconService } from '../service/macon.service';

@Injectable({ providedIn: 'root' })
export class MaconRoutingResolveService implements Resolve<IMacon> {
  constructor(protected service: MaconService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMacon> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((macon: HttpResponse<Macon>) => {
          if (macon.body) {
            return of(macon.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Macon());
  }
}
