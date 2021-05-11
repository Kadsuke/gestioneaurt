jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ModeEvacExcretaService } from '../service/mode-evac-excreta.service';
import { IModeEvacExcreta, ModeEvacExcreta } from '../mode-evac-excreta.model';

import { ModeEvacExcretaUpdateComponent } from './mode-evac-excreta-update.component';

describe('Component Tests', () => {
  describe('ModeEvacExcreta Management Update Component', () => {
    let comp: ModeEvacExcretaUpdateComponent;
    let fixture: ComponentFixture<ModeEvacExcretaUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let modeEvacExcretaService: ModeEvacExcretaService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ModeEvacExcretaUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ModeEvacExcretaUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ModeEvacExcretaUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      modeEvacExcretaService = TestBed.inject(ModeEvacExcretaService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const modeEvacExcreta: IModeEvacExcreta = { id: 456 };

        activatedRoute.data = of({ modeEvacExcreta });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(modeEvacExcreta));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacExcreta = { id: 123 };
        spyOn(modeEvacExcretaService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacExcreta });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: modeEvacExcreta }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(modeEvacExcretaService.update).toHaveBeenCalledWith(modeEvacExcreta);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacExcreta = new ModeEvacExcreta();
        spyOn(modeEvacExcretaService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacExcreta });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: modeEvacExcreta }));
        saveSubject.complete();

        // THEN
        expect(modeEvacExcretaService.create).toHaveBeenCalledWith(modeEvacExcreta);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacExcreta = { id: 123 };
        spyOn(modeEvacExcretaService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacExcreta });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(modeEvacExcretaService.update).toHaveBeenCalledWith(modeEvacExcreta);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
