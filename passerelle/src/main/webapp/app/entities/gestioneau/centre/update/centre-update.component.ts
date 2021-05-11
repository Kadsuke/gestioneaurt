import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICentre, Centre } from '../centre.model';
import { CentreService } from '../service/centre.service';
import { ICentreRegroupement } from 'app/entities/gestioneau/centre-regroupement/centre-regroupement.model';
import { CentreRegroupementService } from 'app/entities/gestioneau/centre-regroupement/service/centre-regroupement.service';

@Component({
  selector: 'jhi-centre-update',
  templateUrl: './centre-update.component.html',
})
export class CentreUpdateComponent implements OnInit {
  isSaving = false;

  centreRegroupementsSharedCollection: ICentreRegroupement[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    responsable: [null, [Validators.required]],
    contact: [null, [Validators.required]],
    centreregroupement: [],
  });

  constructor(
    protected centreService: CentreService,
    protected centreRegroupementService: CentreRegroupementService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centre }) => {
      this.updateForm(centre);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centre = this.createFromForm();
    if (centre.id !== undefined) {
      this.subscribeToSaveResponse(this.centreService.update(centre));
    } else {
      this.subscribeToSaveResponse(this.centreService.create(centre));
    }
  }

  trackCentreRegroupementById(index: number, item: ICentreRegroupement): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICentre>>): void {
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

  protected updateForm(centre: ICentre): void {
    this.editForm.patchValue({
      id: centre.id,
      libelle: centre.libelle,
      responsable: centre.responsable,
      contact: centre.contact,
      centreregroupement: centre.centreregroupement,
    });

    this.centreRegroupementsSharedCollection = this.centreRegroupementService.addCentreRegroupementToCollectionIfMissing(
      this.centreRegroupementsSharedCollection,
      centre.centreregroupement
    );
  }

  protected loadRelationshipsOptions(): void {
    this.centreRegroupementService
      .query()
      .pipe(map((res: HttpResponse<ICentreRegroupement[]>) => res.body ?? []))
      .pipe(
        map((centreRegroupements: ICentreRegroupement[]) =>
          this.centreRegroupementService.addCentreRegroupementToCollectionIfMissing(
            centreRegroupements,
            this.editForm.get('centreregroupement')!.value
          )
        )
      )
      .subscribe((centreRegroupements: ICentreRegroupement[]) => (this.centreRegroupementsSharedCollection = centreRegroupements));
  }

  protected createFromForm(): ICentre {
    return {
      ...new Centre(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      responsable: this.editForm.get(['responsable'])!.value,
      contact: this.editForm.get(['contact'])!.value,
      centreregroupement: this.editForm.get(['centreregroupement'])!.value,
    };
  }
}
