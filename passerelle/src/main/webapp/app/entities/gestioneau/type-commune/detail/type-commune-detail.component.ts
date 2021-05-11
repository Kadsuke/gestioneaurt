import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITypeCommune } from '../type-commune.model';

@Component({
  selector: 'jhi-type-commune-detail',
  templateUrl: './type-commune-detail.component.html',
})
export class TypeCommuneDetailComponent implements OnInit {
  typeCommune: ITypeCommune | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeCommune }) => {
      this.typeCommune = typeCommune;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
