import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITypeCommune, TypeCommune } from '../type-commune.model';

import { TypeCommuneService } from './type-commune.service';

describe('Service Tests', () => {
  describe('TypeCommune Service', () => {
    let service: TypeCommuneService;
    let httpMock: HttpTestingController;
    let elemDefault: ITypeCommune;
    let expectedResult: ITypeCommune | ITypeCommune[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TypeCommuneService);
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

      it('should create a TypeCommune', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new TypeCommune()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TypeCommune', () => {
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

      it('should partial update a TypeCommune', () => {
        const patchObject = Object.assign({}, new TypeCommune());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TypeCommune', () => {
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

      it('should delete a TypeCommune', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTypeCommuneToCollectionIfMissing', () => {
        it('should add a TypeCommune to an empty array', () => {
          const typeCommune: ITypeCommune = { id: 123 };
          expectedResult = service.addTypeCommuneToCollectionIfMissing([], typeCommune);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(typeCommune);
        });

        it('should not add a TypeCommune to an array that contains it', () => {
          const typeCommune: ITypeCommune = { id: 123 };
          const typeCommuneCollection: ITypeCommune[] = [
            {
              ...typeCommune,
            },
            { id: 456 },
          ];
          expectedResult = service.addTypeCommuneToCollectionIfMissing(typeCommuneCollection, typeCommune);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TypeCommune to an array that doesn't contain it", () => {
          const typeCommune: ITypeCommune = { id: 123 };
          const typeCommuneCollection: ITypeCommune[] = [{ id: 456 }];
          expectedResult = service.addTypeCommuneToCollectionIfMissing(typeCommuneCollection, typeCommune);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(typeCommune);
        });

        it('should add only unique TypeCommune to an array', () => {
          const typeCommuneArray: ITypeCommune[] = [{ id: 123 }, { id: 456 }, { id: 83105 }];
          const typeCommuneCollection: ITypeCommune[] = [{ id: 123 }];
          expectedResult = service.addTypeCommuneToCollectionIfMissing(typeCommuneCollection, ...typeCommuneArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const typeCommune: ITypeCommune = { id: 123 };
          const typeCommune2: ITypeCommune = { id: 456 };
          expectedResult = service.addTypeCommuneToCollectionIfMissing([], typeCommune, typeCommune2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(typeCommune);
          expect(expectedResult).toContain(typeCommune2);
        });

        it('should accept null and undefined values', () => {
          const typeCommune: ITypeCommune = { id: 123 };
          expectedResult = service.addTypeCommuneToCollectionIfMissing([], null, typeCommune, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(typeCommune);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
