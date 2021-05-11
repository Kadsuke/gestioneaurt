import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IFicheSuiviOuvrage, getFicheSuiviOuvrageIdentifier } from '../fiche-suivi-ouvrage.model';

export type EntityResponseType = HttpResponse<IFicheSuiviOuvrage>;
export type EntityArrayResponseType = HttpResponse<IFicheSuiviOuvrage[]>;

@Injectable({ providedIn: 'root' })
export class FicheSuiviOuvrageService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/fiche-suivi-ouvrages', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/fiche-suivi-ouvrages', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(ficheSuiviOuvrage: IFicheSuiviOuvrage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ficheSuiviOuvrage);
    return this.http
      .post<IFicheSuiviOuvrage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(ficheSuiviOuvrage: IFicheSuiviOuvrage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ficheSuiviOuvrage);
    return this.http
      .put<IFicheSuiviOuvrage>(`${this.resourceUrl}/${getFicheSuiviOuvrageIdentifier(ficheSuiviOuvrage) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(ficheSuiviOuvrage: IFicheSuiviOuvrage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ficheSuiviOuvrage);
    return this.http
      .patch<IFicheSuiviOuvrage>(`${this.resourceUrl}/${getFicheSuiviOuvrageIdentifier(ficheSuiviOuvrage) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IFicheSuiviOuvrage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFicheSuiviOuvrage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFicheSuiviOuvrage[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  addFicheSuiviOuvrageToCollectionIfMissing(
    ficheSuiviOuvrageCollection: IFicheSuiviOuvrage[],
    ...ficheSuiviOuvragesToCheck: (IFicheSuiviOuvrage | null | undefined)[]
  ): IFicheSuiviOuvrage[] {
    const ficheSuiviOuvrages: IFicheSuiviOuvrage[] = ficheSuiviOuvragesToCheck.filter(isPresent);
    if (ficheSuiviOuvrages.length > 0) {
      const ficheSuiviOuvrageCollectionIdentifiers = ficheSuiviOuvrageCollection.map(
        ficheSuiviOuvrageItem => getFicheSuiviOuvrageIdentifier(ficheSuiviOuvrageItem)!
      );
      const ficheSuiviOuvragesToAdd = ficheSuiviOuvrages.filter(ficheSuiviOuvrageItem => {
        const ficheSuiviOuvrageIdentifier = getFicheSuiviOuvrageIdentifier(ficheSuiviOuvrageItem);
        if (ficheSuiviOuvrageIdentifier == null || ficheSuiviOuvrageCollectionIdentifiers.includes(ficheSuiviOuvrageIdentifier)) {
          return false;
        }
        ficheSuiviOuvrageCollectionIdentifiers.push(ficheSuiviOuvrageIdentifier);
        return true;
      });
      return [...ficheSuiviOuvragesToAdd, ...ficheSuiviOuvrageCollection];
    }
    return ficheSuiviOuvrageCollection;
  }

  protected convertDateFromClient(ficheSuiviOuvrage: IFicheSuiviOuvrage): IFicheSuiviOuvrage {
    return Object.assign({}, ficheSuiviOuvrage, {
      dateRemiseDevis: ficheSuiviOuvrage.dateRemiseDevis?.isValid() ? ficheSuiviOuvrage.dateRemiseDevis.toJSON() : undefined,
      dateDebutTravaux: ficheSuiviOuvrage.dateDebutTravaux?.isValid() ? ficheSuiviOuvrage.dateDebutTravaux.toJSON() : undefined,
      dateFinTravaux: ficheSuiviOuvrage.dateFinTravaux?.isValid() ? ficheSuiviOuvrage.dateFinTravaux.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateRemiseDevis = res.body.dateRemiseDevis ? dayjs(res.body.dateRemiseDevis) : undefined;
      res.body.dateDebutTravaux = res.body.dateDebutTravaux ? dayjs(res.body.dateDebutTravaux) : undefined;
      res.body.dateFinTravaux = res.body.dateFinTravaux ? dayjs(res.body.dateFinTravaux) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((ficheSuiviOuvrage: IFicheSuiviOuvrage) => {
        ficheSuiviOuvrage.dateRemiseDevis = ficheSuiviOuvrage.dateRemiseDevis ? dayjs(ficheSuiviOuvrage.dateRemiseDevis) : undefined;
        ficheSuiviOuvrage.dateDebutTravaux = ficheSuiviOuvrage.dateDebutTravaux ? dayjs(ficheSuiviOuvrage.dateDebutTravaux) : undefined;
        ficheSuiviOuvrage.dateFinTravaux = ficheSuiviOuvrage.dateFinTravaux ? dayjs(ficheSuiviOuvrage.dateFinTravaux) : undefined;
      });
    }
    return res;
  }
}
