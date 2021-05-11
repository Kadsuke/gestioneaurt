import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISourceApprovEp, getSourceApprovEpIdentifier } from '../source-approv-ep.model';

export type EntityResponseType = HttpResponse<ISourceApprovEp>;
export type EntityArrayResponseType = HttpResponse<ISourceApprovEp[]>;

@Injectable({ providedIn: 'root' })
export class SourceApprovEpService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/source-approv-eps', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/source-approv-eps', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(sourceApprovEp: ISourceApprovEp): Observable<EntityResponseType> {
    return this.http.post<ISourceApprovEp>(this.resourceUrl, sourceApprovEp, { observe: 'response' });
  }

  update(sourceApprovEp: ISourceApprovEp): Observable<EntityResponseType> {
    return this.http.put<ISourceApprovEp>(`${this.resourceUrl}/${getSourceApprovEpIdentifier(sourceApprovEp) as number}`, sourceApprovEp, {
      observe: 'response',
    });
  }

  partialUpdate(sourceApprovEp: ISourceApprovEp): Observable<EntityResponseType> {
    return this.http.patch<ISourceApprovEp>(
      `${this.resourceUrl}/${getSourceApprovEpIdentifier(sourceApprovEp) as number}`,
      sourceApprovEp,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISourceApprovEp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISourceApprovEp[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISourceApprovEp[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSourceApprovEpToCollectionIfMissing(
    sourceApprovEpCollection: ISourceApprovEp[],
    ...sourceApprovEpsToCheck: (ISourceApprovEp | null | undefined)[]
  ): ISourceApprovEp[] {
    const sourceApprovEps: ISourceApprovEp[] = sourceApprovEpsToCheck.filter(isPresent);
    if (sourceApprovEps.length > 0) {
      const sourceApprovEpCollectionIdentifiers = sourceApprovEpCollection.map(
        sourceApprovEpItem => getSourceApprovEpIdentifier(sourceApprovEpItem)!
      );
      const sourceApprovEpsToAdd = sourceApprovEps.filter(sourceApprovEpItem => {
        const sourceApprovEpIdentifier = getSourceApprovEpIdentifier(sourceApprovEpItem);
        if (sourceApprovEpIdentifier == null || sourceApprovEpCollectionIdentifiers.includes(sourceApprovEpIdentifier)) {
          return false;
        }
        sourceApprovEpCollectionIdentifiers.push(sourceApprovEpIdentifier);
        return true;
      });
      return [...sourceApprovEpsToAdd, ...sourceApprovEpCollection];
    }
    return sourceApprovEpCollection;
  }
}
