import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ModeEvacuationEauUseeDetailComponent } from './mode-evacuation-eau-usee-detail.component';

describe('Component Tests', () => {
  describe('ModeEvacuationEauUsee Management Detail Component', () => {
    let comp: ModeEvacuationEauUseeDetailComponent;
    let fixture: ComponentFixture<ModeEvacuationEauUseeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ModeEvacuationEauUseeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ modeEvacuationEauUsee: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ModeEvacuationEauUseeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ModeEvacuationEauUseeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load modeEvacuationEauUsee on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.modeEvacuationEauUsee).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
