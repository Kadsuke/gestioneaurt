import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SourceApprovEpDetailComponent } from './source-approv-ep-detail.component';

describe('Component Tests', () => {
  describe('SourceApprovEp Management Detail Component', () => {
    let comp: SourceApprovEpDetailComponent;
    let fixture: ComponentFixture<SourceApprovEpDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SourceApprovEpDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ sourceApprovEp: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SourceApprovEpDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SourceApprovEpDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load sourceApprovEp on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.sourceApprovEp).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
