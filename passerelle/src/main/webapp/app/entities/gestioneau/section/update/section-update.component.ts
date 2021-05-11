import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISection, Section } from '../section.model';
import { SectionService } from '../service/section.service';
import { ISecteur } from 'app/entities/gestioneau/secteur/secteur.model';
import { SecteurService } from 'app/entities/gestioneau/secteur/service/secteur.service';

@Component({
  selector: 'jhi-section-update',
  templateUrl: './section-update.component.html',
})
export class SectionUpdateComponent implements OnInit {
  isSaving = false;

  secteursSharedCollection: ISecteur[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    secteur: [],
  });

  constructor(
    protected sectionService: SectionService,
    protected secteurService: SecteurService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ section }) => {
      this.updateForm(section);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const section = this.createFromForm();
    if (section.id !== undefined) {
      this.subscribeToSaveResponse(this.sectionService.update(section));
    } else {
      this.subscribeToSaveResponse(this.sectionService.create(section));
    }
  }

  trackSecteurById(index: number, item: ISecteur): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISection>>): void {
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

  protected updateForm(section: ISection): void {
    this.editForm.patchValue({
      id: section.id,
      libelle: section.libelle,
      secteur: section.secteur,
    });

    this.secteursSharedCollection = this.secteurService.addSecteurToCollectionIfMissing(this.secteursSharedCollection, section.secteur);
  }

  protected loadRelationshipsOptions(): void {
    this.secteurService
      .query()
      .pipe(map((res: HttpResponse<ISecteur[]>) => res.body ?? []))
      .pipe(
        map((secteurs: ISecteur[]) => this.secteurService.addSecteurToCollectionIfMissing(secteurs, this.editForm.get('secteur')!.value))
      )
      .subscribe((secteurs: ISecteur[]) => (this.secteursSharedCollection = secteurs));
  }

  protected createFromForm(): ISection {
    return {
      ...new Section(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      secteur: this.editForm.get(['secteur'])!.value,
    };
  }
}
