import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IModeEvacExcreta } from '../mode-evac-excreta.model';

@Component({
  selector: 'jhi-mode-evac-excreta-detail',
  templateUrl: './mode-evac-excreta-detail.component.html',
})
export class ModeEvacExcretaDetailComponent implements OnInit {
  modeEvacExcreta: IModeEvacExcreta | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ modeEvacExcreta }) => {
      this.modeEvacExcreta = modeEvacExcreta;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
