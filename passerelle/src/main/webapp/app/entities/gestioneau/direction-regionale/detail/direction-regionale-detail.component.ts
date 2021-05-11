import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDirectionRegionale } from '../direction-regionale.model';

@Component({
  selector: 'jhi-direction-regionale-detail',
  templateUrl: './direction-regionale-detail.component.html',
})
export class DirectionRegionaleDetailComponent implements OnInit {
  directionRegionale: IDirectionRegionale | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ directionRegionale }) => {
      this.directionRegionale = directionRegionale;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
