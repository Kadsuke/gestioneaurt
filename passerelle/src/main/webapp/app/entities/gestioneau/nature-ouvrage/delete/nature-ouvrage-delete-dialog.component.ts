import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INatureOuvrage } from '../nature-ouvrage.model';
import { NatureOuvrageService } from '../service/nature-ouvrage.service';

@Component({
  templateUrl: './nature-ouvrage-delete-dialog.component.html',
})
export class NatureOuvrageDeleteDialogComponent {
  natureOuvrage?: INatureOuvrage;

  constructor(protected natureOuvrageService: NatureOuvrageService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.natureOuvrageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
