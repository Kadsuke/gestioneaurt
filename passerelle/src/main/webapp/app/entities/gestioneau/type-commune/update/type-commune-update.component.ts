import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITypeCommune, TypeCommune } from '../type-commune.model';
import { TypeCommuneService } from '../service/type-commune.service';

@Component({
  selector: 'jhi-type-commune-update',
  templateUrl: './type-commune-update.component.html',
})
export class TypeCommuneUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(protected typeCommuneService: TypeCommuneService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeCommune }) => {
      this.updateForm(typeCommune);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const typeCommune = this.createFromForm();
    if (typeCommune.id !== undefined) {
      this.subscribeToSaveResponse(this.typeCommuneService.update(typeCommune));
    } else {
      this.subscribeToSaveResponse(this.typeCommuneService.create(typeCommune));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypeCommune>>): void {
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

  protected updateForm(typeCommune: ITypeCommune): void {
    this.editForm.patchValue({
      id: typeCommune.id,
      libelle: typeCommune.libelle,
    });
  }

  protected createFromForm(): ITypeCommune {
    return {
      ...new TypeCommune(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
