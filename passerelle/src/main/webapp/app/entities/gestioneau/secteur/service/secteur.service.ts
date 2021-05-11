import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISecteur, getSecteurIdentifier } from '../secteur.model';

export type EntityResponseType = HttpResponse<ISecteur>;
export type EntityArrayResponseType = HttpResponse<ISecteur[]>;

@Injectable({ providedIn: 'root' })
export class SecteurService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/secteurs', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/secteurs', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(secteur: ISecteur): Observable<EntityResponseType> {
    return this.http.post<ISecteur>(this.resourceUrl, secteur, { observe: 'response' });
  }

  update(secteur: ISecteur): Observable<EntityResponseType> {
    return this.http.put<ISecteur>(`${this.resourceUrl}/${getSecteurIdentifier(secteur) as number}`, secteur, { observe: 'response' });
  }

  partialUpdate(secteur: ISecteur): Observable<EntityResponseType> {
    return this.http.patch<ISecteur>(`${this.resourceUrl}/${getSecteurIdentifier(secteur) as number}`, secteur, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISecteur>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecteur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISecteur[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSecteurToCollectionIfMissing(secteurCollection: ISecteur[], ...secteursToCheck: (ISecteur | null | undefined)[]): ISecteur[] {
    const secteurs: ISecteur[] = secteursToCheck.filter(isPresent);
    if (secteurs.length > 0) {
      const secteurCollectionIdentifiers = secteurCollection.map(secteurItem => getSecteurIdentifier(secteurItem)!);
      const secteursToAdd = secteurs.filter(secteurItem => {
        const secteurIdentifier = getSecteurIdentifier(secteurItem);
        if (secteurIdentifier == null || secteurCollectionIdentifiers.includes(secteurIdentifier)) {
          return false;
        }
        secteurCollectionIdentifiers.push(secteurIdentifier);
        return true;
      });
      return [...secteursToAdd, ...secteurCollection];
    }
    return secteurCollection;
  }
}
