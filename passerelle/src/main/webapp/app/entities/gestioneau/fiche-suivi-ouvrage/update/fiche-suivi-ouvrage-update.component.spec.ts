jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { FicheSuiviOuvrageService } from '../service/fiche-suivi-ouvrage.service';
import { IFicheSuiviOuvrage, FicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';
import { IParcelle } from 'app/entities/gestioneau/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/gestioneau/parcelle/service/parcelle.service';
import { IPrevision } from 'app/entities/gestioneau/prevision/prevision.model';
import { PrevisionService } from 'app/entities/gestioneau/prevision/service/prevision.service';
import { INatureOuvrage } from 'app/entities/gestioneau/nature-ouvrage/nature-ouvrage.model';
import { NatureOuvrageService } from 'app/entities/gestioneau/nature-ouvrage/service/nature-ouvrage.service';
import { ITypeHabitation } from 'app/entities/gestioneau/type-habitation/type-habitation.model';
import { TypeHabitationService } from 'app/entities/gestioneau/type-habitation/service/type-habitation.service';
import { ISourceApprovEp } from 'app/entities/gestioneau/source-approv-ep/source-approv-ep.model';
import { SourceApprovEpService } from 'app/entities/gestioneau/source-approv-ep/service/source-approv-ep.service';
import { IModeEvacuationEauUsee } from 'app/entities/gestioneau/mode-evacuation-eau-usee/mode-evacuation-eau-usee.model';
import { ModeEvacuationEauUseeService } from 'app/entities/gestioneau/mode-evacuation-eau-usee/service/mode-evacuation-eau-usee.service';
import { IModeEvacExcreta } from 'app/entities/gestioneau/mode-evac-excreta/mode-evac-excreta.model';
import { ModeEvacExcretaService } from 'app/entities/gestioneau/mode-evac-excreta/service/mode-evac-excreta.service';
import { IMacon } from 'app/entities/gestioneau/macon/macon.model';
import { MaconService } from 'app/entities/gestioneau/macon/service/macon.service';
import { IPrefabricant } from 'app/entities/gestioneau/prefabricant/prefabricant.model';
import { PrefabricantService } from 'app/entities/gestioneau/prefabricant/service/prefabricant.service';

import { FicheSuiviOuvrageUpdateComponent } from './fiche-suivi-ouvrage-update.component';

describe('Component Tests', () => {
  describe('FicheSuiviOuvrage Management Update Component', () => {
    let comp: FicheSuiviOuvrageUpdateComponent;
    let fixture: ComponentFixture<FicheSuiviOuvrageUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let ficheSuiviOuvrageService: FicheSuiviOuvrageService;
    let parcelleService: ParcelleService;
    let previsionService: PrevisionService;
    let natureOuvrageService: NatureOuvrageService;
    let typeHabitationService: TypeHabitationService;
    let sourceApprovEpService: SourceApprovEpService;
    let modeEvacuationEauUseeService: ModeEvacuationEauUseeService;
    let modeEvacExcretaService: ModeEvacExcretaService;
    let maconService: MaconService;
    let prefabricantService: PrefabricantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [FicheSuiviOuvrageUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(FicheSuiviOuvrageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(FicheSuiviOuvrageUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      ficheSuiviOuvrageService = TestBed.inject(FicheSuiviOuvrageService);
      parcelleService = TestBed.inject(ParcelleService);
      previsionService = TestBed.inject(PrevisionService);
      natureOuvrageService = TestBed.inject(NatureOuvrageService);
      typeHabitationService = TestBed.inject(TypeHabitationService);
      sourceApprovEpService = TestBed.inject(SourceApprovEpService);
      modeEvacuationEauUseeService = TestBed.inject(ModeEvacuationEauUseeService);
      modeEvacExcretaService = TestBed.inject(ModeEvacExcretaService);
      maconService = TestBed.inject(MaconService);
      prefabricantService = TestBed.inject(PrefabricantService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Parcelle query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const parcelle: IParcelle = { id: 6918 };
        ficheSuiviOuvrage.parcelle = parcelle;

        const parcelleCollection: IParcelle[] = [{ id: 61727 }];
        spyOn(parcelleService, 'query').and.returnValue(of(new HttpResponse({ body: parcelleCollection })));
        const additionalParcelles = [parcelle];
        const expectedCollection: IParcelle[] = [...additionalParcelles, ...parcelleCollection];
        spyOn(parcelleService, 'addParcelleToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(parcelleService.query).toHaveBeenCalled();
        expect(parcelleService.addParcelleToCollectionIfMissing).toHaveBeenCalledWith(parcelleCollection, ...additionalParcelles);
        expect(comp.parcellesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Prevision query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const prevision: IPrevision = { id: 35487 };
        ficheSuiviOuvrage.prevision = prevision;

        const previsionCollection: IPrevision[] = [{ id: 42111 }];
        spyOn(previsionService, 'query').and.returnValue(of(new HttpResponse({ body: previsionCollection })));
        const additionalPrevisions = [prevision];
        const expectedCollection: IPrevision[] = [...additionalPrevisions, ...previsionCollection];
        spyOn(previsionService, 'addPrevisionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(previsionService.query).toHaveBeenCalled();
        expect(previsionService.addPrevisionToCollectionIfMissing).toHaveBeenCalledWith(previsionCollection, ...additionalPrevisions);
        expect(comp.previsionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call NatureOuvrage query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const natureouvrage: INatureOuvrage = { id: 23215 };
        ficheSuiviOuvrage.natureouvrage = natureouvrage;

        const natureOuvrageCollection: INatureOuvrage[] = [{ id: 69697 }];
        spyOn(natureOuvrageService, 'query').and.returnValue(of(new HttpResponse({ body: natureOuvrageCollection })));
        const additionalNatureOuvrages = [natureouvrage];
        const expectedCollection: INatureOuvrage[] = [...additionalNatureOuvrages, ...natureOuvrageCollection];
        spyOn(natureOuvrageService, 'addNatureOuvrageToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(natureOuvrageService.query).toHaveBeenCalled();
        expect(natureOuvrageService.addNatureOuvrageToCollectionIfMissing).toHaveBeenCalledWith(
          natureOuvrageCollection,
          ...additionalNatureOuvrages
        );
        expect(comp.natureOuvragesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TypeHabitation query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const typehabitation: ITypeHabitation = { id: 61772 };
        ficheSuiviOuvrage.typehabitation = typehabitation;

        const typeHabitationCollection: ITypeHabitation[] = [{ id: 70554 }];
        spyOn(typeHabitationService, 'query').and.returnValue(of(new HttpResponse({ body: typeHabitationCollection })));
        const additionalTypeHabitations = [typehabitation];
        const expectedCollection: ITypeHabitation[] = [...additionalTypeHabitations, ...typeHabitationCollection];
        spyOn(typeHabitationService, 'addTypeHabitationToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(typeHabitationService.query).toHaveBeenCalled();
        expect(typeHabitationService.addTypeHabitationToCollectionIfMissing).toHaveBeenCalledWith(
          typeHabitationCollection,
          ...additionalTypeHabitations
        );
        expect(comp.typeHabitationsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call SourceApprovEp query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const sourceapprovep: ISourceApprovEp = { id: 59823 };
        ficheSuiviOuvrage.sourceapprovep = sourceapprovep;

        const sourceApprovEpCollection: ISourceApprovEp[] = [{ id: 8621 }];
        spyOn(sourceApprovEpService, 'query').and.returnValue(of(new HttpResponse({ body: sourceApprovEpCollection })));
        const additionalSourceApprovEps = [sourceapprovep];
        const expectedCollection: ISourceApprovEp[] = [...additionalSourceApprovEps, ...sourceApprovEpCollection];
        spyOn(sourceApprovEpService, 'addSourceApprovEpToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(sourceApprovEpService.query).toHaveBeenCalled();
        expect(sourceApprovEpService.addSourceApprovEpToCollectionIfMissing).toHaveBeenCalledWith(
          sourceApprovEpCollection,
          ...additionalSourceApprovEps
        );
        expect(comp.sourceApprovEpsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call ModeEvacuationEauUsee query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const modeevacuationeauusee: IModeEvacuationEauUsee = { id: 44724 };
        ficheSuiviOuvrage.modeevacuationeauusee = modeevacuationeauusee;

        const modeEvacuationEauUseeCollection: IModeEvacuationEauUsee[] = [{ id: 29631 }];
        spyOn(modeEvacuationEauUseeService, 'query').and.returnValue(of(new HttpResponse({ body: modeEvacuationEauUseeCollection })));
        const additionalModeEvacuationEauUsees = [modeevacuationeauusee];
        const expectedCollection: IModeEvacuationEauUsee[] = [...additionalModeEvacuationEauUsees, ...modeEvacuationEauUseeCollection];
        spyOn(modeEvacuationEauUseeService, 'addModeEvacuationEauUseeToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(modeEvacuationEauUseeService.query).toHaveBeenCalled();
        expect(modeEvacuationEauUseeService.addModeEvacuationEauUseeToCollectionIfMissing).toHaveBeenCalledWith(
          modeEvacuationEauUseeCollection,
          ...additionalModeEvacuationEauUsees
        );
        expect(comp.modeEvacuationEauUseesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call ModeEvacExcreta query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const modeevacexcreta: IModeEvacExcreta = { id: 50287 };
        ficheSuiviOuvrage.modeevacexcreta = modeevacexcreta;

        const modeEvacExcretaCollection: IModeEvacExcreta[] = [{ id: 957 }];
        spyOn(modeEvacExcretaService, 'query').and.returnValue(of(new HttpResponse({ body: modeEvacExcretaCollection })));
        const additionalModeEvacExcretas = [modeevacexcreta];
        const expectedCollection: IModeEvacExcreta[] = [...additionalModeEvacExcretas, ...modeEvacExcretaCollection];
        spyOn(modeEvacExcretaService, 'addModeEvacExcretaToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(modeEvacExcretaService.query).toHaveBeenCalled();
        expect(modeEvacExcretaService.addModeEvacExcretaToCollectionIfMissing).toHaveBeenCalledWith(
          modeEvacExcretaCollection,
          ...additionalModeEvacExcretas
        );
        expect(comp.modeEvacExcretasSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Macon query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const macon: IMacon = { id: 83548 };
        ficheSuiviOuvrage.macon = macon;

        const maconCollection: IMacon[] = [{ id: 38871 }];
        spyOn(maconService, 'query').and.returnValue(of(new HttpResponse({ body: maconCollection })));
        const additionalMacons = [macon];
        const expectedCollection: IMacon[] = [...additionalMacons, ...maconCollection];
        spyOn(maconService, 'addMaconToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(maconService.query).toHaveBeenCalled();
        expect(maconService.addMaconToCollectionIfMissing).toHaveBeenCalledWith(maconCollection, ...additionalMacons);
        expect(comp.maconsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Prefabricant query and add missing value', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const prefabricant: IPrefabricant = { id: 81945 };
        ficheSuiviOuvrage.prefabricant = prefabricant;

        const prefabricantCollection: IPrefabricant[] = [{ id: 388 }];
        spyOn(prefabricantService, 'query').and.returnValue(of(new HttpResponse({ body: prefabricantCollection })));
        const additionalPrefabricants = [prefabricant];
        const expectedCollection: IPrefabricant[] = [...additionalPrefabricants, ...prefabricantCollection];
        spyOn(prefabricantService, 'addPrefabricantToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(prefabricantService.query).toHaveBeenCalled();
        expect(prefabricantService.addPrefabricantToCollectionIfMissing).toHaveBeenCalledWith(
          prefabricantCollection,
          ...additionalPrefabricants
        );
        expect(comp.prefabricantsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 456 };
        const parcelle: IParcelle = { id: 79860 };
        ficheSuiviOuvrage.parcelle = parcelle;
        const prevision: IPrevision = { id: 32704 };
        ficheSuiviOuvrage.prevision = prevision;
        const natureouvrage: INatureOuvrage = { id: 94423 };
        ficheSuiviOuvrage.natureouvrage = natureouvrage;
        const typehabitation: ITypeHabitation = { id: 35437 };
        ficheSuiviOuvrage.typehabitation = typehabitation;
        const sourceapprovep: ISourceApprovEp = { id: 2074 };
        ficheSuiviOuvrage.sourceapprovep = sourceapprovep;
        const modeevacuationeauusee: IModeEvacuationEauUsee = { id: 8438 };
        ficheSuiviOuvrage.modeevacuationeauusee = modeevacuationeauusee;
        const modeevacexcreta: IModeEvacExcreta = { id: 28931 };
        ficheSuiviOuvrage.modeevacexcreta = modeevacexcreta;
        const macon: IMacon = { id: 48914 };
        ficheSuiviOuvrage.macon = macon;
        const prefabricant: IPrefabricant = { id: 45255 };
        ficheSuiviOuvrage.prefabricant = prefabricant;

        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(ficheSuiviOuvrage));
        expect(comp.parcellesSharedCollection).toContain(parcelle);
        expect(comp.previsionsSharedCollection).toContain(prevision);
        expect(comp.natureOuvragesSharedCollection).toContain(natureouvrage);
        expect(comp.typeHabitationsSharedCollection).toContain(typehabitation);
        expect(comp.sourceApprovEpsSharedCollection).toContain(sourceapprovep);
        expect(comp.modeEvacuationEauUseesSharedCollection).toContain(modeevacuationeauusee);
        expect(comp.modeEvacExcretasSharedCollection).toContain(modeevacexcreta);
        expect(comp.maconsSharedCollection).toContain(macon);
        expect(comp.prefabricantsSharedCollection).toContain(prefabricant);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const ficheSuiviOuvrage = { id: 123 };
        spyOn(ficheSuiviOuvrageService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: ficheSuiviOuvrage }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(ficheSuiviOuvrageService.update).toHaveBeenCalledWith(ficheSuiviOuvrage);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const ficheSuiviOuvrage = new FicheSuiviOuvrage();
        spyOn(ficheSuiviOuvrageService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: ficheSuiviOuvrage }));
        saveSubject.complete();

        // THEN
        expect(ficheSuiviOuvrageService.create).toHaveBeenCalledWith(ficheSuiviOuvrage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const ficheSuiviOuvrage = { id: 123 };
        spyOn(ficheSuiviOuvrageService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ ficheSuiviOuvrage });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(ficheSuiviOuvrageService.update).toHaveBeenCalledWith(ficheSuiviOuvrage);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackParcelleById', () => {
        it('Should return tracked Parcelle primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackParcelleById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPrevisionById', () => {
        it('Should return tracked Prevision primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPrevisionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackNatureOuvrageById', () => {
        it('Should return tracked NatureOuvrage primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackNatureOuvrageById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTypeHabitationById', () => {
        it('Should return tracked TypeHabitation primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTypeHabitationById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackSourceApprovEpById', () => {
        it('Should return tracked SourceApprovEp primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackSourceApprovEpById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackModeEvacuationEauUseeById', () => {
        it('Should return tracked ModeEvacuationEauUsee primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackModeEvacuationEauUseeById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackModeEvacExcretaById', () => {
        it('Should return tracked ModeEvacExcreta primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackModeEvacExcretaById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackMaconById', () => {
        it('Should return tracked Macon primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackMaconById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackPrefabricantById', () => {
        it('Should return tracked Prefabricant primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackPrefabricantById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
