import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITypeHabitation } from '../type-habitation.model';
import { TypeHabitationService } from '../service/type-habitation.service';

@Component({
  templateUrl: './type-habitation-delete-dialog.component.html',
})
export class TypeHabitationDeleteDialogComponent {
  typeHabitation?: ITypeHabitation;

  constructor(protected typeHabitationService: TypeHabitationService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.typeHabitationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
