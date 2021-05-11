import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISection } from '../section.model';
import { SectionService } from '../service/section.service';

@Component({
  templateUrl: './section-delete-dialog.component.html',
})
export class SectionDeleteDialogComponent {
  section?: ISection;

  constructor(protected sectionService: SectionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sectionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
