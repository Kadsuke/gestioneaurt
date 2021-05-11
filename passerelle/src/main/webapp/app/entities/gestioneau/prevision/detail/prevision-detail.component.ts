import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrevision } from '../prevision.model';

@Component({
  selector: 'jhi-prevision-detail',
  templateUrl: './prevision-detail.component.html',
})
export class PrevisionDetailComponent implements OnInit {
  prevision: IPrevision | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prevision }) => {
      this.prevision = prevision;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
