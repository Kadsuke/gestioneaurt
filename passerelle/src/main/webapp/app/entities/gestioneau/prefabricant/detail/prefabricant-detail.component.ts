import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPrefabricant } from '../prefabricant.model';

@Component({
  selector: 'jhi-prefabricant-detail',
  templateUrl: './prefabricant-detail.component.html',
})
export class PrefabricantDetailComponent implements OnInit {
  prefabricant: IPrefabricant | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ prefabricant }) => {
      this.prefabricant = prefabricant;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
