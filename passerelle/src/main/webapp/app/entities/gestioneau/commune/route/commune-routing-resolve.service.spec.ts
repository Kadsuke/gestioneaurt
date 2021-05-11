jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICommune, Commune } from '../commune.model';
import { CommuneService } from '../service/commune.service';

import { CommuneRoutingResolveService } from './commune-routing-resolve.service';

describe('Service Tests', () => {
  describe('Commune routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CommuneRoutingResolveService;
    let service: CommuneService;
    let resultCommune: ICommune | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CommuneRoutingResolveService);
      service = TestBed.inject(CommuneService);
      resultCommune = undefined;
    });

    describe('resolve', () => {
      it('should return ICommune returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommune = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCommune).toEqual({ id: 123 });
      });

      it('should return new ICommune if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommune = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCommune).toEqual(new Commune());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommune = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCommune).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
