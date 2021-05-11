import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProvince, Province } from '../province.model';
import { ProvinceService } from '../service/province.service';

@Injectable({ providedIn: 'root' })
export class ProvinceRoutingResolveService implements Resolve<IProvince> {
  constructor(protected service: ProvinceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProvince> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((province: HttpResponse<Province>) => {
          if (province.body) {
            return of(province.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Province());
  }
}
