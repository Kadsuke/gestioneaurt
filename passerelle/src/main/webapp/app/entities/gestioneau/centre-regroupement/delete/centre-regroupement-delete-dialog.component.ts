import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICentreRegroupement } from '../centre-regroupement.model';
import { CentreRegroupementService } from '../service/centre-regroupement.service';

@Component({
  templateUrl: './centre-regroupement-delete-dialog.component.html',
})
export class CentreRegroupementDeleteDialogComponent {
  centreRegroupement?: ICentreRegroupement;

  constructor(protected centreRegroupementService: CentreRegroupementService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.centreRegroupementService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
