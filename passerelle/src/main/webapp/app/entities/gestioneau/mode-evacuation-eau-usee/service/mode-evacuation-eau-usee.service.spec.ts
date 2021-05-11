import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IModeEvacuationEauUsee, ModeEvacuationEauUsee } from '../mode-evacuation-eau-usee.model';

import { ModeEvacuationEauUseeService } from './mode-evacuation-eau-usee.service';

describe('Service Tests', () => {
  describe('ModeEvacuationEauUsee Service', () => {
    let service: ModeEvacuationEauUseeService;
    let httpMock: HttpTestingController;
    let elemDefault: IModeEvacuationEauUsee;
    let expectedResult: IModeEvacuationEauUsee | IModeEvacuationEauUsee[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ModeEvacuationEauUseeService);
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

      it('should create a ModeEvacuationEauUsee', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new ModeEvacuationEauUsee()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ModeEvacuationEauUsee', () => {
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

      it('should partial update a ModeEvacuationEauUsee', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new ModeEvacuationEauUsee()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of ModeEvacuationEauUsee', () => {
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

      it('should delete a ModeEvacuationEauUsee', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addModeEvacuationEauUseeToCollectionIfMissing', () => {
        it('should add a ModeEvacuationEauUsee to an empty array', () => {
          const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 123 };
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing([], modeEvacuationEauUsee);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(modeEvacuationEauUsee);
        });

        it('should not add a ModeEvacuationEauUsee to an array that contains it', () => {
          const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 123 };
          const modeEvacuationEauUseeCollection: IModeEvacuationEauUsee[] = [
            {
              ...modeEvacuationEauUsee,
            },
            { id: 456 },
          ];
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing(modeEvacuationEauUseeCollection, modeEvacuationEauUsee);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ModeEvacuationEauUsee to an array that doesn't contain it", () => {
          const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 123 };
          const modeEvacuationEauUseeCollection: IModeEvacuationEauUsee[] = [{ id: 456 }];
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing(modeEvacuationEauUseeCollection, modeEvacuationEauUsee);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(modeEvacuationEauUsee);
        });

        it('should add only unique ModeEvacuationEauUsee to an array', () => {
          const modeEvacuationEauUseeArray: IModeEvacuationEauUsee[] = [{ id: 123 }, { id: 456 }, { id: 48691 }];
          const modeEvacuationEauUseeCollection: IModeEvacuationEauUsee[] = [{ id: 123 }];
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing(
            modeEvacuationEauUseeCollection,
            ...modeEvacuationEauUseeArray
          );
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 123 };
          const modeEvacuationEauUsee2: IModeEvacuationEauUsee = { id: 456 };
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing([], modeEvacuationEauUsee, modeEvacuationEauUsee2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(modeEvacuationEauUsee);
          expect(expectedResult).toContain(modeEvacuationEauUsee2);
        });

        it('should accept null and undefined values', () => {
          const modeEvacuationEauUsee: IModeEvacuationEauUsee = { id: 123 };
          expectedResult = service.addModeEvacuationEauUseeToCollectionIfMissing([], null, modeEvacuationEauUsee, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(modeEvacuationEauUsee);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
