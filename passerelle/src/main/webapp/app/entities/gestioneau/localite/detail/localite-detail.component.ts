import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILocalite } from '../localite.model';

@Component({
  selector: 'jhi-localite-detail',
  templateUrl: './localite-detail.component.html',
})
export class LocaliteDetailComponent implements OnInit {
  localite: ILocalite | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ localite }) => {
      this.localite = localite;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
