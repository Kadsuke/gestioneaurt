import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ModeEvacExcretaDetailComponent } from './mode-evac-excreta-detail.component';

describe('Component Tests', () => {
  describe('ModeEvacExcreta Management Detail Component', () => {
    let comp: ModeEvacExcretaDetailComponent;
    let fixture: ComponentFixture<ModeEvacExcretaDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [ModeEvacExcretaDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ modeEvacExcreta: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(ModeEvacExcretaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ModeEvacExcretaDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load modeEvacExcreta on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.modeEvacExcreta).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
