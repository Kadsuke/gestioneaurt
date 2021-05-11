import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IModeEvacExcreta, getModeEvacExcretaIdentifier } from '../mode-evac-excreta.model';

export type EntityResponseType = HttpResponse<IModeEvacExcreta>;
export type EntityArrayResponseType = HttpResponse<IModeEvacExcreta[]>;

@Injectable({ providedIn: 'root' })
export class ModeEvacExcretaService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/mode-evac-excretas', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/mode-evac-excretas', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(modeEvacExcreta: IModeEvacExcreta): Observable<EntityResponseType> {
    return this.http.post<IModeEvacExcreta>(this.resourceUrl, modeEvacExcreta, { observe: 'response' });
  }

  update(modeEvacExcreta: IModeEvacExcreta): Observable<EntityResponseType> {
    return this.http.put<IModeEvacExcreta>(
      `${this.resourceUrl}/${getModeEvacExcretaIdentifier(modeEvacExcreta) as number}`,
      modeEvacExcreta,
      { observe: 'response' }
    );
  }

  partialUpdate(modeEvacExcreta: IModeEvacExcreta): Observable<EntityResponseType> {
    return this.http.patch<IModeEvacExcreta>(
      `${this.resourceUrl}/${getModeEvacExcretaIdentifier(modeEvacExcreta) as number}`,
      modeEvacExcreta,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IModeEvacExcreta>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModeEvacExcreta[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModeEvacExcreta[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addModeEvacExcretaToCollectionIfMissing(
    modeEvacExcretaCollection: IModeEvacExcreta[],
    ...modeEvacExcretasToCheck: (IModeEvacExcreta | null | undefined)[]
  ): IModeEvacExcreta[] {
    const modeEvacExcretas: IModeEvacExcreta[] = modeEvacExcretasToCheck.filter(isPresent);
    if (modeEvacExcretas.length > 0) {
      const modeEvacExcretaCollectionIdentifiers = modeEvacExcretaCollection.map(
        modeEvacExcretaItem => getModeEvacExcretaIdentifier(modeEvacExcretaItem)!
      );
      const modeEvacExcretasToAdd = modeEvacExcretas.filter(modeEvacExcretaItem => {
        const modeEvacExcretaIdentifier = getModeEvacExcretaIdentifier(modeEvacExcretaItem);
        if (modeEvacExcretaIdentifier == null || modeEvacExcretaCollectionIdentifiers.includes(modeEvacExcretaIdentifier)) {
          return false;
        }
        modeEvacExcretaCollectionIdentifiers.push(modeEvacExcretaIdentifier);
        return true;
      });
      return [...modeEvacExcretasToAdd, ...modeEvacExcretaCollection];
    }
    return modeEvacExcretaCollection;
  }
}
