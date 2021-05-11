import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICentreRegroupement, CentreRegroupement } from '../centre-regroupement.model';
import { CentreRegroupementService } from '../service/centre-regroupement.service';
import { IDirectionRegionale } from 'app/entities/gestioneau/direction-regionale/direction-regionale.model';
import { DirectionRegionaleService } from 'app/entities/gestioneau/direction-regionale/service/direction-regionale.service';

@Component({
  selector: 'jhi-centre-regroupement-update',
  templateUrl: './centre-regroupement-update.component.html',
})
export class CentreRegroupementUpdateComponent implements OnInit {
  isSaving = false;

  directionRegionalesSharedCollection: IDirectionRegionale[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    responsable: [null, [Validators.required]],
    contact: [null, [Validators.required]],
    directionregionale: [],
  });

  constructor(
    protected centreRegroupementService: CentreRegroupementService,
    protected directionRegionaleService: DirectionRegionaleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centreRegroupement }) => {
      this.updateForm(centreRegroupement);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const centreRegroupement = this.createFromForm();
    if (centreRegroupement.id !== undefined) {
      this.subscribeToSaveResponse(this.centreRegroupementService.update(centreRegroupement));
    } else {
      this.subscribeToSaveResponse(this.centreRegroupementService.create(centreRegroupement));
    }
  }

  trackDirectionRegionaleById(index: number, item: IDirectionRegionale): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICentreRegroupement>>): void {
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

  protected updateForm(centreRegroupement: ICentreRegroupement): void {
    this.editForm.patchValue({
      id: centreRegroupement.id,
      libelle: centreRegroupement.libelle,
      responsable: centreRegroupement.responsable,
      contact: centreRegroupement.contact,
      directionregionale: centreRegroupement.directionregionale,
    });

    this.directionRegionalesSharedCollection = this.directionRegionaleService.addDirectionRegionaleToCollectionIfMissing(
      this.directionRegionalesSharedCollection,
      centreRegroupement.directionregionale
    );
  }

  protected loadRelationshipsOptions(): void {
    this.directionRegionaleService
      .query()
      .pipe(map((res: HttpResponse<IDirectionRegionale[]>) => res.body ?? []))
      .pipe(
        map((directionRegionales: IDirectionRegionale[]) =>
          this.directionRegionaleService.addDirectionRegionaleToCollectionIfMissing(
            directionRegionales,
            this.editForm.get('directionregionale')!.value
          )
        )
      )
      .subscribe((directionRegionales: IDirectionRegionale[]) => (this.directionRegionalesSharedCollection = directionRegionales));
  }

  protected createFromForm(): ICentreRegroupement {
    return {
      ...new CentreRegroupement(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      responsable: this.editForm.get(['responsable'])!.value,
      contact: this.editForm.get(['contact'])!.value,
      directionregionale: this.editForm.get(['directionregionale'])!.value,
    };
  }
}
