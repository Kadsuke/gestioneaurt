import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LocaliteDetailComponent } from './localite-detail.component';

describe('Component Tests', () => {
  describe('Localite Management Detail Component', () => {
    let comp: LocaliteDetailComponent;
    let fixture: ComponentFixture<LocaliteDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [LocaliteDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ localite: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(LocaliteDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LocaliteDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load localite on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.localite).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
