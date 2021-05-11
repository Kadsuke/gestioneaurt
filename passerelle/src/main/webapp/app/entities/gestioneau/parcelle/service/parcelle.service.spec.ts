import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IParcelle, Parcelle } from '../parcelle.model';

import { ParcelleService } from './parcelle.service';

describe('Service Tests', () => {
  describe('Parcelle Service', () => {
    let service: ParcelleService;
    let httpMock: HttpTestingController;
    let elemDefault: IParcelle;
    let expectedResult: IParcelle | IParcelle[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ParcelleService);
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

      it('should create a Parcelle', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Parcelle()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Parcelle', () => {
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

      it('should partial update a Parcelle', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new Parcelle()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Parcelle', () => {
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

      it('should delete a Parcelle', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addParcelleToCollectionIfMissing', () => {
        it('should add a Parcelle to an empty array', () => {
          const parcelle: IParcelle = { id: 123 };
          expectedResult = service.addParcelleToCollectionIfMissing([], parcelle);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(parcelle);
        });

        it('should not add a Parcelle to an array that contains it', () => {
          const parcelle: IParcelle = { id: 123 };
          const parcelleCollection: IParcelle[] = [
            {
              ...parcelle,
            },
            { id: 456 },
          ];
          expectedResult = service.addParcelleToCollectionIfMissing(parcelleCollection, parcelle);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Parcelle to an array that doesn't contain it", () => {
          const parcelle: IParcelle = { id: 123 };
          const parcelleCollection: IParcelle[] = [{ id: 456 }];
          expectedResult = service.addParcelleToCollectionIfMissing(parcelleCollection, parcelle);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(parcelle);
        });

        it('should add only unique Parcelle to an array', () => {
          const parcelleArray: IParcelle[] = [{ id: 123 }, { id: 456 }, { id: 93845 }];
          const parcelleCollection: IParcelle[] = [{ id: 123 }];
          expectedResult = service.addParcelleToCollectionIfMissing(parcelleCollection, ...parcelleArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const parcelle: IParcelle = { id: 123 };
          const parcelle2: IParcelle = { id: 456 };
          expectedResult = service.addParcelleToCollectionIfMissing([], parcelle, parcelle2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(parcelle);
          expect(expectedResult).toContain(parcelle2);
        });

        it('should accept null and undefined values', () => {
          const parcelle: IParcelle = { id: 123 };
          expectedResult = service.addParcelleToCollectionIfMissing([], null, parcelle, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(parcelle);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
