import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISourceApprovEp } from '../source-approv-ep.model';

@Component({
  selector: 'jhi-source-approv-ep-detail',
  templateUrl: './source-approv-ep-detail.component.html',
})
export class SourceApprovEpDetailComponent implements OnInit {
  sourceApprovEp: ISourceApprovEp | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sourceApprovEp }) => {
      this.sourceApprovEp = sourceApprovEp;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
