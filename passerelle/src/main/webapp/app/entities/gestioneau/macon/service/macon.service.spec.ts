import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMacon, Macon } from '../macon.model';

import { MaconService } from './macon.service';

describe('Service Tests', () => {
  describe('Macon Service', () => {
    let service: MaconService;
    let httpMock: HttpTestingController;
    let elemDefault: IMacon;
    let expectedResult: IMacon | IMacon[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(MaconService);
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

      it('should create a Macon', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Macon()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Macon', () => {
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

      it('should partial update a Macon', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new Macon()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Macon', () => {
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

      it('should delete a Macon', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addMaconToCollectionIfMissing', () => {
        it('should add a Macon to an empty array', () => {
          const macon: IMacon = { id: 123 };
          expectedResult = service.addMaconToCollectionIfMissing([], macon);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(macon);
        });

        it('should not add a Macon to an array that contains it', () => {
          const macon: IMacon = { id: 123 };
          const maconCollection: IMacon[] = [
            {
              ...macon,
            },
            { id: 456 },
          ];
          expectedResult = service.addMaconToCollectionIfMissing(maconCollection, macon);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Macon to an array that doesn't contain it", () => {
          const macon: IMacon = { id: 123 };
          const maconCollection: IMacon[] = [{ id: 456 }];
          expectedResult = service.addMaconToCollectionIfMissing(maconCollection, macon);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(macon);
        });

        it('should add only unique Macon to an array', () => {
          const maconArray: IMacon[] = [{ id: 123 }, { id: 456 }, { id: 64956 }];
          const maconCollection: IMacon[] = [{ id: 123 }];
          expectedResult = service.addMaconToCollectionIfMissing(maconCollection, ...maconArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const macon: IMacon = { id: 123 };
          const macon2: IMacon = { id: 456 };
          expectedResult = service.addMaconToCollectionIfMissing([], macon, macon2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(macon);
          expect(expectedResult).toContain(macon2);
        });

        it('should accept null and undefined values', () => {
          const macon: IMacon = { id: 123 };
          expectedResult = service.addMaconToCollectionIfMissing([], null, macon, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(macon);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
