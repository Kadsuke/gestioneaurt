import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TypeCommuneDetailComponent } from './type-commune-detail.component';

describe('Component Tests', () => {
  describe('TypeCommune Management Detail Component', () => {
    let comp: TypeCommuneDetailComponent;
    let fixture: ComponentFixture<TypeCommuneDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TypeCommuneDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ typeCommune: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TypeCommuneDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TypeCommuneDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load typeCommune on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.typeCommune).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
