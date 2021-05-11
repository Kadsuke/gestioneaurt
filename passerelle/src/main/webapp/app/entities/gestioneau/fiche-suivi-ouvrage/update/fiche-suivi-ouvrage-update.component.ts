import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IFicheSuiviOuvrage, FicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';
import { FicheSuiviOuvrageService } from '../service/fiche-suivi-ouvrage.service';
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

@Component({
  selector: 'jhi-fiche-suivi-ouvrage-update',
  templateUrl: './fiche-suivi-ouvrage-update.component.html',
})
export class FicheSuiviOuvrageUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];
  previsionsSharedCollection: IPrevision[] = [];
  natureOuvragesSharedCollection: INatureOuvrage[] = [];
  typeHabitationsSharedCollection: ITypeHabitation[] = [];
  sourceApprovEpsSharedCollection: ISourceApprovEp[] = [];
  modeEvacuationEauUseesSharedCollection: IModeEvacuationEauUsee[] = [];
  modeEvacExcretasSharedCollection: IModeEvacExcreta[] = [];
  maconsSharedCollection: IMacon[] = [];
  prefabricantsSharedCollection: IPrefabricant[] = [];

  editForm = this.fb.group({
    id: [],
    prjAppuis: [null, [Validators.required]],
    nomBenef: [null, [Validators.required]],
    prenomBenef: [null, [Validators.required]],
    professionBenef: [null, [Validators.required]],
    nbUsagers: [null, [Validators.required]],
    contacts: [null, [Validators.required]],
    longitude: [null, [Validators.required]],
    latitude: [null, [Validators.required]],
    dateRemiseDevis: [null, [Validators.required]],
    dateDebutTravaux: [null, [Validators.required]],
    dateFinTravaux: [null, [Validators.required]],
    rue: [],
    porte: [],
    coutMenage: [null, [Validators.required]],
    subvOnea: [null, [Validators.required]],
    subvProjet: [null, [Validators.required]],
    autreSubv: [null, [Validators.required]],
    toles: [null, [Validators.required]],
    animateur: [null, [Validators.required]],
    superviseur: [null, [Validators.required]],
    controleur: [null, [Validators.required]],
    parcelle: [],
    prevision: [],
    natureouvrage: [],
    typehabitation: [],
    sourceapprovep: [],
    modeevacuationeauusee: [],
    modeevacexcreta: [],
    macon: [],
    prefabricant: [],
  });

  constructor(
    protected ficheSuiviOuvrageService: FicheSuiviOuvrageService,
    protected parcelleService: ParcelleService,
    protected previsionService: PrevisionService,
    protected natureOuvrageService: NatureOuvrageService,
    protected typeHabitationService: TypeHabitationService,
    protected sourceApprovEpService: SourceApprovEpService,
    protected modeEvacuationEauUseeService: ModeEvacuationEauUseeService,
    protected modeEvacExcretaService: ModeEvacExcretaService,
    protected maconService: MaconService,
    protected prefabricantService: PrefabricantService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ficheSuiviOuvrage }) => {
      if (ficheSuiviOuvrage.id === undefined) {
        const today = dayjs().startOf('day');
        ficheSuiviOuvrage.dateRemiseDevis = today;
        ficheSuiviOuvrage.dateDebutTravaux = today;
        ficheSuiviOuvrage.dateFinTravaux = today;
      }

      this.updateForm(ficheSuiviOuvrage);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ficheSuiviOuvrage = this.createFromForm();
    if (ficheSuiviOuvrage.id !== undefined) {
      this.subscribeToSaveResponse(this.ficheSuiviOuvrageService.update(ficheSuiviOuvrage));
    } else {
      this.subscribeToSaveResponse(this.ficheSuiviOuvrageService.create(ficheSuiviOuvrage));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  trackPrevisionById(index: number, item: IPrevision): number {
    return item.id!;
  }

  trackNatureOuvrageById(index: number, item: INatureOuvrage): number {
    return item.id!;
  }

  trackTypeHabitationById(index: number, item: ITypeHabitation): number {
    return item.id!;
  }

  trackSourceApprovEpById(index: number, item: ISourceApprovEp): number {
    return item.id!;
  }

  trackModeEvacuationEauUseeById(index: number, item: IModeEvacuationEauUsee): number {
    return item.id!;
  }

  trackModeEvacExcretaById(index: number, item: IModeEvacExcreta): number {
    return item.id!;
  }

  trackMaconById(index: number, item: IMacon): number {
    return item.id!;
  }

  trackPrefabricantById(index: number, item: IPrefabricant): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFicheSuiviOuvrage>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ficheSuiviOuvrage: IFicheSuiviOuvrage): void {
    this.editForm.patchValue({
      id: ficheSuiviOuvrage.id,
      prjAppuis: ficheSuiviOuvrage.prjAppuis,
      nomBenef: ficheSuiviOuvrage.nomBenef,
      prenomBenef: ficheSuiviOuvrage.prenomBenef,
      professionBenef: ficheSuiviOuvrage.professionBenef,
      nbUsagers: ficheSuiviOuvrage.nbUsagers,
      contacts: ficheSuiviOuvrage.contacts,
      longitude: ficheSuiviOuvrage.longitude,
      latitude: ficheSuiviOuvrage.latitude,
      dateRemiseDevis: ficheSuiviOuvrage.dateRemiseDevis ? ficheSuiviOuvrage.dateRemiseDevis.format(DATE_TIME_FORMAT) : null,
      dateDebutTravaux: ficheSuiviOuvrage.dateDebutTravaux ? ficheSuiviOuvrage.dateDebutTravaux.format(DATE_TIME_FORMAT) : null,
      dateFinTravaux: ficheSuiviOuvrage.dateFinTravaux ? ficheSuiviOuvrage.dateFinTravaux.format(DATE_TIME_FORMAT) : null,
      rue: ficheSuiviOuvrage.rue,
      porte: ficheSuiviOuvrage.porte,
      coutMenage: ficheSuiviOuvrage.coutMenage,
      subvOnea: ficheSuiviOuvrage.subvOnea,
      subvProjet: ficheSuiviOuvrage.subvProjet,
      autreSubv: ficheSuiviOuvrage.autreSubv,
      toles: ficheSuiviOuvrage.toles,
      animateur: ficheSuiviOuvrage.animateur,
      superviseur: ficheSuiviOuvrage.superviseur,
      controleur: ficheSuiviOuvrage.controleur,
      parcelle: ficheSuiviOuvrage.parcelle,
      prevision: ficheSuiviOuvrage.prevision,
      natureouvrage: ficheSuiviOuvrage.natureouvrage,
      typehabitation: ficheSuiviOuvrage.typehabitation,
      sourceapprovep: ficheSuiviOuvrage.sourceapprovep,
      modeevacuationeauusee: ficheSuiviOuvrage.modeevacuationeauusee,
      modeevacexcreta: ficheSuiviOuvrage.modeevacexcreta,
      macon: ficheSuiviOuvrage.macon,
      prefabricant: ficheSuiviOuvrage.prefabricant,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      ficheSuiviOuvrage.parcelle
    );
    this.previsionsSharedCollection = this.previsionService.addPrevisionToCollectionIfMissing(
      this.previsionsSharedCollection,
      ficheSuiviOuvrage.prevision
    );
    this.natureOuvragesSharedCollection = this.natureOuvrageService.addNatureOuvrageToCollectionIfMissing(
      this.natureOuvragesSharedCollection,
      ficheSuiviOuvrage.natureouvrage
    );
    this.typeHabitationsSharedCollection = this.typeHabitationService.addTypeHabitationToCollectionIfMissing(
      this.typeHabitationsSharedCollection,
      ficheSuiviOuvrage.typehabitation
    );
    this.sourceApprovEpsSharedCollection = this.sourceApprovEpService.addSourceApprovEpToCollectionIfMissing(
      this.sourceApprovEpsSharedCollection,
      ficheSuiviOuvrage.sourceapprovep
    );
    this.modeEvacuationEauUseesSharedCollection = this.modeEvacuationEauUseeService.addModeEvacuationEauUseeToCollectionIfMissing(
      this.modeEvacuationEauUseesSharedCollection,
      ficheSuiviOuvrage.modeevacuationeauusee
    );
    this.modeEvacExcretasSharedCollection = this.modeEvacExcretaService.addModeEvacExcretaToCollectionIfMissing(
      this.modeEvacExcretasSharedCollection,
      ficheSuiviOuvrage.modeevacexcreta
    );
    this.maconsSharedCollection = this.maconService.addMaconToCollectionIfMissing(this.maconsSharedCollection, ficheSuiviOuvrage.macon);
    this.prefabricantsSharedCollection = this.prefabricantService.addPrefabricantToCollectionIfMissing(
      this.prefabricantsSharedCollection,
      ficheSuiviOuvrage.prefabricant
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parcelleService
      .query()
      .pipe(map((res: HttpResponse<IParcelle[]>) => res.body ?? []))
      .pipe(
        map((parcelles: IParcelle[]) =>
          this.parcelleService.addParcelleToCollectionIfMissing(parcelles, this.editForm.get('parcelle')!.value)
        )
      )
      .subscribe((parcelles: IParcelle[]) => (this.parcellesSharedCollection = parcelles));

    this.previsionService
      .query()
      .pipe(map((res: HttpResponse<IPrevision[]>) => res.body ?? []))
      .pipe(
        map((previsions: IPrevision[]) =>
          this.previsionService.addPrevisionToCollectionIfMissing(previsions, this.editForm.get('prevision')!.value)
        )
      )
      .subscribe((previsions: IPrevision[]) => (this.previsionsSharedCollection = previsions));

    this.natureOuvrageService
      .query()
      .pipe(map((res: HttpResponse<INatureOuvrage[]>) => res.body ?? []))
      .pipe(
        map((natureOuvrages: INatureOuvrage[]) =>
          this.natureOuvrageService.addNatureOuvrageToCollectionIfMissing(natureOuvrages, this.editForm.get('natureouvrage')!.value)
        )
      )
      .subscribe((natureOuvrages: INatureOuvrage[]) => (this.natureOuvragesSharedCollection = natureOuvrages));

    this.typeHabitationService
      .query()
      .pipe(map((res: HttpResponse<ITypeHabitation[]>) => res.body ?? []))
      .pipe(
        map((typeHabitations: ITypeHabitation[]) =>
          this.typeHabitationService.addTypeHabitationToCollectionIfMissing(typeHabitations, this.editForm.get('typehabitation')!.value)
        )
      )
      .subscribe((typeHabitations: ITypeHabitation[]) => (this.typeHabitationsSharedCollection = typeHabitations));

    this.sourceApprovEpService
      .query()
      .pipe(map((res: HttpResponse<ISourceApprovEp[]>) => res.body ?? []))
      .pipe(
        map((sourceApprovEps: ISourceApprovEp[]) =>
          this.sourceApprovEpService.addSourceApprovEpToCollectionIfMissing(sourceApprovEps, this.editForm.get('sourceapprovep')!.value)
        )
      )
      .subscribe((sourceApprovEps: ISourceApprovEp[]) => (this.sourceApprovEpsSharedCollection = sourceApprovEps));

    this.modeEvacuationEauUseeService
      .query()
      .pipe(map((res: HttpResponse<IModeEvacuationEauUsee[]>) => res.body ?? []))
      .pipe(
        map((modeEvacuationEauUsees: IModeEvacuationEauUsee[]) =>
          this.modeEvacuationEauUseeService.addModeEvacuationEauUseeToCollectionIfMissing(
            modeEvacuationEauUsees,
            this.editForm.get('modeevacuationeauusee')!.value
          )
        )
      )
      .subscribe(
        (modeEvacuationEauUsees: IModeEvacuationEauUsee[]) => (this.modeEvacuationEauUseesSharedCollection = modeEvacuationEauUsees)
      );

    this.modeEvacExcretaService
      .query()
      .pipe(map((res: HttpResponse<IModeEvacExcreta[]>) => res.body ?? []))
      .pipe(
        map((modeEvacExcretas: IModeEvacExcreta[]) =>
          this.modeEvacExcretaService.addModeEvacExcretaToCollectionIfMissing(modeEvacExcretas, this.editForm.get('modeevacexcreta')!.value)
        )
      )
      .subscribe((modeEvacExcretas: IModeEvacExcreta[]) => (this.modeEvacExcretasSharedCollection = modeEvacExcretas));

    this.maconService
      .query()
      .pipe(map((res: HttpResponse<IMacon[]>) => res.body ?? []))
      .pipe(map((macons: IMacon[]) => this.maconService.addMaconToCollectionIfMissing(macons, this.editForm.get('macon')!.value)))
      .subscribe((macons: IMacon[]) => (this.maconsSharedCollection = macons));

    this.prefabricantService
      .query()
      .pipe(map((res: HttpResponse<IPrefabricant[]>) => res.body ?? []))
      .pipe(
        map((prefabricants: IPrefabricant[]) =>
          this.prefabricantService.addPrefabricantToCollectionIfMissing(prefabricants, this.editForm.get('prefabricant')!.value)
        )
      )
      .subscribe((prefabricants: IPrefabricant[]) => (this.prefabricantsSharedCollection = prefabricants));
  }

  protected createFromForm(): IFicheSuiviOuvrage {
    return {
      ...new FicheSuiviOuvrage(),
      id: this.editForm.get(['id'])!.value,
      prjAppuis: this.editForm.get(['prjAppuis'])!.value,
      nomBenef: this.editForm.get(['nomBenef'])!.value,
      prenomBenef: this.editForm.get(['prenomBenef'])!.value,
      professionBenef: this.editForm.get(['professionBenef'])!.value,
      nbUsagers: this.editForm.get(['nbUsagers'])!.value,
      contacts: this.editForm.get(['contacts'])!.value,
      longitude: this.editForm.get(['longitude'])!.value,
      latitude: this.editForm.get(['latitude'])!.value,
      dateRemiseDevis: this.editForm.get(['dateRemiseDevis'])!.value
        ? dayjs(this.editForm.get(['dateRemiseDevis'])!.value, DATE_TIME_FORMAT)
        : undefined,
      dateDebutTravaux: this.editForm.get(['dateDebutTravaux'])!.value
        ? dayjs(this.editForm.get(['dateDebutTravaux'])!.value, DATE_TIME_FORMAT)
        : undefined,
      dateFinTravaux: this.editForm.get(['dateFinTravaux'])!.value
        ? dayjs(this.editForm.get(['dateFinTravaux'])!.value, DATE_TIME_FORMAT)
        : undefined,
      rue: this.editForm.get(['rue'])!.value,
      porte: this.editForm.get(['porte'])!.value,
      coutMenage: this.editForm.get(['coutMenage'])!.value,
      subvOnea: this.editForm.get(['subvOnea'])!.value,
      subvProjet: this.editForm.get(['subvProjet'])!.value,
      autreSubv: this.editForm.get(['autreSubv'])!.value,
      toles: this.editForm.get(['toles'])!.value,
      animateur: this.editForm.get(['animateur'])!.value,
      superviseur: this.editForm.get(['superviseur'])!.value,
      controleur: this.editForm.get(['controleur'])!.value,
      parcelle: this.editForm.get(['parcelle'])!.value,
      prevision: this.editForm.get(['prevision'])!.value,
      natureouvrage: this.editForm.get(['natureouvrage'])!.value,
      typehabitation: this.editForm.get(['typehabitation'])!.value,
      sourceapprovep: this.editForm.get(['sourceapprovep'])!.value,
      modeevacuationeauusee: this.editForm.get(['modeevacuationeauusee'])!.value,
      modeevacexcreta: this.editForm.get(['modeevacexcreta'])!.value,
      macon: this.editForm.get(['macon'])!.value,
      prefabricant: this.editForm.get(['prefabricant'])!.value,
    };
  }
}
