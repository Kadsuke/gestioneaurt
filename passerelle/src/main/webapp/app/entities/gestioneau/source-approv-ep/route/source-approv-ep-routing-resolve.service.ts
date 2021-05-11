import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISourceApprovEp, SourceApprovEp } from '../source-approv-ep.model';
import { SourceApprovEpService } from '../service/source-approv-ep.service';

@Injectable({ providedIn: 'root' })
export class SourceApprovEpRoutingResolveService implements Resolve<ISourceApprovEp> {
  constructor(protected service: SourceApprovEpService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISourceApprovEp> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((sourceApprovEp: HttpResponse<SourceApprovEp>) => {
          if (sourceApprovEp.body) {
            return of(sourceApprovEp.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SourceApprovEp());
  }
}
