jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IFicheSuiviOuvrage, FicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';
import { FicheSuiviOuvrageService } from '../service/fiche-suivi-ouvrage.service';

import { FicheSuiviOuvrageRoutingResolveService } from './fiche-suivi-ouvrage-routing-resolve.service';

describe('Service Tests', () => {
  describe('FicheSuiviOuvrage routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: FicheSuiviOuvrageRoutingResolveService;
    let service: FicheSuiviOuvrageService;
    let resultFicheSuiviOuvrage: IFicheSuiviOuvrage | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(FicheSuiviOuvrageRoutingResolveService);
      service = TestBed.inject(FicheSuiviOuvrageService);
      resultFicheSuiviOuvrage = undefined;
    });

    describe('resolve', () => {
      it('should return IFicheSuiviOuvrage returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFicheSuiviOuvrage = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFicheSuiviOuvrage).toEqual({ id: 123 });
      });

      it('should return new IFicheSuiviOuvrage if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFicheSuiviOuvrage = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultFicheSuiviOuvrage).toEqual(new FicheSuiviOuvrage());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultFicheSuiviOuvrage = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultFicheSuiviOuvrage).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
