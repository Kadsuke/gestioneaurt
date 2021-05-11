jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IDirectionRegionale, DirectionRegionale } from '../direction-regionale.model';
import { DirectionRegionaleService } from '../service/direction-regionale.service';

import { DirectionRegionaleRoutingResolveService } from './direction-regionale-routing-resolve.service';

describe('Service Tests', () => {
  describe('DirectionRegionale routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: DirectionRegionaleRoutingResolveService;
    let service: DirectionRegionaleService;
    let resultDirectionRegionale: IDirectionRegionale | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(DirectionRegionaleRoutingResolveService);
      service = TestBed.inject(DirectionRegionaleService);
      resultDirectionRegionale = undefined;
    });

    describe('resolve', () => {
      it('should return IDirectionRegionale returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDirectionRegionale = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultDirectionRegionale).toEqual({ id: 123 });
      });

      it('should return new IDirectionRegionale if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDirectionRegionale = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultDirectionRegionale).toEqual(new DirectionRegionale());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultDirectionRegionale = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultDirectionRegionale).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
