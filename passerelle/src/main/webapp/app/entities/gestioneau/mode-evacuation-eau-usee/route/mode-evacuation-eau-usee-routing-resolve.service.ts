import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IModeEvacuationEauUsee, ModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';
import { ModeEvacuationEauUseeService } from '../service/mode-evacuation-eau-usee.service';

@Injectable({ providedIn: 'root' })
export class ModeEvacuationEauUseeRoutingResolveService implements Resolve<IModeEvacuationEauUsee> {
  constructor(protected service: ModeEvacuationEauUseeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IModeEvacuationEauUsee> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((modeEvacuationEauUsee: HttpResponse<ModeEvacuationEauUsee>) => {
          if (modeEvacuationEauUsee.body) {
            return of(modeEvacuationEauUsee.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ModeEvacuationEauUsee());
  }
}
