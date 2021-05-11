import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPrefabricant } from '../prefabricant.model';
import { PrefabricantService } from '../service/prefabricant.service';

@Component({
  templateUrl: './prefabricant-delete-dialog.component.html',
})
export class PrefabricantDeleteDialogComponent {
  prefabricant?: IPrefabricant;

  constructor(protected prefabricantService: PrefabricantService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.prefabricantService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
