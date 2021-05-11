import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITypeCommune } from '../type-commune.model';
import { TypeCommuneService } from '../service/type-commune.service';

@Component({
  templateUrl: './type-commune-delete-dialog.component.html',
})
export class TypeCommuneDeleteDialogComponent {
  typeCommune?: ITypeCommune;

  constructor(protected typeCommuneService: TypeCommuneService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.typeCommuneService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
