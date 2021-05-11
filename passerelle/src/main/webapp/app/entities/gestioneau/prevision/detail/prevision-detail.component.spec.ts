import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PrevisionDetailComponent } from './prevision-detail.component';

describe('Component Tests', () => {
  describe('Prevision Management Detail Component', () => {
    let comp: PrevisionDetailComponent;
    let fixture: ComponentFixture<PrevisionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PrevisionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ prevision: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PrevisionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PrevisionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load prevision on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.prevision).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
