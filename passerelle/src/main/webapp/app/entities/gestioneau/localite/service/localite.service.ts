import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ILocalite, getLocaliteIdentifier } from '../localite.model';

export type EntityResponseType = HttpResponse<ILocalite>;
export type EntityArrayResponseType = HttpResponse<ILocalite[]>;

@Injectable({ providedIn: 'root' })
export class LocaliteService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/localites', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/localites', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(localite: ILocalite): Observable<EntityResponseType> {
    return this.http.post<ILocalite>(this.resourceUrl, localite, { observe: 'response' });
  }

  update(localite: ILocalite): Observable<EntityResponseType> {
    return this.http.put<ILocalite>(`${this.resourceUrl}/${getLocaliteIdentifier(localite) as number}`, localite, { observe: 'response' });
  }

  partialUpdate(localite: ILocalite): Observable<EntityResponseType> {
    return this.http.patch<ILocalite>(`${this.resourceUrl}/${getLocaliteIdentifier(localite) as number}`, localite, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILocalite>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILocalite[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILocalite[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addLocaliteToCollectionIfMissing(localiteCollection: ILocalite[], ...localitesToCheck: (ILocalite | null | undefined)[]): ILocalite[] {
    const localites: ILocalite[] = localitesToCheck.filter(isPresent);
    if (localites.length > 0) {
      const localiteCollectionIdentifiers = localiteCollection.map(localiteItem => getLocaliteIdentifier(localiteItem)!);
      const localitesToAdd = localites.filter(localiteItem => {
        const localiteIdentifier = getLocaliteIdentifier(localiteItem);
        if (localiteIdentifier == null || localiteCollectionIdentifiers.includes(localiteIdentifier)) {
          return false;
        }
        localiteCollectionIdentifiers.push(localiteIdentifier);
        return true;
      });
      return [...localitesToAdd, ...localiteCollection];
    }
    return localiteCollection;
  }
}
