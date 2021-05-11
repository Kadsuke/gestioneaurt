jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ModeEvacuationEauUseeService } from '../service/mode-evacuation-eau-usee.service';
import { IModeEvacuationEauUsee, ModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';

import { ModeEvacuationEauUseeUpdateComponent } from './mode-evacuation-eau-usee-update.component';

describe('Component Tests', () => {
  describe('ModeEvacuationEauUsee Management Update Component', () => {
    let comp: ModeEvacuationEauUseeUpdateComponent;
    let fixture: ComponentFixture<ModeEvacuationEauUseeUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let modeEvacuationEauUseeService: ModeEvacuationEauUseeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ModeEvacuationEauUseeUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ModeEvacuationEauUseeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ModeEvacuationEauUseeUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      modeEvacuationEauUseeService = TestBed.inject(ModeEvacuationEauUseeService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 456 };

        activatedRoute.data = of({ modeEvacuationEauUsee });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(modeEvacuationEauUsee));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacuationEauUsee = { id: 123 };
        spyOn(modeEvacuationEauUseeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacuationEauUsee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: modeEvacuationEauUsee }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(modeEvacuationEauUseeService.update).toHaveBeenCalledWith(modeEvacuationEauUsee);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacuationEauUsee = new ModeEvacuationEauUsee();
        spyOn(modeEvacuationEauUseeService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacuationEauUsee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: modeEvacuationEauUsee }));
        saveSubject.complete();

        // THEN
        expect(modeEvacuationEauUseeService.create).toHaveBeenCalledWith(modeEvacuationEauUsee);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const modeEvacuationEauUsee = { id: 123 };
        spyOn(modeEvacuationEauUseeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ modeEvacuationEauUsee });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(modeEvacuationEauUseeService.update).toHaveBeenCalledWith(modeEvacuationEauUsee);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
