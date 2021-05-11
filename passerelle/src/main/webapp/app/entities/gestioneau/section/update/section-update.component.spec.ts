jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SectionService } from '../service/section.service';
import { ISection, Section } from '../section.model';
import { ISecteur } from 'app/entities/gestioneau/secteur/secteur.model';
import { SecteurService } from 'app/entities/gestioneau/secteur/service/secteur.service';

import { SectionUpdateComponent } from './section-update.component';

describe('Component Tests', () => {
  describe('Section Management Update Component', () => {
    let comp: SectionUpdateComponent;
    let fixture: ComponentFixture<SectionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let sectionService: SectionService;
    let secteurService: SecteurService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SectionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SectionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SectionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      sectionService = TestBed.inject(SectionService);
      secteurService = TestBed.inject(SecteurService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Secteur query and add missing value', () => {
        const section: ISection = { id: 456 };
        const secteur: ISecteur = { id: 93518 };
        section.secteur = secteur;

        const secteurCollection: ISecteur[] = [{ id: 88190 }];
        spyOn(secteurService, 'query').and.returnValue(of(new HttpResponse({ body: secteurCollection })));
        const additionalSecteurs = [secteur];
        const expectedCollection: ISecteur[] = [...additionalSecteurs, ...secteurCollection];
        spyOn(secteurService, 'addSecteurToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ section });
        comp.ngOnInit();

        expect(secteurService.query).toHaveBeenCalled();
        expect(secteurService.addSecteurToCollectionIfMissing).toHaveBeenCalledWith(secteurCollection, ...additionalSecteurs);
        expect(comp.secteursSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const section: ISection = { id: 456 };
        const secteur: ISecteur = { id: 87914 };
        section.secteur = secteur;

        activatedRoute.data = of({ section });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(section));
        expect(comp.secteursSharedCollection).toContain(secteur);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = { id: 123 };
        spyOn(sectionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: section }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(sectionService.update).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = new Section();
        spyOn(sectionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: section }));
        saveSubject.complete();

        // THEN
        expect(sectionService.create).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const section = { id: 123 };
        spyOn(sectionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ section });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(sectionService.update).toHaveBeenCalledWith(section);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackSecteurById', () => {
        it('Should return tracked Secteur primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSecteurById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
