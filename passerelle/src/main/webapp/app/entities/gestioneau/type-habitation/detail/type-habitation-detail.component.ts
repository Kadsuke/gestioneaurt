import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITypeHabitation } from '../type-habitation.model';

@Component({
  selector: 'jhi-type-habitation-detail',
  templateUrl: './type-habitation-detail.component.html',
})
export class TypeHabitationDetailComponent implements OnInit {
  typeHabitation: ITypeHabitation | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typeHabitation }) => {
      this.typeHabitation = typeHabitation;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
