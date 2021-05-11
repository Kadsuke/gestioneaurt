import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICommune, Commune } from '../commune.model';
import { CommuneService } from '../service/commune.service';
import { IProvince } from 'app/entities/gestioneau/province/province.model';
import { ProvinceService } from 'app/entities/gestioneau/province/service/province.service';
import { ITypeCommune } from 'app/entities/gestioneau/type-commune/type-commune.model';
import { TypeCommuneService } from 'app/entities/gestioneau/type-commune/service/type-commune.service';

@Component({
  selector: 'jhi-commune-update',
  templateUrl: './commune-update.component.html',
})
export class CommuneUpdateComponent implements OnInit {
  isSaving = false;

  provincesSharedCollection: IProvince[] = [];
  typeCommunesSharedCollection: ITypeCommune[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    province: [],
    typecommune: [],
  });

  constructor(
    protected communeService: CommuneService,
    protected provinceService: ProvinceService,
    protected typeCommuneService: TypeCommuneService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commune }) => {
      this.updateForm(commune);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const commune = this.createFromForm();
    if (commune.id !== undefined) {
      this.subscribeToSaveResponse(this.communeService.update(commune));
    } else {
      this.subscribeToSaveResponse(this.communeService.create(commune));
    }
  }

  trackProvinceById(index: number, item: IProvince): number {
    return item.id!;
  }

  trackTypeCommuneById(index: number, item: ITypeCommune): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICommune>>): void {
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

  protected updateForm(commune: ICommune): void {
    this.editForm.patchValue({
      id: commune.id,
      libelle: commune.libelle,
      province: commune.province,
      typecommune: commune.typecommune,
    });

    this.provincesSharedCollection = this.provinceService.addProvinceToCollectionIfMissing(
      this.provincesSharedCollection,
      commune.province
    );
    this.typeCommunesSharedCollection = this.typeCommuneService.addTypeCommuneToCollectionIfMissing(
      this.typeCommunesSharedCollection,
      commune.typecommune
    );
  }

  protected loadRelationshipsOptions(): void {
    this.provinceService
      .query()
      .pipe(map((res: HttpResponse<IProvince[]>) => res.body ?? []))
      .pipe(
        map((provinces: IProvince[]) =>
          this.provinceService.addProvinceToCollectionIfMissing(provinces, this.editForm.get('province')!.value)
        )
      )
      .subscribe((provinces: IProvince[]) => (this.provincesSharedCollection = provinces));

    this.typeCommuneService
      .query()
      .pipe(map((res: HttpResponse<ITypeCommune[]>) => res.body ?? []))
      .pipe(
        map((typeCommunes: ITypeCommune[]) =>
          this.typeCommuneService.addTypeCommuneToCollectionIfMissing(typeCommunes, this.editForm.get('typecommune')!.value)
        )
      )
      .subscribe((typeCommunes: ITypeCommune[]) => (this.typeCommunesSharedCollection = typeCommunes));
  }

  protected createFromForm(): ICommune {
    return {
      ...new Commune(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      province: this.editForm.get(['province'])!.value,
      typecommune: this.editForm.get(['typecommune'])!.value,
    };
  }
}
