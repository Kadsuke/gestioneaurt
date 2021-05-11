jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IModeEvacuationEauUsee, ModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';
import { ModeEvacuationEauUseeService } from '../service/mode-evacuation-eau-usee.service';

import { ModeEvacuationEauUseeRoutingResolveService } from './mode-evacuation-eau-usee-routing-resolve.service';

describe('Service Tests', () => {
  describe('ModeEvacuationEauUsee routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ModeEvacuationEauUseeRoutingResolveService;
    let service: ModeEvacuationEauUseeService;
    let resultModeEvacuationEauUsee: IModeEvacuationEauUsee | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ModeEvacuationEauUseeRoutingResolveService);
      service = TestBed.inject(ModeEvacuationEauUseeService);
      resultModeEvacuationEauUsee = undefined;
    });

    describe('resolve', () => {
      it('should return IModeEvacuationEauUsee returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacuationEauUsee = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultModeEvacuationEauUsee).toEqual({ id: 123 });
      });

      it('should return new IModeEvacuationEauUsee if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacuationEauUsee = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultModeEvacuationEauUsee).toEqual(new ModeEvacuationEauUsee());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultModeEvacuationEauUsee = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultModeEvacuationEauUsee).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
