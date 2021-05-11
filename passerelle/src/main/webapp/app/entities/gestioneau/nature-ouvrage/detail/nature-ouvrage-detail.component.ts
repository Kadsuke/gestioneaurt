import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INatureOuvrage } from '../nature-ouvrage.model';

@Component({
  selector: 'jhi-nature-ouvrage-detail',
  templateUrl: './nature-ouvrage-detail.component.html',
})
export class NatureOuvrageDetailComponent implements OnInit {
  natureOuvrage: INatureOuvrage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ natureOuvrage }) => {
      this.natureOuvrage = natureOuvrage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
