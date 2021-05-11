import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILocalite } from '../localite.model';
import { LocaliteService } from '../service/localite.service';

@Component({
  templateUrl: './localite-delete-dialog.component.html',
})
export class LocaliteDeleteDialogComponent {
  localite?: ILocalite;

  constructor(protected localiteService: LocaliteService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.localiteService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
