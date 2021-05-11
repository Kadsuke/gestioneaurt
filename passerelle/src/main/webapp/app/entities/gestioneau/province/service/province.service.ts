import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IProvince, getProvinceIdentifier } from '../province.model';

export type EntityResponseType = HttpResponse<IProvince>;
export type EntityArrayResponseType = HttpResponse<IProvince[]>;

@Injectable({ providedIn: 'root' })
export class ProvinceService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/provinces', 'gestioneau');
  public resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/provinces', 'gestioneau');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(province: IProvince): Observable<EntityResponseType> {
    return this.http.post<IProvince>(this.resourceUrl, province, { observe: 'response' });
  }

  update(province: IProvince): Observable<EntityResponseType> {
    return this.http.put<IProvince>(`${this.resourceUrl}/${getProvinceIdentifier(province) as number}`, province, { observe: 'response' });
  }

  partialUpdate(province: IProvince): Observable<EntityResponseType> {
    return this.http.patch<IProvince>(`${this.resourceUrl}/${getProvinceIdentifier(province) as number}`, province, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProvince>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProvince[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProvince[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  addProvinceToCollectionIfMissing(provinceCollection: IProvince[], ...provincesToCheck: (IProvince | null | undefined)[]): IProvince[] {
    const provinces: IProvince[] = provincesToCheck.filter(isPresent);
    if (provinces.length > 0) {
      const provinceCollectionIdentifiers = provinceCollection.map(provinceItem => getProvinceIdentifier(provinceItem)!);
      const provincesToAdd = provinces.filter(provinceItem => {
        const provinceIdentifier = getProvinceIdentifier(provinceItem);
        if (provinceIdentifier == null || provinceCollectionIdentifiers.includes(provinceIdentifier)) {
          return false;
        }
        provinceCollectionIdentifiers.push(provinceIdentifier);
        return true;
      });
      return [...provincesToAdd, ...provinceCollection];
    }
    return provinceCollection;
  }
}
