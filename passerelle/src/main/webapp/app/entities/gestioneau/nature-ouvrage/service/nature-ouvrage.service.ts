import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { INatureOuvrage, getNatureOuvrageIdentifier } from '../nature-ouvrage.model';

export type EntityResponseType = HttpResponse<INatureOuvrage>;
export type EntityArrayResponseType = HttpResponse<INatureOuvrage[]>;

@Injectable({ providedIn: 'root' })
export class NatureOuvrageService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/nature-ouvrages', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/nature-ouvrages', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(natureOuvrage: INatureOuvrage): Observable<EntityResponseType> {
    return this.http.post<INatureOuvrage>(this.resourceUrl, natureOuvrage, { observe: 'response' });
  }

  update(natureOuvrage: INatureOuvrage): Observable<EntityResponseType> {
    return this.http.put<INatureOuvrage>(`${this.resourceUrl}/${getNatureOuvrageIdentifier(natureOuvrage) as number}`, natureOuvrage, {
      observe: 'response',
    });
  }

  partialUpdate(natureOuvrage: INatureOuvrage): Observable<EntityResponseType> {
    return this.http.patch<INatureOuvrage>(`${this.resourceUrl}/${getNatureOuvrageIdentifier(natureOuvrage) as number}`, natureOuvrage, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<INatureOuvrage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INatureOuvrage[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<INatureOuvrage[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addNatureOuvrageToCollectionIfMissing(
    natureOuvrageCollection: INatureOuvrage[],
    ...natureOuvragesToCheck: (INatureOuvrage | null | undefined)[]
  ): INatureOuvrage[] {
    const natureOuvrages: INatureOuvrage[] = natureOuvragesToCheck.filter(isPresent);
    if (natureOuvrages.length > 0) {
      const natureOuvrageCollectionIdentifiers = natureOuvrageCollection.map(
        natureOuvrageItem => getNatureOuvrageIdentifier(natureOuvrageItem)!
      );
      const natureOuvragesToAdd = natureOuvrages.filter(natureOuvrageItem => {
        const natureOuvrageIdentifier = getNatureOuvrageIdentifier(natureOuvrageItem);
        if (natureOuvrageIdentifier == null || natureOuvrageCollectionIdentifiers.includes(natureOuvrageIdentifier)) {
          return false;
        }
        natureOuvrageCollectionIdentifiers.push(natureOuvrageIdentifier);
        return true;
      });
      return [...natureOuvragesToAdd, ...natureOuvrageCollection];
    }
    return natureOuvrageCollection;
  }
}
