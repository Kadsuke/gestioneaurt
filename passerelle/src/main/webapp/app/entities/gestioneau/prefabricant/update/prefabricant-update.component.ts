import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPrefabricant, Prefabricant } from '../prefabricant.model';
import { PrefabricantService } from '../service/prefabricant.service';

@Component({
  selector: 'jhi-prefabricant-update',
  templateUrl: './prefabricant-update.component.html',
})
export class PrefabricantUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(protected prefabricantService: PrefabricantService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prefabricant }) => {
      this.updateForm(prefabricant);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const prefabricant = this.createFromForm();
    if (prefabricant.id !== undefined) {
      this.subscribeToSaveResponse(this.prefabricantService.update(prefabricant));
    } else {
      this.subscribeToSaveResponse(this.prefabricantService.create(prefabricant));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPrefabricant>>): void {
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

  protected updateForm(prefabricant: IPrefabricant): void {
    this.editForm.patchValue({
      id: prefabricant.id,
      libelle: prefabricant.libelle,
    });
  }

  protected createFromForm(): IPrefabricant {
    return {
      ...new Prefabricant(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
