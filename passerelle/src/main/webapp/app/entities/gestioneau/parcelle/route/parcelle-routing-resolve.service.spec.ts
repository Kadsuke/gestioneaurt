jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IParcelle, Parcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';

import { ParcelleRoutingResolveService } from './parcelle-routing-resolve.service';

describe('Service Tests', () => {
  describe('Parcelle routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ParcelleRoutingResolveService;
    let service: ParcelleService;
    let resultParcelle: IParcelle | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ParcelleRoutingResolveService);
      service = TestBed.inject(ParcelleService);
      resultParcelle = undefined;
    });

    describe('resolve', () => {
      it('should return IParcelle returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultParcelle = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultParcelle).toEqual({ id: 123 });
      });

      it('should return new IParcelle if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultParcelle = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultParcelle).toEqual(new Parcelle());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultParcelle = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultParcelle).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
