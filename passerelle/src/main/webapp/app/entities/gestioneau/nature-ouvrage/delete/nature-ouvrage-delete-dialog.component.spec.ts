jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { NatureOuvrageService } from '../service/nature-ouvrage.service';

import { NatureOuvrageDeleteDialogComponent } from './nature-ouvrage-delete-dialog.component';

describe('Component Tests', () => {
  describe('NatureOuvrage Management Delete Component', () => {
    let comp: NatureOuvrageDeleteDialogComponent;
    let fixture: ComponentFixture<NatureOuvrageDeleteDialogComponent>;
    let service: NatureOuvrageService;
    let mockActiveModal: NgbActiveModal;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [NatureOuvrageDeleteDialogComponent],
        providers: [NgbActiveModal],
      })
        .overrideTemplate(NatureOuvrageDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NatureOuvrageDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(NatureOuvrageService);
      mockActiveModal = TestBed.inject(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
        })
      ));

      it('Should not call delete service on clear', () => {
        // GIVEN
        spyOn(service, 'delete');

        // WHEN
        comp.cancel();

        // THEN
        expect(service.delete).not.toHaveBeenCalled();
        expect(mockActiveModal.close).not.toHaveBeenCalled();
        expect(mockActiveModal.dismiss).toHaveBeenCalled();
      });
    });
  });
});
