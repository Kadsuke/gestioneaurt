import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProvinceDetailComponent } from './province-detail.component';

describe('Component Tests', () => {
  describe('Province Management Detail Component', () => {
    let comp: ProvinceDetailComponent;
    let fixture: ComponentFixture<ProvinceDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ProvinceDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ province: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ProvinceDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ProvinceDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load province on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.province).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
