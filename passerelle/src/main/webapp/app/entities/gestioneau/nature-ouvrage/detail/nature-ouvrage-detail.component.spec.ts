import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NatureOuvrageDetailComponent } from './nature-ouvrage-detail.component';

describe('Component Tests', () => {
  describe('NatureOuvrage Management Detail Component', () => {
    let comp: NatureOuvrageDetailComponent;
    let fixture: ComponentFixture<NatureOuvrageDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [NatureOuvrageDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ natureOuvrage: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(NatureOuvrageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NatureOuvrageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load natureOuvrage on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.natureOuvrage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
