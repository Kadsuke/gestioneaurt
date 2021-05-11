import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICentreRegroupement, getCentreRegroupementIdentifier } from '../centre-regroupement.model';

export type EntityResponseType = HttpResponse<ICentreRegroupement>;
export type EntityArrayResponseType = HttpResponse<ICentreRegroupement[]>;

@Injectable({ providedIn: 'root' })
export class CentreRegroupementService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/centre-regroupements', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/centre-regroupements', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(centreRegroupement: ICentreRegroupement): Observable<EntityResponseType> {
    return this.http.post<ICentreRegroupement>(this.resourceUrl, centreRegroupement, { observe: 'response' });
  }

  update(centreRegroupement: ICentreRegroupement): Observable<EntityResponseType> {
    return this.http.put<ICentreRegroupement>(
      `${this.resourceUrl}/${getCentreRegroupementIdentifier(centreRegroupement) as number}`,
      centreRegroupement,
      { observe: 'response' }
    );
  }

  partialUpdate(centreRegroupement: ICentreRegroupement): Observable<EntityResponseType> {
    return this.http.patch<ICentreRegroupement>(
      `${this.resourceUrl}/${getCentreRegroupementIdentifier(centreRegroupement) as number}`,
      centreRegroupement,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICentreRegroupement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentreRegroupement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICentreRegroupement[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addCentreRegroupementToCollectionIfMissing(
    centreRegroupementCollection: ICentreRegroupement[],
    ...centreRegroupementsToCheck: (ICentreRegroupement | null | undefined)[]
  ): ICentreRegroupement[] {
    const centreRegroupements: ICentreRegroupement[] = centreRegroupementsToCheck.filter(isPresent);
    if (centreRegroupements.length > 0) {
      const centreRegroupementCollectionIdentifiers = centreRegroupementCollection.map(
        centreRegroupementItem => getCentreRegroupementIdentifier(centreRegroupementItem)!
      );
      const centreRegroupementsToAdd = centreRegroupements.filter(centreRegroupementItem => {
        const centreRegroupementIdentifier = getCentreRegroupementIdentifier(centreRegroupementItem);
        if (centreRegroupementIdentifier == null || centreRegroupementCollectionIdentifiers.includes(centreRegroupementIdentifier)) {
          return false;
        }
        centreRegroupementCollectionIdentifiers.push(centreRegroupementIdentifier);
        return true;
      });
      return [...centreRegroupementsToAdd, ...centreRegroupementCollection];
    }
    return centreRegroupementCollection;
  }
}
