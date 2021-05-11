jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { NatureOuvrageService } from '../service/nature-ouvrage.service';
import { INatureOuvrage, NatureOuvrage } from '../nature-ouvrage.model';

import { NatureOuvrageUpdateComponent } from './nature-ouvrage-update.component';

describe('Component Tests', () => {
  describe('NatureOuvrage Management Update Component', () => {
    let comp: NatureOuvrageUpdateComponent;
    let fixture: ComponentFixture<NatureOuvrageUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let natureOuvrageService: NatureOuvrageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [NatureOuvrageUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(NatureOuvrageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NatureOuvrageUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      natureOuvrageService = TestBed.inject(NatureOuvrageService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const natureOuvrage: INatureOuvrage = { id: 456 };

        activatedRoute.data = of({ natureOuvrage });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(natureOuvrage));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const natureOuvrage = { id: 123 };
        spyOn(natureOuvrageService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ natureOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: natureOuvrage }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(natureOuvrageService.update).toHaveBeenCalledWith(natureOuvrage);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const natureOuvrage = new NatureOuvrage();
        spyOn(natureOuvrageService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ natureOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: natureOuvrage }));
        saveSubject.complete();

        // THEN
        expect(natureOuvrageService.create).toHaveBeenCalledWith(natureOuvrage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const natureOuvrage = { id: 123 };
        spyOn(natureOuvrageService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ natureOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(natureOuvrageService.update).toHaveBeenCalledWith(natureOuvrage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
