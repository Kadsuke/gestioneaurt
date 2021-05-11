import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MaconDetailComponent } from './macon-detail.component';

describe('Component Tests', () => {
  describe('Macon Management Detail Component', () => {
    let comp: MaconDetailComponent;
    let fixture: ComponentFixture<MaconDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [MaconDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ macon: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(MaconDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MaconDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load macon on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.macon).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
