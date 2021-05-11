import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITypeHabitation, TypeHabitation } from '../type-habitation.model';

import { TypeHabitationService } from './type-habitation.service';

describe('Service Tests', () => {
  describe('TypeHabitation Service', () => {
    let service: TypeHabitationService;
    let httpMock: HttpTestingController;
    let elemDefault: ITypeHabitation;
    let expectedResult: ITypeHabitation | ITypeHabitation[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TypeHabitationService);
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

      it('should create a TypeHabitation', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new TypeHabitation()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TypeHabitation', () => {
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

      it('should partial update a TypeHabitation', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new TypeHabitation()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TypeHabitation', () => {
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

      it('should delete a TypeHabitation', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTypeHabitationToCollectionIfMissing', () => {
        it('should add a TypeHabitation to an empty array', () => {
          const typeHabitation: ITypeHabitation = { id: 123 };
          expectedResult = service.addTypeHabitationToCollectionIfMissing([], typeHabitation);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(typeHabitation);
        });

        it('should not add a TypeHabitation to an array that contains it', () => {
          const typeHabitation: ITypeHabitation = { id: 123 };
          const typeHabitationCollection: ITypeHabitation[] = [
            {
              ...typeHabitation,
            },
            { id: 456 },
          ];
          expectedResult = service.addTypeHabitationToCollectionIfMissing(typeHabitationCollection, typeHabitation);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TypeHabitation to an array that doesn't contain it", () => {
          const typeHabitation: ITypeHabitation = { id: 123 };
          const typeHabitationCollection: ITypeHabitation[] = [{ id: 456 }];
          expectedResult = service.addTypeHabitationToCollectionIfMissing(typeHabitationCollection, typeHabitation);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(typeHabitation);
        });

        it('should add only unique TypeHabitation to an array', () => {
          const typeHabitationArray: ITypeHabitation[] = [{ id: 123 }, { id: 456 }, { id: 47699 }];
          const typeHabitationCollection: ITypeHabitation[] = [{ id: 123 }];
          expectedResult = service.addTypeHabitationToCollectionIfMissing(typeHabitationCollection, ...typeHabitationArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const typeHabitation: ITypeHabitation = { id: 123 };
          const typeHabitation2: ITypeHabitation = { id: 456 };
          expectedResult = service.addTypeHabitationToCollectionIfMissing([], typeHabitation, typeHabitation2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(typeHabitation);
          expect(expectedResult).toContain(typeHabitation2);
        });

        it('should accept null and undefined values', () => {
          const typeHabitation: ITypeHabitation = { id: 123 };
          expectedResult = service.addTypeHabitationToCollectionIfMissing([], null, typeHabitation, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(typeHabitation);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
