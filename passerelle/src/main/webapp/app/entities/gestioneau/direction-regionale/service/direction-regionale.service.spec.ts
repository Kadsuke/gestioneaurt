import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDirectionRegionale, DirectionRegionale } from '../direction-regionale.model';

import { DirectionRegionaleService } from './direction-regionale.service';

describe('Service Tests', () => {
  describe('DirectionRegionale Service', () => {
    let service: DirectionRegionaleService;
    let httpMock: HttpTestingController;
    let elemDefault: IDirectionRegionale;
    let expectedResult: IDirectionRegionale | IDirectionRegionale[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(DirectionRegionaleService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        libelle: 'AAAAAAA',
        responsable: 'AAAAAAA',
        contact: 'AAAAAAA',
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

      it('should create a DirectionRegionale', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new DirectionRegionale()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a DirectionRegionale', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            libelle: 'BBBBBB',
            responsable: 'BBBBBB',
            contact: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a DirectionRegionale', () => {
        const patchObject = Object.assign({}, new DirectionRegionale());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of DirectionRegionale', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            libelle: 'BBBBBB',
            responsable: 'BBBBBB',
            contact: 'BBBBBB',
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

      it('should delete a DirectionRegionale', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addDirectionRegionaleToCollectionIfMissing', () => {
        it('should add a DirectionRegionale to an empty array', () => {
          const directionRegionale: IDirectionRegionale = { id: 123 };
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing([], directionRegionale);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(directionRegionale);
        });

        it('should not add a DirectionRegionale to an array that contains it', () => {
          const directionRegionale: IDirectionRegionale = { id: 123 };
          const directionRegionaleCollection: IDirectionRegionale[] = [
            {
              ...directionRegionale,
            },
            { id: 456 },
          ];
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing(directionRegionaleCollection, directionRegionale);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a DirectionRegionale to an array that doesn't contain it", () => {
          const directionRegionale: IDirectionRegionale = { id: 123 };
          const directionRegionaleCollection: IDirectionRegionale[] = [{ id: 456 }];
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing(directionRegionaleCollection, directionRegionale);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(directionRegionale);
        });

        it('should add only unique DirectionRegionale to an array', () => {
          const directionRegionaleArray: IDirectionRegionale[] = [{ id: 123 }, { id: 456 }, { id: 42991 }];
          const directionRegionaleCollection: IDirectionRegionale[] = [{ id: 123 }];
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing(directionRegionaleCollection, ...directionRegionaleArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const directionRegionale: IDirectionRegionale = { id: 123 };
          const directionRegionale2: IDirectionRegionale = { id: 456 };
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing([], directionRegionale, directionRegionale2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(directionRegionale);
          expect(expectedResult).toContain(directionRegionale2);
        });

        it('should accept null and undefined values', () => {
          const directionRegionale: IDirectionRegionale = { id: 123 };
          expectedResult = service.addDirectionRegionaleToCollectionIfMissing([], null, directionRegionale, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(directionRegionale);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
