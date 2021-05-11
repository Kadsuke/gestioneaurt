jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { SecteurService } from '../service/secteur.service';
import { ISecteur, Secteur } from '../secteur.model';
import { ILocalite } from 'app/entities/gestioneau/localite/localite.model';
import { LocaliteService } from 'app/entities/gestioneau/localite/service/localite.service';

import { SecteurUpdateComponent } from './secteur-update.component';

describe('Component Tests', () => {
  describe('Secteur Management Update Component', () => {
    let comp: SecteurUpdateComponent;
    let fixture: ComponentFixture<SecteurUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let secteurService: SecteurService;
    let localiteService: LocaliteService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SecteurUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(SecteurUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SecteurUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      secteurService = TestBed.inject(SecteurService);
      localiteService = TestBed.inject(LocaliteService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Localite query and add missing value', () => {
        const secteur: ISecteur = { id: 456 };
        const localite: ILocalite = { id: 18253 };
        secteur.localite = localite;

        const localiteCollection: ILocalite[] = [{ id: 98035 }];
        spyOn(localiteService, 'query').and.returnValue(of(new HttpResponse({ body: localiteCollection })));
        const additionalLocalites = [localite];
        const expectedCollection: ILocalite[] = [...additionalLocalites, ...localiteCollection];
        spyOn(localiteService, 'addLocaliteToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ secteur });
        comp.ngOnInit();

        expect(localiteService.query).toHaveBeenCalled();
        expect(localiteService.addLocaliteToCollectionIfMissing).toHaveBeenCalledWith(localiteCollection, ...additionalLocalites);
        expect(comp.localitesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const secteur: ISecteur = { id: 456 };
        const localite: ILocalite = { id: 92367 };
        secteur.localite = localite;

        activatedRoute.data = of({ secteur });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(secteur));
        expect(comp.localitesSharedCollection).toContain(localite);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const secteur = { id: 123 };
        spyOn(secteurService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ secteur });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: secteur }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(secteurService.update).toHaveBeenCalledWith(secteur);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const secteur = new Secteur();
        spyOn(secteurService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ secteur });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: secteur }));
        saveSubject.complete();

        // THEN
        expect(secteurService.create).toHaveBeenCalledWith(secteur);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const secteur = { id: 123 };
        spyOn(secteurService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ secteur });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(secteurService.update).toHaveBeenCalledWith(secteur);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackLocaliteById', () => {
        it('Should return tracked Localite primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLocaliteById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
