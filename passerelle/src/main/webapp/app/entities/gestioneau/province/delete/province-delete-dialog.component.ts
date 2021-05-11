import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProvince } from '../province.model';
import { ProvinceService } from '../service/province.service';

@Component({
  templateUrl: './province-delete-dialog.component.html',
})
export class ProvinceDeleteDialogComponent {
  province?: IProvince;

  constructor(protected provinceService: ProvinceService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.provinceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
