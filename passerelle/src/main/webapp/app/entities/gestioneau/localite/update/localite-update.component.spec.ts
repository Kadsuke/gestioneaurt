jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LocaliteService } from '../service/localite.service';
import { ILocalite, Localite } from '../localite.model';
import { ICommune } from 'app/entities/gestioneau/commune/commune.model';
import { CommuneService } from 'app/entities/gestioneau/commune/service/commune.service';

import { LocaliteUpdateComponent } from './localite-update.component';

describe('Component Tests', () => {
  describe('Localite Management Update Component', () => {
    let comp: LocaliteUpdateComponent;
    let fixture: ComponentFixture<LocaliteUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let localiteService: LocaliteService;
    let communeService: CommuneService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LocaliteUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LocaliteUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LocaliteUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      localiteService = TestBed.inject(LocaliteService);
      communeService = TestBed.inject(CommuneService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Commune query and add missing value', () => {
        const localite: ILocalite = { id: 456 };
        const commune: ICommune = { id: 65812 };
        localite.commune = commune;

        const communeCollection: ICommune[] = [{ id: 36302 }];
        spyOn(communeService, 'query').and.returnValue(of(new HttpResponse({ body: communeCollection })));
        const additionalCommunes = [commune];
        const expectedCollection: ICommune[] = [...additionalCommunes, ...communeCollection];
        spyOn(communeService, 'addCommuneToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ localite });
        comp.ngOnInit();

        expect(communeService.query).toHaveBeenCalled();
        expect(communeService.addCommuneToCollectionIfMissing).toHaveBeenCalledWith(communeCollection, ...additionalCommunes);
        expect(comp.communesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const localite: ILocalite = { id: 456 };
        const commune: ICommune = { id: 97760 };
        localite.commune = commune;

        activatedRoute.data = of({ localite });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(localite));
        expect(comp.communesSharedCollection).toContain(commune);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const localite = { id: 123 };
        spyOn(localiteService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ localite });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: localite }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(localiteService.update).toHaveBeenCalledWith(localite);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const localite = new Localite();
        spyOn(localiteService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ localite });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: localite }));
        saveSubject.complete();

        // THEN
        expect(localiteService.create).toHaveBeenCalledWith(localite);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const localite = { id: 123 };
        spyOn(localiteService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ localite });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(localiteService.update).toHaveBeenCalledWith(localite);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCommuneById', () => {
        it('Should return tracked Commune primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCommuneById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
