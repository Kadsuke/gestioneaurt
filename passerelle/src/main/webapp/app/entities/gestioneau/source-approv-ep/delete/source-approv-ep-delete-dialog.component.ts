import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISourceApprovEp } from '../source-approv-ep.model';
import { SourceApprovEpService } from '../service/source-approv-ep.service';

@Component({
  templateUrl: './source-approv-ep-delete-dialog.component.html',
})
export class SourceApprovEpDeleteDialogComponent {
  sourceApprovEp?: ISourceApprovEp;

  constructor(protected sourceApprovEpService: SourceApprovEpService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sourceApprovEpService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
