import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISection, getSectionIdentifier } from '../section.model';

export type EntityResponseType = HttpResponse<ISection>;
export type EntityArrayResponseType = HttpResponse<ISection[]>;

@Injectable({ providedIn: 'root' })
export class SectionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/sections', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/sections', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(section: ISection): Observable<EntityResponseType> {
    return this.http.post<ISection>(this.resourceUrl, section, { observe: 'response' });
  }

  update(section: ISection): Observable<EntityResponseType> {
    return this.http.put<ISection>(`${this.resourceUrl}/${getSectionIdentifier(section) as number}`, section, { observe: 'response' });
  }

  partialUpdate(section: ISection): Observable<EntityResponseType> {
    return this.http.patch<ISection>(`${this.resourceUrl}/${getSectionIdentifier(section) as number}`, section, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISection>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISection[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISection[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addSectionToCollectionIfMissing(sectionCollection: ISection[], ...sectionsToCheck: (ISection | null | undefined)[]): ISection[] {
    const sections: ISection[] = sectionsToCheck.filter(isPresent);
    if (sections.length > 0) {
      const sectionCollectionIdentifiers = sectionCollection.map(sectionItem => getSectionIdentifier(sectionItem)!);
      const sectionsToAdd = sections.filter(sectionItem => {
        const sectionIdentifier = getSectionIdentifier(sectionItem);
        if (sectionIdentifier == null || sectionCollectionIdentifiers.includes(sectionIdentifier)) {
          return false;
        }
        sectionCollectionIdentifiers.push(sectionIdentifier);
        return true;
      });
      return [...sectionsToAdd, ...sectionCollection];
    }
    return sectionCollection;
  }
}
