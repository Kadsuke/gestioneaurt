import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILocalite, Localite } from '../localite.model';
import { LocaliteService } from '../service/localite.service';

@Injectable({ providedIn: 'root' })
export class LocaliteRoutingResolveService implements Resolve<ILocalite> {
  constructor(protected service: LocaliteService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILocalite> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((localite: HttpResponse<Localite>) => {
          if (localite.body) {
            return of(localite.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Localite());
  }
}
