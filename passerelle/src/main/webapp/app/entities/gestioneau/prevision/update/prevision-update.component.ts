import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPrevision, Prevision } from '../prevision.model';
import { PrevisionService } from '../service/prevision.service';
import { ICentre } from 'app/entities/gestioneau/centre/centre.model';
import { CentreService } from 'app/entities/gestioneau/centre/service/centre.service';
import { IAnnee } from 'app/entities/gestioneau/annee/annee.model';
import { AnneeService } from 'app/entities/gestioneau/annee/service/annee.service';

@Component({
  selector: 'jhi-prevision-update',
  templateUrl: './prevision-update.component.html',
})
export class PrevisionUpdateComponent implements OnInit {
  isSaving = false;

  centresCollection: ICentre[] = [];
  refanneesCollection: IAnnee[] = [];

  editForm = this.fb.group({
    id: [],
    nbLatrine: [null, [Validators.required]],
    nbPuisard: [null, [Validators.required]],
    nbPublic: [null, [Validators.required]],
    nbScolaire: [null, [Validators.required]],
    centre: [],
    refannee: [],
  });

  constructor(
    protected previsionService: PrevisionService,
    protected centreService: CentreService,
    protected anneeService: AnneeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prevision }) => {
      this.updateForm(prevision);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const prevision = this.createFromForm();
    if (prevision.id !== undefined) {
      this.subscribeToSaveResponse(this.previsionService.update(prevision));
    } else {
      this.subscribeToSaveResponse(this.previsionService.create(prevision));
    }
  }

  trackCentreById(index: number, item: ICentre): number {
    return item.id!;
  }

  trackAnneeById(index: number, item: IAnnee): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrevision>>): void {
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

  protected updateForm(prevision: IPrevision): void {
    this.editForm.patchValue({
      id: prevision.id,
      nbLatrine: prevision.nbLatrine,
      nbPuisard: prevision.nbPuisard,
      nbPublic: prevision.nbPublic,
      nbScolaire: prevision.nbScolaire,
      centre: prevision.centre,
      refannee: prevision.refannee,
    });

    this.centresCollection = this.centreService.addCentreToCollectionIfMissing(this.centresCollection, prevision.centre);
    this.refanneesCollection = this.anneeService.addAnneeToCollectionIfMissing(this.refanneesCollection, prevision.refannee);
  }

  protected loadRelationshipsOptions(): void {
    this.centreService
      .query({ filter: 'prevision-is-null' })
      .pipe(map((res: HttpResponse<ICentre[]>) => res.body ?? []))
      .pipe(map((centres: ICentre[]) => this.centreService.addCentreToCollectionIfMissing(centres, this.editForm.get('centre')!.value)))
      .subscribe((centres: ICentre[]) => (this.centresCollection = centres));

    this.anneeService
      .query({ filter: 'prevision-is-null' })
      .pipe(map((res: HttpResponse<IAnnee[]>) => res.body ?? []))
      .pipe(map((annees: IAnnee[]) => this.anneeService.addAnneeToCollectionIfMissing(annees, this.editForm.get('refannee')!.value)))
      .subscribe((annees: IAnnee[]) => (this.refanneesCollection = annees));
  }

  protected createFromForm(): IPrevision {
    return {
      ...new Prevision(),
      id: this.editForm.get(['id'])!.value,
      nbLatrine: this.editForm.get(['nbLatrine'])!.value,
      nbPuisard: this.editForm.get(['nbPuisard'])!.value,
      nbPublic: this.editForm.get(['nbPublic'])!.value,
      nbScolaire: this.editForm.get(['nbScolaire'])!.value,
      centre: this.editForm.get(['centre'])!.value,
      refannee: this.editForm.get(['refannee'])!.value,
    };
  }
}
