jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { CommuneService } from '../service/commune.service';
import { ICommune, Commune } from '../commune.model';
import { IProvince } from 'app/entities/gestioneau/province/province.model';
import { ProvinceService } from 'app/entities/gestioneau/province/service/province.service';
import { ITypeCommune } from 'app/entities/gestioneau/type-commune/type-commune.model';
import { TypeCommuneService } from 'app/entities/gestioneau/type-commune/service/type-commune.service';

import { CommuneUpdateComponent } from './commune-update.component';

describe('Component Tests', () => {
  describe('Commune Management Update Component', () => {
    let comp: CommuneUpdateComponent;
    let fixture: ComponentFixture<CommuneUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let communeService: CommuneService;
    let provinceService: ProvinceService;
    let typeCommuneService: TypeCommuneService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CommuneUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(CommuneUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CommuneUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      communeService = TestBed.inject(CommuneService);
      provinceService = TestBed.inject(ProvinceService);
      typeCommuneService = TestBed.inject(TypeCommuneService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Province query and add missing value', () => {
        const commune: ICommune = { id: 456 };
        const province: IProvince = { id: 83051 };
        commune.province = province;

        const provinceCollection: IProvince[] = [{ id: 50162 }];
        spyOn(provinceService, 'query').and.returnValue(of(new HttpResponse({ body: provinceCollection })));
        const additionalProvinces = [province];
        const expectedCollection: IProvince[] = [...additionalProvinces, ...provinceCollection];
        spyOn(provinceService, 'addProvinceToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        expect(provinceService.query).toHaveBeenCalled();
        expect(provinceService.addProvinceToCollectionIfMissing).toHaveBeenCalledWith(provinceCollection, ...additionalProvinces);
        expect(comp.provincesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TypeCommune query and add missing value', () => {
        const commune: ICommune = { id: 456 };
        const typecommune: ITypeCommune = { id: 95857 };
        commune.typecommune = typecommune;

        const typeCommuneCollection: ITypeCommune[] = [{ id: 68953 }];
        spyOn(typeCommuneService, 'query').and.returnValue(of(new HttpResponse({ body: typeCommuneCollection })));
        const additionalTypeCommunes = [typecommune];
        const expectedCollection: ITypeCommune[] = [...additionalTypeCommunes, ...typeCommuneCollection];
        spyOn(typeCommuneService, 'addTypeCommuneToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        expect(typeCommuneService.query).toHaveBeenCalled();
        expect(typeCommuneService.addTypeCommuneToCollectionIfMissing).toHaveBeenCalledWith(
          typeCommuneCollection,
          ...additionalTypeCommunes
        );
        expect(comp.typeCommunesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const commune: ICommune = { id: 456 };
        const province: IProvince = { id: 48947 };
        commune.province = province;
        const typecommune: ITypeCommune = { id: 80093 };
        commune.typecommune = typecommune;

        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(commune));
        expect(comp.provincesSharedCollection).toContain(province);
        expect(comp.typeCommunesSharedCollection).toContain(typecommune);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const commune = { id: 123 };
        spyOn(communeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: commune }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(communeService.update).toHaveBeenCalledWith(commune);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const commune = new Commune();
        spyOn(communeService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: commune }));
        saveSubject.complete();

        // THEN
        expect(communeService.create).toHaveBeenCalledWith(commune);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const commune = { id: 123 };
        spyOn(communeService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ commune });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(communeService.update).toHaveBeenCalledWith(commune);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackProvinceById', () => {
        it('Should return tracked Province primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackProvinceById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTypeCommuneById', () => {
        it('Should return tracked TypeCommune primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTypeCommuneById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
