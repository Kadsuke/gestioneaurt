import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FicheSuiviOuvrageDetailComponent } from './fiche-suivi-ouvrage-detail.component';

describe('Component Tests', () => {
  describe('FicheSuiviOuvrage Management Detail Component', () => {
    let comp: FicheSuiviOuvrageDetailComponent;
    let fixture: ComponentFixture<FicheSuiviOuvrageDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [FicheSuiviOuvrageDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ ficheSuiviOuvrage: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(FicheSuiviOuvrageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(FicheSuiviOuvrageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load ficheSuiviOuvrage on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.ficheSuiviOuvrage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
