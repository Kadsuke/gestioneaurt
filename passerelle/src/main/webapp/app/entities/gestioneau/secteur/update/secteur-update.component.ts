import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISecteur, Secteur } from '../secteur.model';
import { SecteurService } from '../service/secteur.service';
import { ILocalite } from 'app/entities/gestioneau/localite/localite.model';
import { LocaliteService } from 'app/entities/gestioneau/localite/service/localite.service';

@Component({
  selector: 'jhi-secteur-update',
  templateUrl: './secteur-update.component.html',
})
export class SecteurUpdateComponent implements OnInit {
  isSaving = false;

  localitesSharedCollection: ILocalite[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    localite: [],
  });

  constructor(
    protected secteurService: SecteurService,
    protected localiteService: LocaliteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ secteur }) => {
      this.updateForm(secteur);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const secteur = this.createFromForm();
    if (secteur.id !== undefined) {
      this.subscribeToSaveResponse(this.secteurService.update(secteur));
    } else {
      this.subscribeToSaveResponse(this.secteurService.create(secteur));
    }
  }

  trackLocaliteById(index: number, item: ILocalite): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISecteur>>): void {
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

  protected updateForm(secteur: ISecteur): void {
    this.editForm.patchValue({
      id: secteur.id,
      libelle: secteur.libelle,
      localite: secteur.localite,
    });

    this.localitesSharedCollection = this.localiteService.addLocaliteToCollectionIfMissing(
      this.localitesSharedCollection,
      secteur.localite
    );
  }

  protected loadRelationshipsOptions(): void {
    this.localiteService
      .query()
      .pipe(map((res: HttpResponse<ILocalite[]>) => res.body ?? []))
      .pipe(
        map((localites: ILocalite[]) =>
          this.localiteService.addLocaliteToCollectionIfMissing(localites, this.editForm.get('localite')!.value)
        )
      )
      .subscribe((localites: ILocalite[]) => (this.localitesSharedCollection = localites));
  }

  protected createFromForm(): ISecteur {
    return {
      ...new Secteur(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      localite: this.editForm.get(['localite'])!.value,
    };
  }
}
