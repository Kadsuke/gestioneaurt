import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILocalite, Localite } from '../localite.model';

import { LocaliteService } from './localite.service';

describe('Service Tests', () => {
  describe('Localite Service', () => {
    let service: LocaliteService;
    let httpMock: HttpTestingController;
    let elemDefault: ILocalite;
    let expectedResult: ILocalite | ILocalite[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LocaliteService);
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

      it('should create a Localite', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Localite()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Localite', () => {
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

      it('should partial update a Localite', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new Localite()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Localite', () => {
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

      it('should delete a Localite', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addLocaliteToCollectionIfMissing', () => {
        it('should add a Localite to an empty array', () => {
          const localite: ILocalite = { id: 123 };
          expectedResult = service.addLocaliteToCollectionIfMissing([], localite);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(localite);
        });

        it('should not add a Localite to an array that contains it', () => {
          const localite: ILocalite = { id: 123 };
          const localiteCollection: ILocalite[] = [
            {
              ...localite,
            },
            { id: 456 },
          ];
          expectedResult = service.addLocaliteToCollectionIfMissing(localiteCollection, localite);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Localite to an array that doesn't contain it", () => {
          const localite: ILocalite = { id: 123 };
          const localiteCollection: ILocalite[] = [{ id: 456 }];
          expectedResult = service.addLocaliteToCollectionIfMissing(localiteCollection, localite);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(localite);
        });

        it('should add only unique Localite to an array', () => {
          const localiteArray: ILocalite[] = [{ id: 123 }, { id: 456 }, { id: 29163 }];
          const localiteCollection: ILocalite[] = [{ id: 123 }];
          expectedResult = service.addLocaliteToCollectionIfMissing(localiteCollection, ...localiteArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const localite: ILocalite = { id: 123 };
          const localite2: ILocalite = { id: 456 };
          expectedResult = service.addLocaliteToCollectionIfMissing([], localite, localite2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(localite);
          expect(expectedResult).toContain(localite2);
        });

        it('should accept null and undefined values', () => {
          const localite: ILocalite = { id: 123 };
          expectedResult = service.addLocaliteToCollectionIfMissing([], null, localite, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(localite);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
