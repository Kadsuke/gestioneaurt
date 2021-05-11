import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILot, Lot } from '../lot.model';
import { LotService } from '../service/lot.service';
import { ISection } from 'app/entities/gestioneau/section/section.model';
import { SectionService } from 'app/entities/gestioneau/section/service/section.service';

@Component({
  selector: 'jhi-lot-update',
  templateUrl: './lot-update.component.html',
})
export class LotUpdateComponent implements OnInit {
  isSaving = false;

  sectionsSharedCollection: ISection[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    section: [],
  });

  constructor(
    protected lotService: LotService,
    protected sectionService: SectionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lot }) => {
      this.updateForm(lot);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lot = this.createFromForm();
    if (lot.id !== undefined) {
      this.subscribeToSaveResponse(this.lotService.update(lot));
    } else {
      this.subscribeToSaveResponse(this.lotService.create(lot));
    }
  }

  trackSectionById(index: number, item: ISection): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILot>>): void {
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

  protected updateForm(lot: ILot): void {
    this.editForm.patchValue({
      id: lot.id,
      libelle: lot.libelle,
      section: lot.section,
    });

    this.sectionsSharedCollection = this.sectionService.addSectionToCollectionIfMissing(this.sectionsSharedCollection, lot.section);
  }

  protected loadRelationshipsOptions(): void {
    this.sectionService
      .query()
      .pipe(map((res: HttpResponse<ISection[]>) => res.body ?? []))
      .pipe(
        map((sections: ISection[]) => this.sectionService.addSectionToCollectionIfMissing(sections, this.editForm.get('section')!.value))
      )
      .subscribe((sections: ISection[]) => (this.sectionsSharedCollection = sections));
  }

  protected createFromForm(): ILot {
    return {
      ...new Lot(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      section: this.editForm.get(['section'])!.value,
    };
  }
}
