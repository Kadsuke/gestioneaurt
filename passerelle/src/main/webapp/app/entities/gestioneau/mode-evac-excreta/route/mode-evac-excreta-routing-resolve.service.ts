import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IModeEvacExcreta, ModeEvacExcreta } from '../mode-evac-excreta.model';
import { ModeEvacExcretaService } from '../service/mode-evac-excreta.service';

@Injectable({ providedIn: 'root' })
export class ModeEvacExcretaRoutingResolveService implements Resolve<IModeEvacExcreta> {
  constructor(protected service: ModeEvacExcretaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IModeEvacExcreta> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((modeEvacExcreta: HttpResponse<ModeEvacExcreta>) => {
          if (modeEvacExcreta.body) {
            return of(modeEvacExcreta.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new ModeEvacExcreta());
  }
}
