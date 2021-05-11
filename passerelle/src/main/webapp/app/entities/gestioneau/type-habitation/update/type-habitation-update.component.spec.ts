jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TypeHabitationService } from '../service/type-habitation.service';
import { ITypeHabitation, TypeHabitation } from '../type-habitation.model';

import { TypeHabitationUpdateComponent } from './type-habitation-update.component';

describe('Component Tests', () => {
  describe('TypeHabitation Management Update Component', () => {
    let comp: TypeHabitationUpdateComponent;
    let fixture: ComponentFixture<TypeHabitationUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let typeHabitationService: TypeHabitationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TypeHabitationUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TypeHabitationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TypeHabitationUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      typeHabitationService = TestBed.inject(TypeHabitationService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const typeHabitation: ITypeHabitation = { id: 456 };

        activatedRoute.data = of({ typeHabitation });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(typeHabitation));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeHabitation = { id: 123 };
        spyOn(typeHabitationService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeHabitation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typeHabitation }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(typeHabitationService.update).toHaveBeenCalledWith(typeHabitation);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeHabitation = new TypeHabitation();
        spyOn(typeHabitationService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeHabitation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typeHabitation }));
        saveSubject.complete();

        // THEN
        expect(typeHabitationService.create).toHaveBeenCalledWith(typeHabitation);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeHabitation = { id: 123 };
        spyOn(typeHabitationService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeHabitation });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(typeHabitationService.update).toHaveBeenCalledWith(typeHabitation);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
