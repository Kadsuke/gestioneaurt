import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IDirectionRegionale, getDirectionRegionaleIdentifier } from '../direction-regionale.model';

export type EntityResponseType = HttpResponse<IDirectionRegionale>;
export type EntityArrayResponseType = HttpResponse<IDirectionRegionale[]>;

@Injectable({ providedIn: 'root' })
export class DirectionRegionaleService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/direction-regionales', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/direction-regionales', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(directionRegionale: IDirectionRegionale): Observable<EntityResponseType> {
    return this.http.post<IDirectionRegionale>(this.resourceUrl, directionRegionale, { observe: 'response' });
  }

  update(directionRegionale: IDirectionRegionale): Observable<EntityResponseType> {
    return this.http.put<IDirectionRegionale>(
      `${this.resourceUrl}/${getDirectionRegionaleIdentifier(directionRegionale) as number}`,
      directionRegionale,
      { observe: 'response' }
    );
  }

  partialUpdate(directionRegionale: IDirectionRegionale): Observable<EntityResponseType> {
    return this.http.patch<IDirectionRegionale>(
      `${this.resourceUrl}/${getDirectionRegionaleIdentifier(directionRegionale) as number}`,
      directionRegionale,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDirectionRegionale>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDirectionRegionale[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDirectionRegionale[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addDirectionRegionaleToCollectionIfMissing(
    directionRegionaleCollection: IDirectionRegionale[],
    ...directionRegionalesToCheck: (IDirectionRegionale | null | undefined)[]
  ): IDirectionRegionale[] {
    const directionRegionales: IDirectionRegionale[] = directionRegionalesToCheck.filter(isPresent);
    if (directionRegionales.length > 0) {
      const directionRegionaleCollectionIdentifiers = directionRegionaleCollection.map(
        directionRegionaleItem => getDirectionRegionaleIdentifier(directionRegionaleItem)!
      );
      const directionRegionalesToAdd = directionRegionales.filter(directionRegionaleItem => {
        const directionRegionaleIdentifier = getDirectionRegionaleIdentifier(directionRegionaleItem);
        if (directionRegionaleIdentifier == null || directionRegionaleCollectionIdentifiers.includes(directionRegionaleIdentifier)) {
          return false;
        }
        directionRegionaleCollectionIdentifiers.push(directionRegionaleIdentifier);
        return true;
      });
      return [...directionRegionalesToAdd, ...directionRegionaleCollection];
    }
    return directionRegionaleCollection;
  }
}
