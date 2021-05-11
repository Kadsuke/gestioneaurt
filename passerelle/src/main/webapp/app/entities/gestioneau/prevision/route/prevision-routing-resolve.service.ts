import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPrevision, Prevision } from '../prevision.model';
import { PrevisionService } from '../service/prevision.service';

@Injectable({ providedIn: 'root' })
export class PrevisionRoutingResolveService implements Resolve<IPrevision> {
  constructor(protected service: PrevisionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPrevision> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((prevision: HttpResponse<Prevision>) => {
          if (prevision.body) {
            return of(prevision.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Prevision());
  }
}
