jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { AnneeService } from '../service/annee.service';
import { IAnnee, Annee } from '../annee.model';

import { AnneeUpdateComponent } from './annee-update.component';

describe('Component Tests', () => {
  describe('Annee Management Update Component', () => {
    let comp: AnneeUpdateComponent;
    let fixture: ComponentFixture<AnneeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let anneeService: AnneeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [AnneeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(AnneeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AnneeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      anneeService = TestBed.inject(AnneeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const annee: IAnnee = { id: 456 };

        activatedRoute.data = of({ annee });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(annee));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const annee = { id: 123 };
        spyOn(anneeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ annee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: annee }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(anneeService.update).toHaveBeenCalledWith(annee);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const annee = new Annee();
        spyOn(anneeService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ annee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: annee }));
        saveSubject.complete();

        // THEN
        expect(anneeService.create).toHaveBeenCalledWith(annee);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const annee = { id: 123 };
        spyOn(anneeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ annee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(anneeService.update).toHaveBeenCalledWith(annee);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
