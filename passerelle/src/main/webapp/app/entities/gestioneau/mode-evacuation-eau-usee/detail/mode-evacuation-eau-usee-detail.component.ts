import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';

@Component({
  selector: 'jhi-mode-evacuation-eau-usee-detail',
  templateUrl: './mode-evacuation-eau-usee-detail.component.html',
})
export class ModeEvacuationEauUseeDetailComponent implements OnInit {
  modeEvacuationEauUsee: IModeEvacuationEauUsee | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ modeEvacuationEauUsee }) => {
      this.modeEvacuationEauUsee = modeEvacuationEauUsee;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
