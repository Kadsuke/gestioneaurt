import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDirectionRegionale } from '../direction-regionale.model';
import { DirectionRegionaleService } from '../service/direction-regionale.service';

@Component({
  templateUrl: './direction-regionale-delete-dialog.component.html',
})
export class DirectionRegionaleDeleteDialogComponent {
  directionRegionale?: IDirectionRegionale;

  constructor(protected directionRegionaleService: DirectionRegionaleService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.directionRegionaleService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
