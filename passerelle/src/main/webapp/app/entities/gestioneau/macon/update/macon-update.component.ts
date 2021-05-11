import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMacon, Macon } from '../macon.model';
import { MaconService } from '../service/macon.service';

@Component({
  selector: 'jhi-macon-update',
  templateUrl: './macon-update.component.html',
})
export class MaconUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(protected maconService: MaconService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ macon }) => {
      this.updateForm(macon);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const macon = this.createFromForm();
    if (macon.id !== undefined) {
      this.subscribeToSaveResponse(this.maconService.update(macon));
    } else {
      this.subscribeToSaveResponse(this.maconService.create(macon));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMacon>>): void {
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

  protected updateForm(macon: IMacon): void {
    this.editForm.patchValue({
      id: macon.id,
      libelle: macon.libelle,
    });
  }

  protected createFromForm(): IMacon {
    return {
      ...new Macon(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
