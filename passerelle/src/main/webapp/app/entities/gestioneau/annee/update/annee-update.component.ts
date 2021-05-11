import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAnnee, Annee } from '../annee.model';
import { AnneeService } from '../service/annee.service';

@Component({
  selector: 'jhi-annee-update',
  templateUrl: './annee-update.component.html',
})
export class AnneeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(protected anneeService: AnneeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ annee }) => {
      this.updateForm(annee);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const annee = this.createFromForm();
    if (annee.id !== undefined) {
      this.subscribeToSaveResponse(this.anneeService.update(annee));
    } else {
      this.subscribeToSaveResponse(this.anneeService.create(annee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnnee>>): void {
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

  protected updateForm(annee: IAnnee): void {
    this.editForm.patchValue({
      id: annee.id,
      libelle: annee.libelle,
    });
  }

  protected createFromForm(): IAnnee {
    return {
      ...new Annee(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
