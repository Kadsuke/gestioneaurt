import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITypeHabitation, getTypeHabitationIdentifier } from '../type-habitation.model';

export type EntityResponseType = HttpResponse<ITypeHabitation>;
export type EntityArrayResponseType = HttpResponse<ITypeHabitation[]>;

@Injectable({ providedIn: 'root' })
export class TypeHabitationService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/type-habitations', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/type-habitations', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(typeHabitation: ITypeHabitation): Observable<EntityResponseType> {
    return this.http.post<ITypeHabitation>(this.resourceUrl, typeHabitation, { observe: 'response' });
  }

  update(typeHabitation: ITypeHabitation): Observable<EntityResponseType> {
    return this.http.put<ITypeHabitation>(`${this.resourceUrl}/${getTypeHabitationIdentifier(typeHabitation) as number}`, typeHabitation, {
      observe: 'response',
    });
  }

  partialUpdate(typeHabitation: ITypeHabitation): Observable<EntityResponseType> {
    return this.http.patch<ITypeHabitation>(
      `${this.resourceUrl}/${getTypeHabitationIdentifier(typeHabitation) as number}`,
      typeHabitation,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITypeHabitation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeHabitation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITypeHabitation[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addTypeHabitationToCollectionIfMissing(
    typeHabitationCollection: ITypeHabitation[],
    ...typeHabitationsToCheck: (ITypeHabitation | null | undefined)[]
  ): ITypeHabitation[] {
    const typeHabitations: ITypeHabitation[] = typeHabitationsToCheck.filter(isPresent);
    if (typeHabitations.length > 0) {
      const typeHabitationCollectionIdentifiers = typeHabitationCollection.map(
        typeHabitationItem => getTypeHabitationIdentifier(typeHabitationItem)!
      );
      const typeHabitationsToAdd = typeHabitations.filter(typeHabitationItem => {
        const typeHabitationIdentifier = getTypeHabitationIdentifier(typeHabitationItem);
        if (typeHabitationIdentifier == null || typeHabitationCollectionIdentifiers.includes(typeHabitationIdentifier)) {
          return false;
        }
        typeHabitationCollectionIdentifiers.push(typeHabitationIdentifier);
        return true;
      });
      return [...typeHabitationsToAdd, ...typeHabitationCollection];
    }
    return typeHabitationCollection;
  }
}
