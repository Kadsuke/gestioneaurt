import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IMacon, getMaconIdentifier } from '../macon.model';

export type EntityResponseType = HttpResponse<IMacon>;
export type EntityArrayResponseType = HttpResponse<IMacon[]>;

@Injectable({ providedIn: 'root' })
export class MaconService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/macons', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/macons', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(macon: IMacon): Observable<EntityResponseType> {
    return this.http.post<IMacon>(this.resourceUrl, macon, { observe: 'response' });
  }

  update(macon: IMacon): Observable<EntityResponseType> {
    return this.http.put<IMacon>(`${this.resourceUrl}/${getMaconIdentifier(macon) as number}`, macon, { observe: 'response' });
  }

  partialUpdate(macon: IMacon): Observable<EntityResponseType> {
    return this.http.patch<IMacon>(`${this.resourceUrl}/${getMaconIdentifier(macon) as number}`, macon, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMacon>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMacon[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMacon[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addMaconToCollectionIfMissing(maconCollection: IMacon[], ...maconsToCheck: (IMacon | null | undefined)[]): IMacon[] {
    const macons: IMacon[] = maconsToCheck.filter(isPresent);
    if (macons.length > 0) {
      const maconCollectionIdentifiers = maconCollection.map(maconItem => getMaconIdentifier(maconItem)!);
      const maconsToAdd = macons.filter(maconItem => {
        const maconIdentifier = getMaconIdentifier(maconItem);
        if (maconIdentifier == null || maconCollectionIdentifiers.includes(maconIdentifier)) {
          return false;
        }
        maconCollectionIdentifiers.push(maconIdentifier);
        return true;
      });
      return [...maconsToAdd, ...maconCollection];
    }
    return maconCollection;
  }
}
