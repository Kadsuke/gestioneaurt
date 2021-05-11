import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDirectionRegionale, DirectionRegionale } from '../direction-regionale.model';
import { DirectionRegionaleService } from '../service/direction-regionale.service';

@Injectable({ providedIn: 'root' })
export class DirectionRegionaleRoutingResolveService implements Resolve<IDirectionRegionale> {
  constructor(protected service: DirectionRegionaleService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDirectionRegionale> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((directionRegionale: HttpResponse<DirectionRegionale>) => {
          if (directionRegionale.body) {
            return of(directionRegionale.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new DirectionRegionale());
  }
}
