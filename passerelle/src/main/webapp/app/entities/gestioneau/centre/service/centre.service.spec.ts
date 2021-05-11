import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICentre, Centre } from '../centre.model';

import { CentreService } from './centre.service';

describe('Service Tests', () => {
  describe('Centre Service', () => {
    let service: CentreService;
    let httpMock: HttpTestingController;
    let elemDefault: ICentre;
    let expectedResult: ICentre | ICentre[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CentreService);
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

      it('should create a Centre', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Centre()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Centre', () => {
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

      it('should partial update a Centre', () => {
        const patchObject = Object.assign({}, new Centre());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Centre', () => {
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

      it('should delete a Centre', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCentreToCollectionIfMissing', () => {
        it('should add a Centre to an empty array', () => {
          const centre: ICentre = { id: 123 };
          expectedResult = service.addCentreToCollectionIfMissing([], centre);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(centre);
        });

        it('should not add a Centre to an array that contains it', () => {
          const centre: ICentre = { id: 123 };
          const centreCollection: ICentre[] = [
            {
              ...centre,
            },
            { id: 456 },
          ];
          expectedResult = service.addCentreToCollectionIfMissing(centreCollection, centre);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Centre to an array that doesn't contain it", () => {
          const centre: ICentre = { id: 123 };
          const centreCollection: ICentre[] = [{ id: 456 }];
          expectedResult = service.addCentreToCollectionIfMissing(centreCollection, centre);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(centre);
        });

        it('should add only unique Centre to an array', () => {
          const centreArray: ICentre[] = [{ id: 123 }, { id: 456 }, { id: 36117 }];
          const centreCollection: ICentre[] = [{ id: 123 }];
          expectedResult = service.addCentreToCollectionIfMissing(centreCollection, ...centreArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const centre: ICentre = { id: 123 };
          const centre2: ICentre = { id: 456 };
          expectedResult = service.addCentreToCollectionIfMissing([], centre, centre2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(centre);
          expect(expectedResult).toContain(centre2);
        });

        it('should accept null and undefined values', () => {
          const centre: ICentre = { id: 123 };
          expectedResult = service.addCentreToCollectionIfMissing([], null, centre, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(centre);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
