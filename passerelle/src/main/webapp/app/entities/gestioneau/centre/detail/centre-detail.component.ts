import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICentre } from '../centre.model';

@Component({
  selector: 'jhi-centre-detail',
  templateUrl: './centre-detail.component.html',
})
export class CentreDetailComponent implements OnInit {
  centre: ICentre | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ centre }) => {
      this.centre = centre;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
