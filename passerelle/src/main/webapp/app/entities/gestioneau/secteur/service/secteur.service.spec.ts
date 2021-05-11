import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISecteur, Secteur } from '../secteur.model';

import { SecteurService } from './secteur.service';

describe('Service Tests', () => {
  describe('Secteur Service', () => {
    let service: SecteurService;
    let httpMock: HttpTestingController;
    let elemDefault: ISecteur;
    let expectedResult: ISecteur | ISecteur[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SecteurService);
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

      it('should create a Secteur', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Secteur()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Secteur', () => {
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

      it('should partial update a Secteur', () => {
        const patchObject = Object.assign({}, new Secteur());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Secteur', () => {
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

      it('should delete a Secteur', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSecteurToCollectionIfMissing', () => {
        it('should add a Secteur to an empty array', () => {
          const secteur: ISecteur = { id: 123 };
          expectedResult = service.addSecteurToCollectionIfMissing([], secteur);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(secteur);
        });

        it('should not add a Secteur to an array that contains it', () => {
          const secteur: ISecteur = { id: 123 };
          const secteurCollection: ISecteur[] = [
            {
              ...secteur,
            },
            { id: 456 },
          ];
          expectedResult = service.addSecteurToCollectionIfMissing(secteurCollection, secteur);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Secteur to an array that doesn't contain it", () => {
          const secteur: ISecteur = { id: 123 };
          const secteurCollection: ISecteur[] = [{ id: 456 }];
          expectedResult = service.addSecteurToCollectionIfMissing(secteurCollection, secteur);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(secteur);
        });

        it('should add only unique Secteur to an array', () => {
          const secteurArray: ISecteur[] = [{ id: 123 }, { id: 456 }, { id: 32127 }];
          const secteurCollection: ISecteur[] = [{ id: 123 }];
          expectedResult = service.addSecteurToCollectionIfMissing(secteurCollection, ...secteurArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const secteur: ISecteur = { id: 123 };
          const secteur2: ISecteur = { id: 456 };
          expectedResult = service.addSecteurToCollectionIfMissing([], secteur, secteur2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(secteur);
          expect(expectedResult).toContain(secteur2);
        });

        it('should accept null and undefined values', () => {
          const secteur: ISecteur = { id: 123 };
          expectedResult = service.addSecteurToCollectionIfMissing([], null, secteur, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(secteur);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
