import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAnnee, Annee } from '../annee.model';

import { AnneeService } from './annee.service';

describe('Service Tests', () => {
  describe('Annee Service', () => {
    let service: AnneeService;
    let httpMock: HttpTestingController;
    let elemDefault: IAnnee;
    let expectedResult: IAnnee | IAnnee[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(AnneeService);
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

      it('should create a Annee', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Annee()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Annee', () => {
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

      it('should partial update a Annee', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new Annee()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Annee', () => {
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

      it('should delete a Annee', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addAnneeToCollectionIfMissing', () => {
        it('should add a Annee to an empty array', () => {
          const annee: IAnnee = { id: 123 };
          expectedResult = service.addAnneeToCollectionIfMissing([], annee);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(annee);
        });

        it('should not add a Annee to an array that contains it', () => {
          const annee: IAnnee = { id: 123 };
          const anneeCollection: IAnnee[] = [
            {
              ...annee,
            },
            { id: 456 },
          ];
          expectedResult = service.addAnneeToCollectionIfMissing(anneeCollection, annee);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Annee to an array that doesn't contain it", () => {
          const annee: IAnnee = { id: 123 };
          const anneeCollection: IAnnee[] = [{ id: 456 }];
          expectedResult = service.addAnneeToCollectionIfMissing(anneeCollection, annee);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(annee);
        });

        it('should add only unique Annee to an array', () => {
          const anneeArray: IAnnee[] = [{ id: 123 }, { id: 456 }, { id: 96117 }];
          const anneeCollection: IAnnee[] = [{ id: 123 }];
          expectedResult = service.addAnneeToCollectionIfMissing(anneeCollection, ...anneeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const annee: IAnnee = { id: 123 };
          const annee2: IAnnee = { id: 456 };
          expectedResult = service.addAnneeToCollectionIfMissing([], annee, annee2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(annee);
          expect(expectedResult).toContain(annee2);
        });

        it('should accept null and undefined values', () => {
          const annee: IAnnee = { id: 123 };
          expectedResult = service.addAnneeToCollectionIfMissing([], null, annee, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(annee);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
