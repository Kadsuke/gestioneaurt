import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISourceApprovEp, SourceApprovEp } from '../source-approv-ep.model';

import { SourceApprovEpService } from './source-approv-ep.service';

describe('Service Tests', () => {
  describe('SourceApprovEp Service', () => {
    let service: SourceApprovEpService;
    let httpMock: HttpTestingController;
    let elemDefault: ISourceApprovEp;
    let expectedResult: ISourceApprovEp | ISourceApprovEp[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SourceApprovEpService);
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

      it('should create a SourceApprovEp', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new SourceApprovEp()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a SourceApprovEp', () => {
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

      it('should partial update a SourceApprovEp', () => {
        const patchObject = Object.assign({}, new SourceApprovEp());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of SourceApprovEp', () => {
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

      it('should delete a SourceApprovEp', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSourceApprovEpToCollectionIfMissing', () => {
        it('should add a SourceApprovEp to an empty array', () => {
          const sourceApprovEp: ISourceApprovEp = { id: 123 };
          expectedResult = service.addSourceApprovEpToCollectionIfMissing([], sourceApprovEp);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(sourceApprovEp);
        });

        it('should not add a SourceApprovEp to an array that contains it', () => {
          const sourceApprovEp: ISourceApprovEp = { id: 123 };
          const sourceApprovEpCollection: ISourceApprovEp[] = [
            {
              ...sourceApprovEp,
            },
            { id: 456 },
          ];
          expectedResult = service.addSourceApprovEpToCollectionIfMissing(sourceApprovEpCollection, sourceApprovEp);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a SourceApprovEp to an array that doesn't contain it", () => {
          const sourceApprovEp: ISourceApprovEp = { id: 123 };
          const sourceApprovEpCollection: ISourceApprovEp[] = [{ id: 456 }];
          expectedResult = service.addSourceApprovEpToCollectionIfMissing(sourceApprovEpCollection, sourceApprovEp);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(sourceApprovEp);
        });

        it('should add only unique SourceApprovEp to an array', () => {
          const sourceApprovEpArray: ISourceApprovEp[] = [{ id: 123 }, { id: 456 }, { id: 92263 }];
          const sourceApprovEpCollection: ISourceApprovEp[] = [{ id: 123 }];
          expectedResult = service.addSourceApprovEpToCollectionIfMissing(sourceApprovEpCollection, ...sourceApprovEpArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const sourceApprovEp: ISourceApprovEp = { id: 123 };
          const sourceApprovEp2: ISourceApprovEp = { id: 456 };
          expectedResult = service.addSourceApprovEpToCollectionIfMissing([], sourceApprovEp, sourceApprovEp2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(sourceApprovEp);
          expect(expectedResult).toContain(sourceApprovEp2);
        });

        it('should accept null and undefined values', () => {
          const sourceApprovEp: ISourceApprovEp = { id: 123 };
          expectedResult = service.addSourceApprovEpToCollectionIfMissing([], null, sourceApprovEp, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(sourceApprovEp);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
