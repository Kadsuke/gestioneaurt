import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IModeEvacuationEauUsee, getModeEvacuationEauUseeIdentifier } from '../mode-evacuation-eau-usee.model';

export type EntityResponseType = HttpResponse<IModeEvacuationEauUsee>;
export type EntityArrayResponseType = HttpResponse<IModeEvacuationEauUsee[]>;

@Injectable({ providedIn: 'root' })
export class ModeEvacuationEauUseeService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/mode-evacuation-eau-usees', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/mode-evacuation-eau-usees', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(modeEvacuationEauUsee: IModeEvacuationEauUsee): Observable<EntityResponseType> {
    return this.http.post<IModeEvacuationEauUsee>(this.resourceUrl, modeEvacuationEauUsee, { observe: 'response' });
  }

  update(modeEvacuationEauUsee: IModeEvacuationEauUsee): Observable<EntityResponseType> {
    return this.http.put<IModeEvacuationEauUsee>(
      `${this.resourceUrl}/${getModeEvacuationEauUseeIdentifier(modeEvacuationEauUsee) as number}`,
      modeEvacuationEauUsee,
      { observe: 'response' }
    );
  }

  partialUpdate(modeEvacuationEauUsee: IModeEvacuationEauUsee): Observable<EntityResponseType> {
    return this.http.patch<IModeEvacuationEauUsee>(
      `${this.resourceUrl}/${getModeEvacuationEauUseeIdentifier(modeEvacuationEauUsee) as number}`,
      modeEvacuationEauUsee,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IModeEvacuationEauUsee>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModeEvacuationEauUsee[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IModeEvacuationEauUsee[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addModeEvacuationEauUseeToCollectionIfMissing(
    modeEvacuationEauUseeCollection: IModeEvacuationEauUsee[],
    ...modeEvacuationEauUseesToCheck: (IModeEvacuationEauUsee | null | undefined)[]
  ): IModeEvacuationEauUsee[] {
    const modeEvacuationEauUsees: IModeEvacuationEauUsee[] = modeEvacuationEauUseesToCheck.filter(isPresent);
    if (modeEvacuationEauUsees.length > 0) {
      const modeEvacuationEauUseeCollectionIdentifiers = modeEvacuationEauUseeCollection.map(
        modeEvacuationEauUseeItem => getModeEvacuationEauUseeIdentifier(modeEvacuationEauUseeItem)!
      );
      const modeEvacuationEauUseesToAdd = modeEvacuationEauUsees.filter(modeEvacuationEauUseeItem => {
        const modeEvacuationEauUseeIdentifier = getModeEvacuationEauUseeIdentifier(modeEvacuationEauUseeItem);
        if (
          modeEvacuationEauUseeIdentifier == null ||
          modeEvacuationEauUseeCollectionIdentifiers.includes(modeEvacuationEauUseeIdentifier)
        ) {
          return false;
        }
        modeEvacuationEauUseeCollectionIdentifiers.push(modeEvacuationEauUseeIdentifier);
        return true;
      });
      return [...modeEvacuationEauUseesToAdd, ...modeEvacuationEauUseeCollection];
    }
    return modeEvacuationEauUseeCollection;
  }
}
