jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CentreService } from '../service/centre.service';
import { ICentre, Centre } from '../centre.model';
import { ICentreRegroupement } from 'app/entities/gestioneau/centre-regroupement/centre-regroupement.model';
import { CentreRegroupementService } from 'app/entities/gestioneau/centre-regroupement/service/centre-regroupement.service';

import { CentreUpdateComponent } from './centre-update.component';

describe('Component Tests', () => {
  describe('Centre Management Update Component', () => {
    let comp: CentreUpdateComponent;
    let fixture: ComponentFixture<CentreUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let centreService: CentreService;
    let centreRegroupementService: CentreRegroupementService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CentreUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CentreUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CentreUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      centreService = TestBed.inject(CentreService);
      centreRegroupementService = TestBed.inject(CentreRegroupementService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call CentreRegroupement query and add missing value', () => {
        const centre: ICentre = { id: 456 };
        const centreregroupement: ICentreRegroupement = { id: 64308 };
        centre.centreregroupement = centreregroupement;

        const centreRegroupementCollection: ICentreRegroupement[] = [{ id: 43737 }];
        spyOn(centreRegroupementService, 'query').and.returnValue(of(new HttpResponse({ body: centreRegroupementCollection })));
        const additionalCentreRegroupements = [centreregroupement];
        const expectedCollection: ICentreRegroupement[] = [...additionalCentreRegroupements, ...centreRegroupementCollection];
        spyOn(centreRegroupementService, 'addCentreRegroupementToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ centre });
        comp.ngOnInit();

        expect(centreRegroupementService.query).toHaveBeenCalled();
        expect(centreRegroupementService.addCentreRegroupementToCollectionIfMissing).toHaveBeenCalledWith(
          centreRegroupementCollection,
          ...additionalCentreRegroupements
        );
        expect(comp.centreRegroupementsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const centre: ICentre = { id: 456 };
        const centreregroupement: ICentreRegroupement = { id: 86320 };
        centre.centreregroupement = centreregroupement;

        activatedRoute.data = of({ centre });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(centre));
        expect(comp.centreRegroupementsSharedCollection).toContain(centreregroupement);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centre = { id: 123 };
        spyOn(centreService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centre });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: centre }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(centreService.update).toHaveBeenCalledWith(centre);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centre = new Centre();
        spyOn(centreService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centre });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: centre }));
        saveSubject.complete();

        // THEN
        expect(centreService.create).toHaveBeenCalledWith(centre);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const centre = { id: 123 };
        spyOn(centreService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ centre });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(centreService.update).toHaveBeenCalledWith(centre);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCentreRegroupementById', () => {
        it('Should return tracked CentreRegroupement primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCentreRegroupementById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
