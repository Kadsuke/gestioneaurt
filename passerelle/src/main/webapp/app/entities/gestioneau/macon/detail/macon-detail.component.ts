import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMacon } from '../macon.model';

@Component({
  selector: 'jhi-macon-detail',
  templateUrl: './macon-detail.component.html',
})
export class MaconDetailComponent implements OnInit {
  macon: IMacon | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ macon }) => {
      this.macon = macon;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
