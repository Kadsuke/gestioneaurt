import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPrevision, Prevision } from '../prevision.model';

import { PrevisionService } from './prevision.service';

describe('Service Tests', () => {
  describe('Prevision Service', () => {
    let service: PrevisionService;
    let httpMock: HttpTestingController;
    let elemDefault: IPrevision;
    let expectedResult: IPrevision | IPrevision[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PrevisionService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        nbLatrine: 0,
        nbPuisard: 0,
        nbPublic: 0,
        nbScolaire: 0,
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

      it('should create a Prevision', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Prevision()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Prevision', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            nbLatrine: 1,
            nbPuisard: 1,
            nbPublic: 1,
            nbScolaire: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Prevision', () => {
        const patchObject = Object.assign(
          {
            nbLatrine: 1,
            nbPuisard: 1,
            nbPublic: 1,
            nbScolaire: 1,
          },
          new Prevision()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Prevision', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            nbLatrine: 1,
            nbPuisard: 1,
            nbPublic: 1,
            nbScolaire: 1,
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

      it('should delete a Prevision', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPrevisionToCollectionIfMissing', () => {
        it('should add a Prevision to an empty array', () => {
          const prevision: IPrevision = { id: 123 };
          expectedResult = service.addPrevisionToCollectionIfMissing([], prevision);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(prevision);
        });

        it('should not add a Prevision to an array that contains it', () => {
          const prevision: IPrevision = { id: 123 };
          const previsionCollection: IPrevision[] = [
            {
              ...prevision,
            },
            { id: 456 },
          ];
          expectedResult = service.addPrevisionToCollectionIfMissing(previsionCollection, prevision);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Prevision to an array that doesn't contain it", () => {
          const prevision: IPrevision = { id: 123 };
          const previsionCollection: IPrevision[] = [{ id: 456 }];
          expectedResult = service.addPrevisionToCollectionIfMissing(previsionCollection, prevision);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(prevision);
        });

        it('should add only unique Prevision to an array', () => {
          const previsionArray: IPrevision[] = [{ id: 123 }, { id: 456 }, { id: 87948 }];
          const previsionCollection: IPrevision[] = [{ id: 123 }];
          expectedResult = service.addPrevisionToCollectionIfMissing(previsionCollection, ...previsionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const prevision: IPrevision = { id: 123 };
          const prevision2: IPrevision = { id: 456 };
          expectedResult = service.addPrevisionToCollectionIfMissing([], prevision, prevision2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(prevision);
          expect(expectedResult).toContain(prevision2);
        });

        it('should accept null and undefined values', () => {
          const prevision: IPrevision = { id: 123 };
          expectedResult = service.addPrevisionToCollectionIfMissing([], null, prevision, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(prevision);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
