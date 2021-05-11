import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';
import { ModeEvacuationEauUseeService } from '../service/mode-evacuation-eau-usee.service';

@Component({
  templateUrl: './mode-evacuation-eau-usee-delete-dialog.component.html',
})
export class ModeEvacuationEauUseeDeleteDialogComponent {
  modeEvacuationEauUsee?: IModeEvacuationEauUsee;

  constructor(protected modeEvacuationEauUseeService: ModeEvacuationEauUseeService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.modeEvacuationEauUseeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
