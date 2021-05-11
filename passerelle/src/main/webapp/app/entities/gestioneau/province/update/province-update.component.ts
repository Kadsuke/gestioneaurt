import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProvince, Province } from '../province.model';
import { ProvinceService } from '../service/province.service';
import { IRegion } from 'app/entities/gestioneau/region/region.model';
import { RegionService } from 'app/entities/gestioneau/region/service/region.service';

@Component({
  selector: 'jhi-province-update',
  templateUrl: './province-update.component.html',
})
export class ProvinceUpdateComponent implements OnInit {
  isSaving = false;

  regionsSharedCollection: IRegion[] = [];

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    region: [],
  });

  constructor(
    protected provinceService: ProvinceService,
    protected regionService: RegionService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ province }) => {
      this.updateForm(province);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const province = this.createFromForm();
    if (province.id !== undefined) {
      this.subscribeToSaveResponse(this.provinceService.update(province));
    } else {
      this.subscribeToSaveResponse(this.provinceService.create(province));
    }
  }

  trackRegionById(index: number, item: IRegion): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProvince>>): void {
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

  protected updateForm(province: IProvince): void {
    this.editForm.patchValue({
      id: province.id,
      libelle: province.libelle,
      region: province.region,
    });

    this.regionsSharedCollection = this.regionService.addRegionToCollectionIfMissing(this.regionsSharedCollection, province.region);
  }

  protected loadRelationshipsOptions(): void {
    this.regionService
      .query()
      .pipe(map((res: HttpResponse<IRegion[]>) => res.body ?? []))
      .pipe(map((regions: IRegion[]) => this.regionService.addRegionToCollectionIfMissing(regions, this.editForm.get('region')!.value)))
      .subscribe((regions: IRegion[]) => (this.regionsSharedCollection = regions));
  }

  protected createFromForm(): IProvince {
    return {
      ...new Province(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      region: this.editForm.get(['region'])!.value,
    };
  }
}
