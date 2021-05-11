import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAnnee } from '../annee.model';
import { AnneeService } from '../service/annee.service';

@Component({
  templateUrl: './annee-delete-dialog.component.html',
})
export class AnneeDeleteDialogComponent {
  annee?: IAnnee;

  constructor(protected anneeService: AnneeService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.anneeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
