import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IModeEvacExcreta } from '../mode-evac-excreta.model';
import { ModeEvacExcretaService } from '../service/mode-evac-excreta.service';

@Component({
  templateUrl: './mode-evac-excreta-delete-dialog.component.html',
})
export class ModeEvacExcretaDeleteDialogComponent {
  modeEvacExcreta?: IModeEvacExcreta;

  constructor(protected modeEvacExcretaService: ModeEvacExcretaService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.modeEvacExcretaService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
