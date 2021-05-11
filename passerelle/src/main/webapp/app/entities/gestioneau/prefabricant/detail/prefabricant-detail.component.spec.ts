import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PrefabricantDetailComponent } from './prefabricant-detail.component';

describe('Component Tests', () => {
  describe('Prefabricant Management Detail Component', () => {
    let comp: PrefabricantDetailComponent;
    let fixture: ComponentFixture<PrefabricantDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PrefabricantDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ prefabricant: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PrefabricantDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PrefabricantDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load prefabricant on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.prefabricant).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
