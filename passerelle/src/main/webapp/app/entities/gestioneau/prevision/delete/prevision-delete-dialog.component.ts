import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPrevision } from '../prevision.model';
import { PrevisionService } from '../service/prevision.service';

@Component({
  templateUrl: './prevision-delete-dialog.component.html',
})
export class PrevisionDeleteDialogComponent {
  prevision?: IPrevision;

  constructor(protected previsionService: PrevisionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.previsionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
