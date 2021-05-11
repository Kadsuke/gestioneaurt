import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IModeEvacExcreta, ModeEvacExcreta } from '../mode-evac-excreta.model';

import { ModeEvacExcretaService } from './mode-evac-excreta.service';

describe('Service Tests', () => {
  describe('ModeEvacExcreta Service', () => {
    let service: ModeEvacExcretaService;
    let httpMock: HttpTestingController;
    let elemDefault: IModeEvacExcreta;
    let expectedResult: IModeEvacExcreta | IModeEvacExcreta[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(ModeEvacExcretaService);
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

      it('should create a ModeEvacExcreta', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new ModeEvacExcreta()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a ModeEvacExcreta', () => {
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

      it('should partial update a ModeEvacExcreta', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new ModeEvacExcreta()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of ModeEvacExcreta', () => {
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

      it('should delete a ModeEvacExcreta', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addModeEvacExcretaToCollectionIfMissing', () => {
        it('should add a ModeEvacExcreta to an empty array', () => {
          const modeEvacExcreta: IModeEvacExcreta = { id: 123 };
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing([], modeEvacExcreta);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(modeEvacExcreta);
        });

        it('should not add a ModeEvacExcreta to an array that contains it', () => {
          const modeEvacExcreta: IModeEvacExcreta = { id: 123 };
          const modeEvacExcretaCollection: IModeEvacExcreta[] = [
            {
              ...modeEvacExcreta,
            },
            { id: 456 },
          ];
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing(modeEvacExcretaCollection, modeEvacExcreta);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a ModeEvacExcreta to an array that doesn't contain it", () => {
          const modeEvacExcreta: IModeEvacExcreta = { id: 123 };
          const modeEvacExcretaCollection: IModeEvacExcreta[] = [{ id: 456 }];
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing(modeEvacExcretaCollection, modeEvacExcreta);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(modeEvacExcreta);
        });

        it('should add only unique ModeEvacExcreta to an array', () => {
          const modeEvacExcretaArray: IModeEvacExcreta[] = [{ id: 123 }, { id: 456 }, { id: 46233 }];
          const modeEvacExcretaCollection: IModeEvacExcreta[] = [{ id: 123 }];
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing(modeEvacExcretaCollection, ...modeEvacExcretaArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const modeEvacExcreta: IModeEvacExcreta = { id: 123 };
          const modeEvacExcreta2: IModeEvacExcreta = { id: 456 };
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing([], modeEvacExcreta, modeEvacExcreta2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(modeEvacExcreta);
          expect(expectedResult).toContain(modeEvacExcreta2);
        });

        it('should accept null and undefined values', () => {
          const modeEvacExcreta: IModeEvacExcreta = { id: 123 };
          expectedResult = service.addModeEvacExcretaToCollectionIfMissing([], null, modeEvacExcreta, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(modeEvacExcreta);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
