import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMacon } from '../macon.model';
import { MaconService } from '../service/macon.service';

@Component({
  templateUrl: './macon-delete-dialog.component.html',
})
export class MaconDeleteDialogComponent {
  macon?: IMacon;

  constructor(protected maconService: MaconService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.maconService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
