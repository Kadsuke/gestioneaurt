import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INatureOuvrage, NatureOuvrage } from '../nature-ouvrage.model';

import { NatureOuvrageService } from './nature-ouvrage.service';

describe('Service Tests', () => {
  describe('NatureOuvrage Service', () => {
    let service: NatureOuvrageService;
    let httpMock: HttpTestingController;
    let elemDefault: INatureOuvrage;
    let expectedResult: INatureOuvrage | INatureOuvrage[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(NatureOuvrageService);
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

      it('should create a NatureOuvrage', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new NatureOuvrage()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a NatureOuvrage', () => {
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

      it('should partial update a NatureOuvrage', () => {
        const patchObject = Object.assign({}, new NatureOuvrage());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of NatureOuvrage', () => {
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

      it('should delete a NatureOuvrage', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addNatureOuvrageToCollectionIfMissing', () => {
        it('should add a NatureOuvrage to an empty array', () => {
          const natureOuvrage: INatureOuvrage = { id: 123 };
          expectedResult = service.addNatureOuvrageToCollectionIfMissing([], natureOuvrage);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(natureOuvrage);
        });

        it('should not add a NatureOuvrage to an array that contains it', () => {
          const natureOuvrage: INatureOuvrage = { id: 123 };
          const natureOuvrageCollection: INatureOuvrage[] = [
            {
              ...natureOuvrage,
            },
            { id: 456 },
          ];
          expectedResult = service.addNatureOuvrageToCollectionIfMissing(natureOuvrageCollection, natureOuvrage);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a NatureOuvrage to an array that doesn't contain it", () => {
          const natureOuvrage: INatureOuvrage = { id: 123 };
          const natureOuvrageCollection: INatureOuvrage[] = [{ id: 456 }];
          expectedResult = service.addNatureOuvrageToCollectionIfMissing(natureOuvrageCollection, natureOuvrage);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(natureOuvrage);
        });

        it('should add only unique NatureOuvrage to an array', () => {
          const natureOuvrageArray: INatureOuvrage[] = [{ id: 123 }, { id: 456 }, { id: 13536 }];
          const natureOuvrageCollection: INatureOuvrage[] = [{ id: 123 }];
          expectedResult = service.addNatureOuvrageToCollectionIfMissing(natureOuvrageCollection, ...natureOuvrageArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const natureOuvrage: INatureOuvrage = { id: 123 };
          const natureOuvrage2: INatureOuvrage = { id: 456 };
          expectedResult = service.addNatureOuvrageToCollectionIfMissing([], natureOuvrage, natureOuvrage2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(natureOuvrage);
          expect(expectedResult).toContain(natureOuvrage2);
        });

        it('should accept null and undefined values', () => {
          const natureOuvrage: INatureOuvrage = { id: 123 };
          expectedResult = service.addNatureOuvrageToCollectionIfMissing([], null, natureOuvrage, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(natureOuvrage);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
