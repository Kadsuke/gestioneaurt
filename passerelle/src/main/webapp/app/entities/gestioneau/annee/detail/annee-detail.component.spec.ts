import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AnneeDetailComponent } from './annee-detail.component';

describe('Component Tests', () => {
  describe('Annee Management Detail Component', () => {
    let comp: AnneeDetailComponent;
    let fixture: ComponentFixture<AnneeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [AnneeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ annee: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(AnneeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AnneeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load annee on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.annee).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
