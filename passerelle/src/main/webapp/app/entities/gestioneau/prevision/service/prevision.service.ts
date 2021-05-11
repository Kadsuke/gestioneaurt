import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPrevision, getPrevisionIdentifier } from '../prevision.model';

export type EntityResponseType = HttpResponse<IPrevision>;
export type EntityArrayResponseType = HttpResponse<IPrevision[]>;

@Injectable({ providedIn: 'root' })
export class PrevisionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/previsions', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/previsions', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(prevision: IPrevision): Observable<EntityResponseType> {
    return this.http.post<IPrevision>(this.resourceUrl, prevision, { observe: 'response' });
  }

  update(prevision: IPrevision): Observable<EntityResponseType> {
    return this.http.put<IPrevision>(`${this.resourceUrl}/${getPrevisionIdentifier(prevision) as number}`, prevision, {
      observe: 'response',
    });
  }

  partialUpdate(prevision: IPrevision): Observable<EntityResponseType> {
    return this.http.patch<IPrevision>(`${this.resourceUrl}/${getPrevisionIdentifier(prevision) as number}`, prevision, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrevision>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrevision[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrevision[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPrevisionToCollectionIfMissing(
    previsionCollection: IPrevision[],
    ...previsionsToCheck: (IPrevision | null | undefined)[]
  ): IPrevision[] {
    const previsions: IPrevision[] = previsionsToCheck.filter(isPresent);
    if (previsions.length > 0) {
      const previsionCollectionIdentifiers = previsionCollection.map(previsionItem => getPrevisionIdentifier(previsionItem)!);
      const previsionsToAdd = previsions.filter(previsionItem => {
        const previsionIdentifier = getPrevisionIdentifier(previsionItem);
        if (previsionIdentifier == null || previsionCollectionIdentifiers.includes(previsionIdentifier)) {
          return false;
        }
        previsionCollectionIdentifiers.push(previsionIdentifier);
        return true;
      });
      return [...previsionsToAdd, ...previsionCollection];
    }
    return previsionCollection;
  }
}
