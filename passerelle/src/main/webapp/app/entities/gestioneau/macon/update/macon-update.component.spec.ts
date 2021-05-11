jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { MaconService } from '../service/macon.service';
import { IMacon, Macon } from '../macon.model';

import { MaconUpdateComponent } from './macon-update.component';

describe('Component Tests', () => {
  describe('Macon Management Update Component', () => {
    let comp: MaconUpdateComponent;
    let fixture: ComponentFixture<MaconUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let maconService: MaconService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [MaconUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(MaconUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MaconUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      maconService = TestBed.inject(MaconService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const macon: IMacon = { id: 456 };

        activatedRoute.data = of({ macon });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(macon));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const macon = { id: 123 };
        spyOn(maconService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ macon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: macon }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(maconService.update).toHaveBeenCalledWith(macon);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const macon = new Macon();
        spyOn(maconService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ macon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: macon }));
        saveSubject.complete();

        // THEN
        expect(maconService.create).toHaveBeenCalledWith(macon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const macon = { id: 123 };
        spyOn(maconService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ macon });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(maconService.update).toHaveBeenCalledWith(macon);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
