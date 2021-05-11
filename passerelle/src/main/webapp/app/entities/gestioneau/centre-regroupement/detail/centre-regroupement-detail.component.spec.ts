import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CentreRegroupementDetailComponent } from './centre-regroupement-detail.component';

describe('Component Tests', () => {
  describe('CentreRegroupement Management Detail Component', () => {
    let comp: CentreRegroupementDetailComponent;
    let fixture: ComponentFixture<CentreRegroupementDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [CentreRegroupementDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ centreRegroupement: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(CentreRegroupementDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CentreRegroupementDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load centreRegroupement on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.centreRegroupement).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
