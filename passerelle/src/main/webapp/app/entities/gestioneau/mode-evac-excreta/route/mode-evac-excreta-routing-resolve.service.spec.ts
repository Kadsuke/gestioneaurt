jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IModeEvacExcreta, ModeEvacExcreta } from '../mode-evac-excreta.model';
import { ModeEvacExcretaService } from '../service/mode-evac-excreta.service';

import { ModeEvacExcretaRoutingResolveService } from './mode-evac-excreta-routing-resolve.service';

describe('Service Tests', () => {
  describe('ModeEvacExcreta routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ModeEvacExcretaRoutingResolveService;
    let service: ModeEvacExcretaService;
    let resultModeEvacExcreta: IModeEvacExcreta | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ModeEvacExcretaRoutingResolveService);
      service = TestBed.inject(ModeEvacExcretaService);
      resultModeEvacExcreta = undefined;
    });

    describe('resolve', () => {
      it('should return IModeEvacExcreta returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacExcreta = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultModeEvacExcreta).toEqual({ id: 123 });
      });

      it('should return new IModeEvacExcreta if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacExcreta = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultModeEvacExcreta).toEqual(new ModeEvacExcreta());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacExcreta = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultModeEvacExcreta).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
