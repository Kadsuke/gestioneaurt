import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICentre, getCentreIdentifier } from '../centre.model';

export type EntityResponseType = HttpResponse<ICentre>;
export type EntityArrayResponseType = HttpResponse<ICentre[]>;

@Injectable({ providedIn: 'root' })
export class CentreService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/centres', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/centres', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(centre: ICentre): Observable<EntityResponseType> {
    return this.http.post<ICentre>(this.resourceUrl, centre, { observe: 'response' });
  }

  update(centre: ICentre): Observable<EntityResponseType> {
    return this.http.put<ICentre>(`${this.resourceUrl}/${getCentreIdentifier(centre) as number}`, centre, { observe: 'response' });
  }

  partialUpdate(centre: ICentre): Observable<EntityResponseType> {
    return this.http.patch<ICentre>(`${this.resourceUrl}/${getCentreIdentifier(centre) as number}`, centre, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICentre>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentre[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentre[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCentreToCollectionIfMissing(centreCollection: ICentre[], ...centresToCheck: (ICentre | null | undefined)[]): ICentre[] {
    const centres: ICentre[] = centresToCheck.filter(isPresent);
    if (centres.length > 0) {
      const centreCollectionIdentifiers = centreCollection.map(centreItem => getCentreIdentifier(centreItem)!);
      const centresToAdd = centres.filter(centreItem => {
        const centreIdentifier = getCentreIdentifier(centreItem);
        if (centreIdentifier == null || centreCollectionIdentifiers.includes(centreIdentifier)) {
          return false;
        }
        centreCollectionIdentifiers.push(centreIdentifier);
        return true;
      });
      return [...centresToAdd, ...centreCollection];
    }
    return centreCollection;
  }
}
