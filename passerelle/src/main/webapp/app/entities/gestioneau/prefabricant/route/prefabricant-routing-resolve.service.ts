import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPrefabricant, Prefabricant } from '../prefabricant.model';
import { PrefabricantService } from '../service/prefabricant.service';

@Injectable({ providedIn: 'root' })
export class PrefabricantRoutingResolveService implements Resolve<IPrefabricant> {
  constructor(protected service: PrefabricantService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPrefabricant> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((prefabricant: HttpResponse<Prefabricant>) => {
          if (prefabricant.body) {
            return of(prefabricant.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Prefabricant());
  }
}
