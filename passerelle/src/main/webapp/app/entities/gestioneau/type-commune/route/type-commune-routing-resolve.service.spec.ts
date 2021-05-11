jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITypeCommune, TypeCommune } from '../type-commune.model';
import { TypeCommuneService } from '../service/type-commune.service';

import { TypeCommuneRoutingResolveService } from './type-commune-routing-resolve.service';

describe('Service Tests', () => {
  describe('TypeCommune routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TypeCommuneRoutingResolveService;
    let service: TypeCommuneService;
    let resultTypeCommune: ITypeCommune | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TypeCommuneRoutingResolveService);
      service = TestBed.inject(TypeCommuneService);
      resultTypeCommune = undefined;
    });

    describe('resolve', () => {
      it('should return ITypeCommune returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeCommune = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTypeCommune).toEqual({ id: 123 });
      });

      it('should return new ITypeCommune if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeCommune = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTypeCommune).toEqual(new TypeCommune());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeCommune = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTypeCommune).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
