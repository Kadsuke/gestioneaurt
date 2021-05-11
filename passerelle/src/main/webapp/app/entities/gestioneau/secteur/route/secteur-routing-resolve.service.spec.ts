jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISecteur, Secteur } from '../secteur.model';
import { SecteurService } from '../service/secteur.service';

import { SecteurRoutingResolveService } from './secteur-routing-resolve.service';

describe('Service Tests', () => {
  describe('Secteur routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SecteurRoutingResolveService;
    let service: SecteurService;
    let resultSecteur: ISecteur | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SecteurRoutingResolveService);
      service = TestBed.inject(SecteurService);
      resultSecteur = undefined;
    });

    describe('resolve', () => {
      it('should return ISecteur returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSecteur = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSecteur).toEqual({ id: 123 });
      });

      it('should return new ISecteur if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSecteur = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSecteur).toEqual(new Secteur());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSecteur = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSecteur).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
