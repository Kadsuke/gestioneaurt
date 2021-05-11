import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { INatureOuvrage, NatureOuvrage } from '../nature-ouvrage.model';
import { NatureOuvrageService } from '../service/nature-ouvrage.service';

@Component({
  selector: 'jhi-nature-ouvrage-update',
  templateUrl: './nature-ouvrage-update.component.html',
})
export class NatureOuvrageUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(protected natureOuvrageService: NatureOuvrageService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ natureOuvrage }) => {
      this.updateForm(natureOuvrage);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const natureOuvrage = this.createFromForm();
    if (natureOuvrage.id !== undefined) {
      this.subscribeToSaveResponse(this.natureOuvrageService.update(natureOuvrage));
    } else {
      this.subscribeToSaveResponse(this.natureOuvrageService.create(natureOuvrage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INatureOuvrage>>): void {
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

  protected updateForm(natureOuvrage: INatureOuvrage): void {
    this.editForm.patchValue({
      id: natureOuvrage.id,
      libelle: natureOuvrage.libelle,
    });
  }

  protected createFromForm(): INatureOuvrage {
    return {
      ...new NatureOuvrage(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
