import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPrefabricant, getPrefabricantIdentifier } from '../prefabricant.model';

export type EntityResponseType = HttpResponse<IPrefabricant>;
export type EntityArrayResponseType = HttpResponse<IPrefabricant[]>;

@Injectable({ providedIn: 'root' })
export class PrefabricantService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/prefabricants', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/prefabricants', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(prefabricant: IPrefabricant): Observable<EntityResponseType> {
    return this.http.post<IPrefabricant>(this.resourceUrl, prefabricant, { observe: 'response' });
  }

  update(prefabricant: IPrefabricant): Observable<EntityResponseType> {
    return this.http.put<IPrefabricant>(`${this.resourceUrl}/${getPrefabricantIdentifier(prefabricant) as number}`, prefabricant, {
      observe: 'response',
    });
  }

  partialUpdate(prefabricant: IPrefabricant): Observable<EntityResponseType> {
    return this.http.patch<IPrefabricant>(`${this.resourceUrl}/${getPrefabricantIdentifier(prefabricant) as number}`, prefabricant, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPrefabricant>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrefabricant[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPrefabricant[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addPrefabricantToCollectionIfMissing(
    prefabricantCollection: IPrefabricant[],
    ...prefabricantsToCheck: (IPrefabricant | null | undefined)[]
  ): IPrefabricant[] {
    const prefabricants: IPrefabricant[] = prefabricantsToCheck.filter(isPresent);
    if (prefabricants.length > 0) {
      const prefabricantCollectionIdentifiers = prefabricantCollection.map(
        prefabricantItem => getPrefabricantIdentifier(prefabricantItem)!
      );
      const prefabricantsToAdd = prefabricants.filter(prefabricantItem => {
        const prefabricantIdentifier = getPrefabricantIdentifier(prefabricantItem);
        if (prefabricantIdentifier == null || prefabricantCollectionIdentifiers.includes(prefabricantIdentifier)) {
          return false;
        }
        prefabricantCollectionIdentifiers.push(prefabricantIdentifier);
        return true;
      });
      return [...prefabricantsToAdd, ...prefabricantCollection];
    }
    return prefabricantCollection;
  }
}
