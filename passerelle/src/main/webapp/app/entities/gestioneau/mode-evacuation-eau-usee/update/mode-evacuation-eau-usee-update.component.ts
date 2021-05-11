import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IModeEvacuationEauUsee, ModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';
import { ModeEvacuationEauUseeService } from '../service/mode-evacuation-eau-usee.service';

@Component({
  selector: 'jhi-mode-evacuation-eau-usee-update',
  templateUrl: './mode-evacuation-eau-usee-update.component.html',
})
export class ModeEvacuationEauUseeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(
    protected modeEvacuationEauUseeService: ModeEvacuationEauUseeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ modeEvacuationEauUsee }) => {
      this.updateForm(modeEvacuationEauUsee);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const modeEvacuationEauUsee = this.createFromForm();
    if (modeEvacuationEauUsee.id !== undefined) {
      this.subscribeToSaveResponse(this.modeEvacuationEauUseeService.update(modeEvacuationEauUsee));
    } else {
      this.subscribeToSaveResponse(this.modeEvacuationEauUseeService.create(modeEvacuationEauUsee));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IModeEvacuationEauUsee>>): void {
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

  protected updateForm(modeEvacuationEauUsee: IModeEvacuationEauUsee): void {
    this.editForm.patchValue({
      id: modeEvacuationEauUsee.id,
      libelle: modeEvacuationEauUsee.libelle,
    });
  }

  protected createFromForm(): IModeEvacuationEauUsee {
    return {
      ...new ModeEvacuationEauUsee(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
