import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ParcelleDetailComponent } from './parcelle-detail.component';

describe('Component Tests', () => {
  describe('Parcelle Management Detail Component', () => {
    let comp: ParcelleDetailComponent;
    let fixture: ComponentFixture<ParcelleDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ParcelleDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ parcelle: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ParcelleDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ParcelleDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load parcelle on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.parcelle).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
