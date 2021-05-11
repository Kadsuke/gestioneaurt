jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITypeHabitation, TypeHabitation } from '../type-habitation.model';
import { TypeHabitationService } from '../service/type-habitation.service';

import { TypeHabitationRoutingResolveService } from './type-habitation-routing-resolve.service';

describe('Service Tests', () => {
  describe('TypeHabitation routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TypeHabitationRoutingResolveService;
    let service: TypeHabitationService;
    let resultTypeHabitation: ITypeHabitation | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TypeHabitationRoutingResolveService);
      service = TestBed.inject(TypeHabitationService);
      resultTypeHabitation = undefined;
    });

    describe('resolve', () => {
      it('should return ITypeHabitation returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeHabitation = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTypeHabitation).toEqual({ id: 123 });
      });

      it('should return new ITypeHabitation if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeHabitation = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTypeHabitation).toEqual(new TypeHabitation());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTypeHabitation = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTypeHabitation).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
