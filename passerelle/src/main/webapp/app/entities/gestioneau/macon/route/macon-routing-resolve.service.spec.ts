jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IMacon, Macon } from '../macon.model';
import { MaconService } from '../service/macon.service';

import { MaconRoutingResolveService } from './macon-routing-resolve.service';

describe('Service Tests', () => {
  describe('Macon routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: MaconRoutingResolveService;
    let service: MaconService;
    let resultMacon: IMacon | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(MaconRoutingResolveService);
      service = TestBed.inject(MaconService);
      resultMacon = undefined;
    });

    describe('resolve', () => {
      it('should return IMacon returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMacon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMacon).toEqual({ id: 123 });
      });

      it('should return new IMacon if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMacon = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultMacon).toEqual(new Macon());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultMacon = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultMacon).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
