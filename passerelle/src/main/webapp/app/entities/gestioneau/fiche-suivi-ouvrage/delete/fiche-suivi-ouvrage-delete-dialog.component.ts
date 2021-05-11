import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';
import { FicheSuiviOuvrageService } from '../service/fiche-suivi-ouvrage.service';

@Component({
  templateUrl: './fiche-suivi-ouvrage-delete-dialog.component.html',
})
export class FicheSuiviOuvrageDeleteDialogComponent {
  ficheSuiviOuvrage?: IFicheSuiviOuvrage;

  constructor(protected ficheSuiviOuvrageService: FicheSuiviOuvrageService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ficheSuiviOuvrageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
