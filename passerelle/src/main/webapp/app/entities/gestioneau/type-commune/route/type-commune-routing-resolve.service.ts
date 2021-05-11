import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITypeCommune, TypeCommune } from '../type-commune.model';
import { TypeCommuneService } from '../service/type-commune.service';

@Injectable({ providedIn: 'root' })
export class TypeCommuneRoutingResolveService implements Resolve<ITypeCommune> {
  constructor(protected service: TypeCommuneService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITypeCommune> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((typeCommune: HttpResponse<TypeCommune>) => {
          if (typeCommune.body) {
            return of(typeCommune.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TypeCommune());
  }
}
