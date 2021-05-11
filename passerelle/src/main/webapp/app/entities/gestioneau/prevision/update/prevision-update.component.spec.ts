jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PrevisionService } from '../service/prevision.service';
import { IPrevision, Prevision } from '../prevision.model';
import { ICentre } from 'app/entities/gestioneau/centre/centre.model';
import { CentreService } from 'app/entities/gestioneau/centre/service/centre.service';
import { IAnnee } from 'app/entities/gestioneau/annee/annee.model';
import { AnneeService } from 'app/entities/gestioneau/annee/service/annee.service';

import { PrevisionUpdateComponent } from './prevision-update.component';

describe('Component Tests', () => {
  describe('Prevision Management Update Component', () => {
    let comp: PrevisionUpdateComponent;
    let fixture: ComponentFixture<PrevisionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let previsionService: PrevisionService;
    let centreService: CentreService;
    let anneeService: AnneeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PrevisionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PrevisionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PrevisionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      previsionService = TestBed.inject(PrevisionService);
      centreService = TestBed.inject(CentreService);
      anneeService = TestBed.inject(AnneeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call centre query and add missing value', () => {
        const prevision: IPrevision = { id: 456 };
        const centre: ICentre = { id: 46012 };
        prevision.centre = centre;

        const centreCollection: ICentre[] = [{ id: 68517 }];
        spyOn(centreService, 'query').and.returnValue(of(new HttpResponse({ body: centreCollection })));
        const expectedCollection: ICentre[] = [centre, ...centreCollection];
        spyOn(centreService, 'addCentreToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        expect(centreService.query).toHaveBeenCalled();
        expect(centreService.addCentreToCollectionIfMissing).toHaveBeenCalledWith(centreCollection, centre);
        expect(comp.centresCollection).toEqual(expectedCollection);
      });

      it('Should call refannee query and add missing value', () => {
        const prevision: IPrevision = { id: 456 };
        const refannee: IAnnee = { id: 80939 };
        prevision.refannee = refannee;

        const refanneeCollection: IAnnee[] = [{ id: 690 }];
        spyOn(anneeService, 'query').and.returnValue(of(new HttpResponse({ body: refanneeCollection })));
        const expectedCollection: IAnnee[] = [refannee, ...refanneeCollection];
        spyOn(anneeService, 'addAnneeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        expect(anneeService.query).toHaveBeenCalled();
        expect(anneeService.addAnneeToCollectionIfMissing).toHaveBeenCalledWith(refanneeCollection, refannee);
        expect(comp.refanneesCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const prevision: IPrevision = { id: 456 };
        const centre: ICentre = { id: 44839 };
        prevision.centre = centre;
        const refannee: IAnnee = { id: 71885 };
        prevision.refannee = refannee;

        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(prevision));
        expect(comp.centresCollection).toContain(centre);
        expect(comp.refanneesCollection).toContain(refannee);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prevision = { id: 123 };
        spyOn(previsionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: prevision }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(previsionService.update).toHaveBeenCalledWith(prevision);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prevision = new Prevision();
        spyOn(previsionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: prevision }));
        saveSubject.complete();

        // THEN
        expect(previsionService.create).toHaveBeenCalledWith(prevision);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prevision = { id: 123 };
        spyOn(previsionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prevision });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(previsionService.update).toHaveBeenCalledWith(prevision);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCentreById', () => {
        it('Should return tracked Centre primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCentreById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackAnneeById', () => {
        it('Should return tracked Annee primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAnneeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
