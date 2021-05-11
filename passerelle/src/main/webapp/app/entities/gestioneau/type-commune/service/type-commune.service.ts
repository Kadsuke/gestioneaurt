import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITypeCommune, getTypeCommuneIdentifier } from '../type-commune.model';

export type EntityResponseType = HttpResponse<ITypeCommune>;
export type EntityArrayResponseType = HttpResponse<ITypeCommune[]>;

@Injectable({ providedIn: 'root' })
export class TypeCommuneService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/type-communes', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/type-communes', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(typeCommune: ITypeCommune): Observable<EntityResponseType> {
    return this.http.post<ITypeCommune>(this.resourceUrl, typeCommune, { observe: 'response' });
  }

  update(typeCommune: ITypeCommune): Observable<EntityResponseType> {
    return this.http.put<ITypeCommune>(`${this.resourceUrl}/${getTypeCommuneIdentifier(typeCommune) as number}`, typeCommune, {
      observe: 'response',
    });
  }

  partialUpdate(typeCommune: ITypeCommune): Observable<EntityResponseType> {
    return this.http.patch<ITypeCommune>(`${this.resourceUrl}/${getTypeCommuneIdentifier(typeCommune) as number}`, typeCommune, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITypeCommune>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeCommune[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeCommune[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addTypeCommuneToCollectionIfMissing(
    typeCommuneCollection: ITypeCommune[],
    ...typeCommunesToCheck: (ITypeCommune | null | undefined)[]
  ): ITypeCommune[] {
    const typeCommunes: ITypeCommune[] = typeCommunesToCheck.filter(isPresent);
    if (typeCommunes.length > 0) {
      const typeCommuneCollectionIdentifiers = typeCommuneCollection.map(typeCommuneItem => getTypeCommuneIdentifier(typeCommuneItem)!);
      const typeCommunesToAdd = typeCommunes.filter(typeCommuneItem => {
        const typeCommuneIdentifier = getTypeCommuneIdentifier(typeCommuneItem);
        if (typeCommuneIdentifier == null || typeCommuneCollectionIdentifiers.includes(typeCommuneIdentifier)) {
          return false;
        }
        typeCommuneCollectionIdentifiers.push(typeCommuneIdentifier);
        return true;
      });
      return [...typeCommunesToAdd, ...typeCommuneCollection];
    }
    return typeCommuneCollection;
  }
}
