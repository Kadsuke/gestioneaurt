import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISecteur } from '../secteur.model';
import { SecteurService } from '../service/secteur.service';

@Component({
  templateUrl: './secteur-delete-dialog.component.html',
})
export class SecteurDeleteDialogComponent {
  secteur?: ISecteur;

  constructor(protected secteurService: SecteurService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.secteurService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
