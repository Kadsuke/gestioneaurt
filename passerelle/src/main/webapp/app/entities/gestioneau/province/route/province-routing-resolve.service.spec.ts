jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IProvince, Province } from '../province.model';
import { ProvinceService } from '../service/province.service';

import { ProvinceRoutingResolveService } from './province-routing-resolve.service';

describe('Service Tests', () => {
  describe('Province routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ProvinceRoutingResolveService;
    let service: ProvinceService;
    let resultProvince: IProvince | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ProvinceRoutingResolveService);
      service = TestBed.inject(ProvinceService);
      resultProvince = undefined;
    });

    describe('resolve', () => {
      it('should return IProvince returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProvince = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultProvince).toEqual({ id: 123 });
      });

      it('should return new IProvince if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProvince = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultProvince).toEqual(new Province());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultProvince = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultProvince).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
