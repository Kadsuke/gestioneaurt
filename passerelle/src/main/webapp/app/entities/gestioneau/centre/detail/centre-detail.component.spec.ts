import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CentreDetailComponent } from './centre-detail.component';

describe('Component Tests', () => {
  describe('Centre Management Detail Component', () => {
    let comp: CentreDetailComponent;
    let fixture: ComponentFixture<CentreDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CentreDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ centre: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CentreDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CentreDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load centre on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.centre).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
