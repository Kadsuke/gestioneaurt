import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPrefabricant, Prefabricant } from '../prefabricant.model';

import { PrefabricantService } from './prefabricant.service';

describe('Service Tests', () => {
  describe('Prefabricant Service', () => {
    let service: PrefabricantService;
    let httpMock: HttpTestingController;
    let elemDefault: IPrefabricant;
    let expectedResult: IPrefabricant | IPrefabricant[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PrefabricantService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        libelle: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Prefabricant', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Prefabricant()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Prefabricant', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            libelle: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Prefabricant', () => {
        const patchObject = Object.assign({}, new Prefabricant());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Prefabricant', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            libelle: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Prefabricant', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPrefabricantToCollectionIfMissing', () => {
        it('should add a Prefabricant to an empty array', () => {
          const prefabricant: IPrefabricant = { id: 123 };
          expectedResult = service.addPrefabricantToCollectionIfMissing([], prefabricant);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(prefabricant);
        });

        it('should not add a Prefabricant to an array that contains it', () => {
          const prefabricant: IPrefabricant = { id: 123 };
          const prefabricantCollection: IPrefabricant[] = [
            {
              ...prefabricant,
            },
            { id: 456 },
          ];
          expectedResult = service.addPrefabricantToCollectionIfMissing(prefabricantCollection, prefabricant);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Prefabricant to an array that doesn't contain it", () => {
          const prefabricant: IPrefabricant = { id: 123 };
          const prefabricantCollection: IPrefabricant[] = [{ id: 456 }];
          expectedResult = service.addPrefabricantToCollectionIfMissing(prefabricantCollection, prefabricant);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(prefabricant);
        });

        it('should add only unique Prefabricant to an array', () => {
          const prefabricantArray: IPrefabricant[] = [{ id: 123 }, { id: 456 }, { id: 90867 }];
          const prefabricantCollection: IPrefabricant[] = [{ id: 123 }];
          expectedResult = service.addPrefabricantToCollectionIfMissing(prefabricantCollection, ...prefabricantArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const prefabricant: IPrefabricant = { id: 123 };
          const prefabricant2: IPrefabricant = { id: 456 };
          expectedResult = service.addPrefabricantToCollectionIfMissing([], prefabricant, prefabricant2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(prefabricant);
          expect(expectedResult).toContain(prefabricant2);
        });

        it('should accept null and undefined values', () => {
          const prefabricant: IPrefabricant = { id: 123 };
          expectedResult = service.addPrefabricantToCollectionIfMissing([], null, prefabricant, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(prefabricant);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
