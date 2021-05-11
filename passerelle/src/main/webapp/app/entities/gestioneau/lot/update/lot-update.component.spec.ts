jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LotService } from '../service/lot.service';
import { ILot, Lot } from '../lot.model';
import { ISection } from 'app/entities/gestioneau/section/section.model';
import { SectionService } from 'app/entities/gestioneau/section/service/section.service';

import { LotUpdateComponent } from './lot-update.component';

describe('Component Tests', () => {
  describe('Lot Management Update Component', () => {
    let comp: LotUpdateComponent;
    let fixture: ComponentFixture<LotUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let lotService: LotService;
    let sectionService: SectionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LotUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LotUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LotUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      lotService = TestBed.inject(LotService);
      sectionService = TestBed.inject(SectionService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Section query and add missing value', () => {
        const lot: ILot = { id: 456 };
        const section: ISection = { id: 533 };
        lot.section = section;

        const sectionCollection: ISection[] = [{ id: 37945 }];
        spyOn(sectionService, 'query').and.returnValue(of(new HttpResponse({ body: sectionCollection })));
        const additionalSections = [section];
        const expectedCollection: ISection[] = [...additionalSections, ...sectionCollection];
        spyOn(sectionService, 'addSectionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ lot });
        comp.ngOnInit();

        expect(sectionService.query).toHaveBeenCalled();
        expect(sectionService.addSectionToCollectionIfMissing).toHaveBeenCalledWith(sectionCollection, ...additionalSections);
        expect(comp.sectionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const lot: ILot = { id: 456 };
        const section: ISection = { id: 43149 };
        lot.section = section;

        activatedRoute.data = of({ lot });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(lot));
        expect(comp.sectionsSharedCollection).toContain(section);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const lot = { id: 123 };
        spyOn(lotService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ lot });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: lot }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(lotService.update).toHaveBeenCalledWith(lot);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const lot = new Lot();
        spyOn(lotService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ lot });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: lot }));
        saveSubject.complete();

        // THEN
        expect(lotService.create).toHaveBeenCalledWith(lot);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const lot = { id: 123 };
        spyOn(lotService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ lot });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(lotService.update).toHaveBeenCalledWith(lot);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackSectionById', () => {
        it('Should return tracked Section primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSectionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
