import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IParcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';

@Component({
  templateUrl: './parcelle-delete-dialog.component.html',
})
export class ParcelleDeleteDialogComponent {
  parcelle?: IParcelle;

  constructor(protected parcelleService: ParcelleService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parcelleService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
