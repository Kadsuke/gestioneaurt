import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IFicheSuiviOuvrage, FicheSuiviOuvrage } from '../fiche-suivi-ouvrage.model';

import { FicheSuiviOuvrageService } from './fiche-suivi-ouvrage.service';

describe('Service Tests', () => {
  describe('FicheSuiviOuvrage Service', () => {
    let service: FicheSuiviOuvrageService;
    let httpMock: HttpTestingController;
    let elemDefault: IFicheSuiviOuvrage;
    let expectedResult: IFicheSuiviOuvrage | IFicheSuiviOuvrage[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(FicheSuiviOuvrageService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        prjAppuis: 'AAAAAAA',
        nomBenef: 'AAAAAAA',
        prenomBenef: 'AAAAAAA',
        professionBenef: 'AAAAAAA',
        nbUsagers: 0,
        contacts: 'AAAAAAA',
        longitude: 0,
        latitude: 0,
        dateRemiseDevis: currentDate,
        dateDebutTravaux: currentDate,
        dateFinTravaux: currentDate,
        rue: 'AAAAAAA',
        porte: 'AAAAAAA',
        coutMenage: 'AAAAAAA',
        subvOnea: 0,
        subvProjet: 0,
        autreSubv: 0,
        toles: 0,
        animateur: 'AAAAAAA',
        superviseur: 'AAAAAAA',
        controleur: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            dateRemiseDevis: currentDate.format(DATE_TIME_FORMAT),
            dateDebutTravaux: currentDate.format(DATE_TIME_FORMAT),
            dateFinTravaux: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a FicheSuiviOuvrage', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            dateRemiseDevis: currentDate.format(DATE_TIME_FORMAT),
            dateDebutTravaux: currentDate.format(DATE_TIME_FORMAT),
            dateFinTravaux: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateRemiseDevis: currentDate,
            dateDebutTravaux: currentDate,
            dateFinTravaux: currentDate,
          },
          returnedFromService
        );

        service.create(new FicheSuiviOuvrage()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a FicheSuiviOuvrage', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            prjAppuis: 'BBBBBB',
            nomBenef: 'BBBBBB',
            prenomBenef: 'BBBBBB',
            professionBenef: 'BBBBBB',
            nbUsagers: 1,
            contacts: 'BBBBBB',
            longitude: 1,
            latitude: 1,
            dateRemiseDevis: currentDate.format(DATE_TIME_FORMAT),
            dateDebutTravaux: currentDate.format(DATE_TIME_FORMAT),
            dateFinTravaux: currentDate.format(DATE_TIME_FORMAT),
            rue: 'BBBBBB',
            porte: 'BBBBBB',
            coutMenage: 'BBBBBB',
            subvOnea: 1,
            subvProjet: 1,
            autreSubv: 1,
            toles: 1,
            animateur: 'BBBBBB',
            superviseur: 'BBBBBB',
            controleur: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateRemiseDevis: currentDate,
            dateDebutTravaux: currentDate,
            dateFinTravaux: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a FicheSuiviOuvrage', () => {
        const patchObject = Object.assign(
          {
            nbUsagers: 1,
            contacts: 'BBBBBB',
            dateRemiseDevis: currentDate.format(DATE_TIME_FORMAT),
            dateFinTravaux: currentDate.format(DATE_TIME_FORMAT),
            rue: 'BBBBBB',
            coutMenage: 'BBBBBB',
            subvProjet: 1,
            autreSubv: 1,
            toles: 1,
            animateur: 'BBBBBB',
            superviseur: 'BBBBBB',
            controleur: 'BBBBBB',
          },
          new FicheSuiviOuvrage()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            dateRemiseDevis: currentDate,
            dateDebutTravaux: currentDate,
            dateFinTravaux: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of FicheSuiviOuvrage', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            prjAppuis: 'BBBBBB',
            nomBenef: 'BBBBBB',
            prenomBenef: 'BBBBBB',
            professionBenef: 'BBBBBB',
            nbUsagers: 1,
            contacts: 'BBBBBB',
            longitude: 1,
            latitude: 1,
            dateRemiseDevis: currentDate.format(DATE_TIME_FORMAT),
            dateDebutTravaux: currentDate.format(DATE_TIME_FORMAT),
            dateFinTravaux: currentDate.format(DATE_TIME_FORMAT),
            rue: 'BBBBBB',
            porte: 'BBBBBB',
            coutMenage: 'BBBBBB',
            subvOnea: 1,
            subvProjet: 1,
            autreSubv: 1,
            toles: 1,
            animateur: 'BBBBBB',
            superviseur: 'BBBBBB',
            controleur: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            dateRemiseDevis: currentDate,
            dateDebutTravaux: currentDate,
            dateFinTravaux: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a FicheSuiviOuvrage', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addFicheSuiviOuvrageToCollectionIfMissing', () => {
        it('should add a FicheSuiviOuvrage to an empty array', () => {
          const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 123 };
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing([], ficheSuiviOuvrage);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(ficheSuiviOuvrage);
        });

        it('should not add a FicheSuiviOuvrage to an array that contains it', () => {
          const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 123 };
          const ficheSuiviOuvrageCollection: IFicheSuiviOuvrage[] = [
            {
              ...ficheSuiviOuvrage,
            },
            { id: 456 },
          ];
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing(ficheSuiviOuvrageCollection, ficheSuiviOuvrage);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a FicheSuiviOuvrage to an array that doesn't contain it", () => {
          const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 123 };
          const ficheSuiviOuvrageCollection: IFicheSuiviOuvrage[] = [{ id: 456 }];
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing(ficheSuiviOuvrageCollection, ficheSuiviOuvrage);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(ficheSuiviOuvrage);
        });

        it('should add only unique FicheSuiviOuvrage to an array', () => {
          const ficheSuiviOuvrageArray: IFicheSuiviOuvrage[] = [{ id: 123 }, { id: 456 }, { id: 33187 }];
          const ficheSuiviOuvrageCollection: IFicheSuiviOuvrage[] = [{ id: 123 }];
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing(ficheSuiviOuvrageCollection, ...ficheSuiviOuvrageArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 123 };
          const ficheSuiviOuvrage2: IFicheSuiviOuvrage = { id: 456 };
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing([], ficheSuiviOuvrage, ficheSuiviOuvrage2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(ficheSuiviOuvrage);
          expect(expectedResult).toContain(ficheSuiviOuvrage2);
        });

        it('should accept null and undefined values', () => {
          const ficheSuiviOuvrage: IFicheSuiviOuvrage = { id: 123 };
          expectedResult = service.addFicheSuiviOuvrageToCollectionIfMissing([], null, ficheSuiviOuvrage, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(ficheSuiviOuvrage);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
