jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CentreRegroupementService } from '../service/centre-regroupement.service';
import { ICentreRegroupement, CentreRegroupement } from '../centre-regroupement.model';
import { IDirectionRegionale } from 'app/entities/gestioneau/direction-regionale/direction-regionale.model';
import { DirectionRegionaleService } from 'app/entities/gestioneau/direction-regionale/service/direction-regionale.service';

import { CentreRegroupementUpdateComponent } from './centre-regroupement-update.component';

describe('Component Tests', () => {
  describe('CentreRegroupement Management Update Component', () => {
    let comp: CentreRegroupementUpdateComponent;
    let fixture: ComponentFixture<CentreRegroupementUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let centreRegroupementService: CentreRegroupementService;
    let directionRegionaleService: DirectionRegionaleService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CentreRegroupementUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CentreRegroupementUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CentreRegroupementUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      centreRegroupementService = TestBed.inject(CentreRegroupementService);
      directionRegionaleService = TestBed.inject(DirectionRegionaleService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call DirectionRegionale query and add missing value', () => {
        const centreRegroupement: ICentreRegroupement = { id: 456 };
        const directionregionale: IDirectionRegionale = { id: 24851 };
        centreRegroupement.directionregionale = directionregionale;

        const directionRegionaleCollection: IDirectionRegionale[] = [{ id: 75618 }];
        spyOn(directionRegionaleService, 'query').and.returnValue(of(new HttpResponse({ body: directionRegionaleCollection })));
        const additionalDirectionRegionales = [directionregionale];
        const expectedCollection: IDirectionRegionale[] = [...additionalDirectionRegionales, ...directionRegionaleCollection];
        spyOn(directionRegionaleService, 'addDirectionRegionaleToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ centreRegroupement });
        comp.ngOnInit();

        expect(directionRegionaleService.query).toHaveBeenCalled();
        expect(directionRegionaleService.addDirectionRegionaleToCollectionIfMissing).toHaveBeenCalledWith(
          directionRegionaleCollection,
          ...additionalDirectionRegionales
        );
        expect(comp.directionRegionalesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const centreRegroupement: ICentreRegroupement = { id: 456 };
        const directionregionale: IDirectionRegionale = { id: 56386 };
        centreRegroupement.directionregionale = directionregionale;

        activatedRoute.data = of({ centreRegroupement });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(centreRegroupement));
        expect(comp.directionRegionalesSharedCollection).toContain(directionregionale);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centreRegroupement = { id: 123 };
        spyOn(centreRegroupementService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centreRegroupement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: centreRegroupement }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(centreRegroupementService.update).toHaveBeenCalledWith(centreRegroupement);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centreRegroupement = new CentreRegroupement();
        spyOn(centreRegroupementService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centreRegroupement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: centreRegroupement }));
        saveSubject.complete();

        // THEN
        expect(centreRegroupementService.create).toHaveBeenCalledWith(centreRegroupement);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centreRegroupement = { id: 123 };
        spyOn(centreRegroupementService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centreRegroupement });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(centreRegroupementService.update).toHaveBeenCalledWith(centreRegroupement);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackDirectionRegionaleById', () => {
        it('Should return tracked DirectionRegionale primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackDirectionRegionaleById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
