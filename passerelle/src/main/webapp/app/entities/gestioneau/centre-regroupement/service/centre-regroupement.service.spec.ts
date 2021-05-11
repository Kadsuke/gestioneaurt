import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICentreRegroupement, CentreRegroupement } from '../centre-regroupement.model';

import { CentreRegroupementService } from './centre-regroupement.service';

describe('Service Tests', () => {
  describe('CentreRegroupement Service', () => {
    let service: CentreRegroupementService;
    let httpMock: HttpTestingController;
    let elemDefault: ICentreRegroupement;
    let expectedResult: ICentreRegroupement | ICentreRegroupement[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CentreRegroupementService);
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

      it('should create a CentreRegroupement', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new CentreRegroupement()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a CentreRegroupement', () => {
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

      it('should partial update a CentreRegroupement', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new CentreRegroupement()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of CentreRegroupement', () => {
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

      it('should delete a CentreRegroupement', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCentreRegroupementToCollectionIfMissing', () => {
        it('should add a CentreRegroupement to an empty array', () => {
          const centreRegroupement: ICentreRegroupement = { id: 123 };
          expectedResult = service.addCentreRegroupementToCollectionIfMissing([], centreRegroupement);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(centreRegroupement);
        });

        it('should not add a CentreRegroupement to an array that contains it', () => {
          const centreRegroupement: ICentreRegroupement = { id: 123 };
          const centreRegroupementCollection: ICentreRegroupement[] = [
            {
              ...centreRegroupement,
            },
            { id: 456 },
          ];
          expectedResult = service.addCentreRegroupementToCollectionIfMissing(centreRegroupementCollection, centreRegroupement);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a CentreRegroupement to an array that doesn't contain it", () => {
          const centreRegroupement: ICentreRegroupement = { id: 123 };
          const centreRegroupementCollection: ICentreRegroupement[] = [{ id: 456 }];
          expectedResult = service.addCentreRegroupementToCollectionIfMissing(centreRegroupementCollection, centreRegroupement);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(centreRegroupement);
        });

        it('should add only unique CentreRegroupement to an array', () => {
          const centreRegroupementArray: ICentreRegroupement[] = [{ id: 123 }, { id: 456 }, { id: 95129 }];
          const centreRegroupementCollection: ICentreRegroupement[] = [{ id: 123 }];
          expectedResult = service.addCentreRegroupementToCollectionIfMissing(centreRegroupementCollection, ...centreRegroupementArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const centreRegroupement: ICentreRegroupement = { id: 123 };
          const centreRegroupement2: ICentreRegroupement = { id: 456 };
          expectedResult = service.addCentreRegroupementToCollectionIfMissing([], centreRegroupement, centreRegroupement2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(centreRegroupement);
          expect(expectedResult).toContain(centreRegroupement2);
        });

        it('should accept null and undefined values', () => {
          const centreRegroupement: ICentreRegroupement = { id: 123 };
          expectedResult = service.addCentreRegroupementToCollectionIfMissing([], null, centreRegroupement, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(centreRegroupement);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
