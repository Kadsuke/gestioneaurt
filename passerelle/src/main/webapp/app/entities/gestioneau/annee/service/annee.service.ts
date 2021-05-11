import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IAnnee, getAnneeIdentifier } from '../annee.model';

export type EntityResponseType = HttpResponse<IAnnee>;
export type EntityArrayResponseType = HttpResponse<IAnnee[]>;

@Injectable({ providedIn: 'root' })
export class AnneeService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/annees', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/annees', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(annee: IAnnee): Observable<EntityResponseType> {
    return this.http.post<IAnnee>(this.resourceUrl, annee, { observe: 'response' });
  }

  update(annee: IAnnee): Observable<EntityResponseType> {
    return this.http.put<IAnnee>(`${this.resourceUrl}/${getAnneeIdentifier(annee) as number}`, annee, { observe: 'response' });
  }

  partialUpdate(annee: IAnnee): Observable<EntityResponseType> {
    return this.http.patch<IAnnee>(`${this.resourceUrl}/${getAnneeIdentifier(annee) as number}`, annee, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAnnee>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAnnee[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAnnee[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addAnneeToCollectionIfMissing(anneeCollection: IAnnee[], ...anneesToCheck: (IAnnee | null | undefined)[]): IAnnee[] {
    const annees: IAnnee[] = anneesToCheck.filter(isPresent);
    if (annees.length > 0) {
      const anneeCollectionIdentifiers = anneeCollection.map(anneeItem => getAnneeIdentifier(anneeItem)!);
      const anneesToAdd = annees.filter(anneeItem => {
        const anneeIdentifier = getAnneeIdentifier(anneeItem);
        if (anneeIdentifier == null || anneeCollectionIdentifiers.includes(anneeIdentifier)) {
          return false;
        }
        anneeCollectionIdentifiers.push(anneeIdentifier);
        return true;
      });
      return [...anneesToAdd, ...anneeCollection];
    }
    return anneeCollection;
  }
}
