jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISection, Section } from '../section.model';
import { SectionService } from '../service/section.service';

import { SectionRoutingResolveService } from './section-routing-resolve.service';

describe('Service Tests', () => {
  describe('Section routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SectionRoutingResolveService;
    let service: SectionService;
    let resultSection: ISection | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SectionRoutingResolveService);
      service = TestBed.inject(SectionService);
      resultSection = undefined;
    });

    describe('resolve', () => {
      it('should return ISection returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSection = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSection).toEqual({ id: 123 });
      });

      it('should return new ISection if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSection = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSection).toEqual(new Section());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSection = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSection).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
