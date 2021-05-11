import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ISourceApprovEp, SourceApprovEp } from '../source-approv-ep.model';
import { SourceApprovEpService } from '../service/source-approv-ep.service';

@Component({
  selector: 'jhi-source-approv-ep-update',
  templateUrl: './source-approv-ep-update.component.html',
})
export class SourceApprovEpUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(
    protected sourceApprovEpService: SourceApprovEpService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sourceApprovEp }) => {
      this.updateForm(sourceApprovEp);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sourceApprovEp = this.createFromForm();
    if (sourceApprovEp.id !== undefined) {
      this.subscribeToSaveResponse(this.sourceApprovEpService.update(sourceApprovEp));
    } else {
      this.subscribeToSaveResponse(this.sourceApprovEpService.create(sourceApprovEp));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISourceApprovEp>>): void {
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

  protected updateForm(sourceApprovEp: ISourceApprovEp): void {
    this.editForm.patchValue({
      id: sourceApprovEp.id,
      libelle: sourceApprovEp.libelle,
    });
  }

  protected createFromForm(): ISourceApprovEp {
    return {
      ...new SourceApprovEp(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
