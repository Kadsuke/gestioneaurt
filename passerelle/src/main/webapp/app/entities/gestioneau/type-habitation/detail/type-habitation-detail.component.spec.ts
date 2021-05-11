import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TypeHabitationDetailComponent } from './type-habitation-detail.component';

describe('Component Tests', () => {
  describe('TypeHabitation Management Detail Component', () => {
    let comp: TypeHabitationDetailComponent;
    let fixture: ComponentFixture<TypeHabitationDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TypeHabitationDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ typeHabitation: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TypeHabitationDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TypeHabitationDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load typeHabitation on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.typeHabitation).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
