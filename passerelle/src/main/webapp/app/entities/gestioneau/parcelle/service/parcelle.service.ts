import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IParcelle, getParcelleIdentifier } from '../parcelle.model';

export type EntityResponseType = HttpResponse<IParcelle>;
export type EntityArrayResponseType = HttpResponse<IParcelle[]>;

@Injectable({ providedIn: 'root' })
export class ParcelleService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/parcelles', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/parcelles', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(parcelle: IParcelle): Observable<EntityResponseType> {
    return this.http.post<IParcelle>(this.resourceUrl, parcelle, { observe: 'response' });
  }

  update(parcelle: IParcelle): Observable<EntityResponseType> {
    return this.http.put<IParcelle>(`${this.resourceUrl}/${getParcelleIdentifier(parcelle) as number}`, parcelle, { observe: 'response' });
  }

  partialUpdate(parcelle: IParcelle): Observable<EntityResponseType> {
    return this.http.patch<IParcelle>(`${this.resourceUrl}/${getParcelleIdentifier(parcelle) as number}`, parcelle, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParcelle>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParcelle[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParcelle[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addParcelleToCollectionIfMissing(parcelleCollection: IParcelle[], ...parcellesToCheck: (IParcelle | null | undefined)[]): IParcelle[] {
    const parcelles: IParcelle[] = parcellesToCheck.filter(isPresent);
    if (parcelles.length > 0) {
      const parcelleCollectionIdentifiers = parcelleCollection.map(parcelleItem => getParcelleIdentifier(parcelleItem)!);
      const parcellesToAdd = parcelles.filter(parcelleItem => {
        const parcelleIdentifier = getParcelleIdentifier(parcelleItem);
        if (parcelleIdentifier == null || parcelleCollectionIdentifiers.includes(parcelleIdentifier)) {
          return false;
        }
        parcelleCollectionIdentifiers.push(parcelleIdentifier);
        return true;
      });
      return [...parcellesToAdd, ...parcelleCollection];
    }
    return parcelleCollection;
  }
}
