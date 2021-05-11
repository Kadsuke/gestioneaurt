import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITypeHabitation, TypeHabitation } from '../type-habitation.model';
import { TypeHabitationService } from '../service/type-habitation.service';

@Component({
  selector: 'jhi-type-habitation-update',
  templateUrl: './type-habitation-update.component.html',
})
export class TypeHabitationUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
  });

  constructor(
    protected typeHabitationService: TypeHabitationService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeHabitation }) => {
      this.updateForm(typeHabitation);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const typeHabitation = this.createFromForm();
    if (typeHabitation.id !== undefined) {
      this.subscribeToSaveResponse(this.typeHabitationService.update(typeHabitation));
    } else {
      this.subscribeToSaveResponse(this.typeHabitationService.create(typeHabitation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypeHabitation>>): void {
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

  protected updateForm(typeHabitation: ITypeHabitation): void {
    this.editForm.patchValue({
      id: typeHabitation.id,
      libelle: typeHabitation.libelle,
    });
  }

  protected createFromForm(): ITypeHabitation {
    return {
      ...new TypeHabitation(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
    };
  }
}
