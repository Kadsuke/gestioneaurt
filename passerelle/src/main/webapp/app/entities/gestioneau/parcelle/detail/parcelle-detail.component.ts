import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IParcelle } from '../parcelle.model';

@Component({
  selector: 'jhi-parcelle-detail',
  templateUrl: './parcelle-detail.component.html',
})
export class ParcelleDetailComponent implements OnInit {
  parcelle: IParcelle | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parcelle }) => {
      this.parcelle = parcelle;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
