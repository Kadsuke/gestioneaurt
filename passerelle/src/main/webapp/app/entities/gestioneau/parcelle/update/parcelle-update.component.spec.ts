jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { ParcelleService } from '../service/parcelle.service';
import { IParcelle, Parcelle } from '../parcelle.model';
import { ILot } from 'app/entities/gestioneau/lot/lot.model';
import { LotService } from 'app/entities/gestioneau/lot/service/lot.service';

import { ParcelleUpdateComponent } from './parcelle-update.component';

describe('Component Tests', () => {
  describe('Parcelle Management Update Component', () => {
    let comp: ParcelleUpdateComponent;
    let fixture: ComponentFixture<ParcelleUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let parcelleService: ParcelleService;
    let lotService: LotService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ParcelleUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ParcelleUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ParcelleUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      parcelleService = TestBed.inject(ParcelleService);
      lotService = TestBed.inject(LotService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Lot query and add missing value', () => {
        const parcelle: IParcelle = { id: 456 };
        const lot: ILot = { id: 84618 };
        parcelle.lot = lot;

        const lotCollection: ILot[] = [{ id: 75177 }];
        spyOn(lotService, 'query').and.returnValue(of(new HttpResponse({ body: lotCollection })));
        const additionalLots = [lot];
        const expectedCollection: ILot[] = [...additionalLots, ...lotCollection];
        spyOn(lotService, 'addLotToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ parcelle });
        comp.ngOnInit();

        expect(lotService.query).toHaveBeenCalled();
        expect(lotService.addLotToCollectionIfMissing).toHaveBeenCalledWith(lotCollection, ...additionalLots);
        expect(comp.lotsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const parcelle: IParcelle = { id: 456 };
        const lot: ILot = { id: 17020 };
        parcelle.lot = lot;

        activatedRoute.data = of({ parcelle });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(parcelle));
        expect(comp.lotsSharedCollection).toContain(lot);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const parcelle = { id: 123 };
        spyOn(parcelleService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ parcelle });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: parcelle }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(parcelleService.update).toHaveBeenCalledWith(parcelle);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const parcelle = new Parcelle();
        spyOn(parcelleService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ parcelle });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: parcelle }));
        saveSubject.complete();

        // THEN
        expect(parcelleService.create).toHaveBeenCalledWith(parcelle);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const parcelle = { id: 123 };
        spyOn(parcelleService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ parcelle });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(parcelleService.update).toHaveBeenCalledWith(parcelle);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackLotById', () => {
        it('Should return tracked Lot primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackLotById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
