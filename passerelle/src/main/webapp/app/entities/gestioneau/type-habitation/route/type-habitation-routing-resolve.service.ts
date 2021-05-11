import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITypeHabitation, TypeHabitation } from '../type-habitation.model';
import { TypeHabitationService } from '../service/type-habitation.service';

@Injectable({ providedIn: 'root' })
export class TypeHabitationRoutingResolveService implements Resolve<ITypeHabitation> {
  constructor(protected service: TypeHabitationService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITypeHabitation> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((typeHabitation: HttpResponse<TypeHabitation>) => {
          if (typeHabitation.body) {
            return of(typeHabitation.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TypeHabitation());
  }
}
