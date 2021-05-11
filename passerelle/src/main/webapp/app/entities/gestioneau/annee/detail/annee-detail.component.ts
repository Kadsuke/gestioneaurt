import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAnnee } from '../annee.model';

@Component({
  selector: 'jhi-annee-detail',
  templateUrl: './annee-detail.component.html',
})
export class AnneeDetailComponent implements OnInit {
  annee: IAnnee | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ annee }) => {
      this.annee = annee;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
