import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICentreRegroupement } from '../centre-regroupement.model';

@Component({
  selector: 'jhi-centre-regroupement-detail',
  templateUrl: './centre-regroupement-detail.component.html',
})
export class CentreRegroupementDetailComponent implements OnInit {
  centreRegroupement: ICentreRegroupement | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centreRegroupement }) => {
      this.centreRegroupement = centreRegroupement;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
