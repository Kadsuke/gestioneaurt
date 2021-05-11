import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILocalite, Localite } from '../localite.model';
import { LocaliteService } from '../service/localite.service';
import { ICommune } from 'app/entities/gestioneau/commune/commune.model';
import { CommuneService } from 'app/entities/gestioneau/commune/service/commune.service';

@Component({
  selector: 'jhi-localite-update',
  templateUrl: './localite-update.component.html',
})
export class LocaliteUpdateComponent implements OnInit {
  isSaving = false;

  communesSharedCollection: ICommune[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    commune: [],
  });

  constructor(
    protected localiteService: LocaliteService,
    protected communeService: CommuneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ localite }) => {
      this.updateForm(localite);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const localite = this.createFromForm();
    if (localite.id !== undefined) {
      this.subscribeToSaveResponse(this.localiteService.update(localite));
    } else {
      this.subscribeToSaveResponse(this.localiteService.create(localite));
    }
  }

  trackCommuneById(index: number, item: ICommune): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILocalite>>): void {
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

  protected updateForm(localite: ILocalite): void {
    this.editForm.patchValue({
      id: localite.id,
      libelle: localite.libelle,
      commune: localite.commune,
    });

    this.communesSharedCollection = this.communeService.addCommuneToCollectionIfMissing(this.communesSharedCollection, localite.commune);
  }

  protected loadRelationshipsOptions(): void {
    this.communeService
      .query()
      .pipe(map((res: HttpResponse<ICommune[]>) => res.body ?? []))
      .pipe(
        map((communes: ICommune[]) => this.communeService.addCommuneToCollectionIfMissing(communes, this.editForm.get('commune')!.value))
      )
      .subscribe((communes: ICommune[]) => (this.communesSharedCollection = communes));
  }

  protected createFromForm(): ILocalite {
    return {
      ...new Localite(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      commune: this.editForm.get(['commune'])!.value,
    };
  }
}
