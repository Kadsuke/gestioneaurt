jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICentreRegroupement, CentreRegroupement } from '../centre-regroupement.model';
import { CentreRegroupementService } from '../service/centre-regroupement.service';

import { CentreRegroupementRoutingResolveService } from './centre-regroupement-routing-resolve.service';

describe('Service Tests', () => {
  describe('CentreRegroupement routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CentreRegroupementRoutingResolveService;
    let service: CentreRegroupementService;
    let resultCentreRegroupement: ICentreRegroupement | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CentreRegroupementRoutingResolveService);
      service = TestBed.inject(CentreRegroupementService);
      resultCentreRegroupement = undefined;
    });

    describe('resolve', () => {
      it('should return ICentreRegroupement returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCentreRegroupement = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCentreRegroupement).toEqual({ id: 123 });
      });

      it('should return new ICentreRegroupement if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCentreRegroupement = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCentreRegroupement).toEqual(new CentreRegroupement());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCentreRegroupement = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCentreRegroupement).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
