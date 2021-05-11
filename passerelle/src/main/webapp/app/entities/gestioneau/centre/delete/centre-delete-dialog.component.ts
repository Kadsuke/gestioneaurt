import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICentre } from '../centre.model';
import { CentreService } from '../service/centre.service';

@Component({
  templateUrl: './centre-delete-dialog.component.html',
})
export class CentreDeleteDialogComponent {
  centre?: ICentre;

  constructor(protected centreService: CentreService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.centreService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
