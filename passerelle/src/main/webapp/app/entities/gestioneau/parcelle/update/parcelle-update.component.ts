import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IParcelle, Parcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';
import { ILot } from 'app/entities/gestioneau/lot/lot.model';
import { LotService } from 'app/entities/gestioneau/lot/service/lot.service';

@Component({
  selector: 'jhi-parcelle-update',
  templateUrl: './parcelle-update.component.html',
})
export class ParcelleUpdateComponent implements OnInit {
  isSaving = false;

  lotsSharedCollection: ILot[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    lot: [],
  });

  constructor(
    protected parcelleService: ParcelleService,
    protected lotService: LotService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parcelle }) => {
      this.updateForm(parcelle);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const parcelle = this.createFromForm();
    if (parcelle.id !== undefined) {
      this.subscribeToSaveResponse(this.parcelleService.update(parcelle));
    } else {
      this.subscribeToSaveResponse(this.parcelleService.create(parcelle));
    }
  }

  trackLotById(index: number, item: ILot): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParcelle>>): void {
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

  protected updateForm(parcelle: IParcelle): void {
    this.editForm.patchValue({
      id: parcelle.id,
      libelle: parcelle.libelle,
      lot: parcelle.lot,
    });

    this.lotsSharedCollection = this.lotService.addLotToCollectionIfMissing(this.lotsSharedCollection, parcelle.lot);
  }

  protected loadRelationshipsOptions(): void {
    this.lotService
      .query()
      .pipe(map((res: HttpResponse<ILot[]>) => res.body ?? []))
      .pipe(map((lots: ILot[]) => this.lotService.addLotToCollectionIfMissing(lots, this.editForm.get('lot')!.value)))
      .subscribe((lots: ILot[]) => (this.lotsSharedCollection = lots));
  }

  protected createFromForm(): IParcelle {
    return {
      ...new Parcelle(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      lot: this.editForm.get(['lot'])!.value,
    };
  }
}
