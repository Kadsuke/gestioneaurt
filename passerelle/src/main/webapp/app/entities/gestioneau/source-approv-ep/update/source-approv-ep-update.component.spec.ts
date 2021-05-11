jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SourceApprovEpService } from '../service/source-approv-ep.service';
import { ISourceApprovEp, SourceApprovEp } from '../source-approv-ep.model';

import { SourceApprovEpUpdateComponent } from './source-approv-ep-update.component';

describe('Component Tests', () => {
  describe('SourceApprovEp Management Update Component', () => {
    let comp: SourceApprovEpUpdateComponent;
    let fixture: ComponentFixture<SourceApprovEpUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let sourceApprovEpService: SourceApprovEpService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SourceApprovEpUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SourceApprovEpUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SourceApprovEpUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      sourceApprovEpService = TestBed.inject(SourceApprovEpService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const sourceApprovEp: ISourceApprovEp = { id: 456 };

        activatedRoute.data = of({ sourceApprovEp });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(sourceApprovEp));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sourceApprovEp = { id: 123 };
        spyOn(sourceApprovEpService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sourceApprovEp });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sourceApprovEp }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(sourceApprovEpService.update).toHaveBeenCalledWith(sourceApprovEp);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sourceApprovEp = new SourceApprovEp();
        spyOn(sourceApprovEpService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sourceApprovEp });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: sourceApprovEp }));
        saveSubject.complete();

        // THEN
        expect(sourceApprovEpService.create).toHaveBeenCalledWith(sourceApprovEp);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const sourceApprovEp = { id: 123 };
        spyOn(sourceApprovEpService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ sourceApprovEp });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(sourceApprovEpService.update).toHaveBeenCalledWith(sourceApprovEp);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
