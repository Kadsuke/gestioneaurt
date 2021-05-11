import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INatureOuvrage, NatureOuvrage } from '../nature-ouvrage.model';
import { NatureOuvrageService } from '../service/nature-ouvrage.service';

@Injectable({ providedIn: 'root' })
export class NatureOuvrageRoutingResolveService implements Resolve<INatureOuvrage> {
  constructor(protected service: NatureOuvrageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INatureOuvrage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((natureOuvrage: HttpResponse<NatureOuvrage>) => {
          if (natureOuvrage.body) {
            return of(natureOuvrage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new NatureOuvrage());
  }
}
