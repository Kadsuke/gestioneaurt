jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IAnnee, Annee } from '../annee.model';
import { AnneeService } from '../service/annee.service';

import { AnneeRoutingResolveService } from './annee-routing-resolve.service';

describe('Service Tests', () => {
  describe('Annee routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: AnneeRoutingResolveService;
    let service: AnneeService;
    let resultAnnee: IAnnee | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(AnneeRoutingResolveService);
      service = TestBed.inject(AnneeService);
      resultAnnee = undefined;
    });

    describe('resolve', () => {
      it('should return IAnnee returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAnnee = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAnnee).toEqual({ id: 123 });
      });

      it('should return new IAnnee if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAnnee = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultAnnee).toEqual(new Annee());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultAnnee = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultAnnee).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
