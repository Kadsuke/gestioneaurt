jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TypeCommuneService } from '../service/type-commune.service';
import { ITypeCommune, TypeCommune } from '../type-commune.model';

import { TypeCommuneUpdateComponent } from './type-commune-update.component';

describe('Component Tests', () => {
  describe('TypeCommune Management Update Component', () => {
    let comp: TypeCommuneUpdateComponent;
    let fixture: ComponentFixture<TypeCommuneUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let typeCommuneService: TypeCommuneService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TypeCommuneUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TypeCommuneUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TypeCommuneUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      typeCommuneService = TestBed.inject(TypeCommuneService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const typeCommune: ITypeCommune = { id: 456 };

        activatedRoute.data = of({ typeCommune });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(typeCommune));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeCommune = { id: 123 };
        spyOn(typeCommuneService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeCommune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typeCommune }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(typeCommuneService.update).toHaveBeenCalledWith(typeCommune);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeCommune = new TypeCommune();
        spyOn(typeCommuneService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeCommune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: typeCommune }));
        saveSubject.complete();

        // THEN
        expect(typeCommuneService.create).toHaveBeenCalledWith(typeCommune);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const typeCommune = { id: 123 };
        spyOn(typeCommuneService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ typeCommune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(typeCommuneService.update).toHaveBeenCalledWith(typeCommune);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
