import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISection, Section } from '../section.model';
import { SectionService } from '../service/section.service';

@Injectable({ providedIn: 'root' })
export class SectionRoutingResolveService implements Resolve<ISection> {
  constructor(protected service: SectionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISection> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((section: HttpResponse<Section>) => {
          if (section.body) {
            return of(section.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Section());
  }
}
