jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ILocalite, Localite } from '../localite.model';
import { LocaliteService } from '../service/localite.service';

import { LocaliteRoutingResolveService } from './localite-routing-resolve.service';

describe('Service Tests', () => {
  describe('Localite routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: LocaliteRoutingResolveService;
    let service: LocaliteService;
    let resultLocalite: ILocalite | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(LocaliteRoutingResolveService);
      service = TestBed.inject(LocaliteService);
      resultLocalite = undefined;
    });

    describe('resolve', () => {
      it('should return ILocalite returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLocalite = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultLocalite).toEqual({ id: 123 });
      });

      it('should return new ILocalite if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLocalite = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultLocalite).toEqual(new Localite());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLocalite = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultLocalite).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
