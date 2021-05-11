import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DirectionRegionaleDetailComponent } from './direction-regionale-detail.component';

describe('Component Tests', () => {
  describe('DirectionRegionale Management Detail Component', () => {
    let comp: DirectionRegionaleDetailComponent;
    let fixture: ComponentFixture<DirectionRegionaleDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [DirectionRegionaleDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ directionRegionale: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(DirectionRegionaleDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DirectionRegionaleDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load directionRegionale on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.directionRegionale).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
