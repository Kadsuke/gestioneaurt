jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PrefabricantService } from '../service/prefabricant.service';
import { IPrefabricant, Prefabricant } from '../prefabricant.model';

import { PrefabricantUpdateComponent } from './prefabricant-update.component';

describe('Component Tests', () => {
  describe('Prefabricant Management Update Component', () => {
    let comp: PrefabricantUpdateComponent;
    let fixture: ComponentFixture<PrefabricantUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let prefabricantService: PrefabricantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PrefabricantUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PrefabricantUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PrefabricantUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      prefabricantService = TestBed.inject(PrefabricantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const prefabricant: IPrefabricant = { id: 456 };

        activatedRoute.data = of({ prefabricant });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(prefabricant));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prefabricant = { id: 123 };
        spyOn(prefabricantService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prefabricant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: prefabricant }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(prefabricantService.update).toHaveBeenCalledWith(prefabricant);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prefabricant = new Prefabricant();
        spyOn(prefabricantService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prefabricant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: prefabricant }));
        saveSubject.complete();

        // THEN
        expect(prefabricantService.create).toHaveBeenCalledWith(prefabricant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const prefabricant = { id: 123 };
        spyOn(prefabricantService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ prefabricant });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(prefabricantService.update).toHaveBeenCalledWith(prefabricant);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
