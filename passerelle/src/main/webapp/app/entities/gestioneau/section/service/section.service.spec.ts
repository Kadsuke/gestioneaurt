import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISection, Section } from '../section.model';

import { SectionService } from './section.service';

describe('Service Tests', () => {
  describe('Section Service', () => {
    let service: SectionService;
    let httpMock: HttpTestingController;
    let elemDefault: ISection;
    let expectedResult: ISection | ISection[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SectionService);
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

      it('should create a Section', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Section()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Section', () => {
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

      it('should partial update a Section', () => {
        const patchObject = Object.assign(
          {
            libelle: 'BBBBBB',
          },
          new Section()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Section', () => {
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

      it('should delete a Section', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSectionToCollectionIfMissing', () => {
        it('should add a Section to an empty array', () => {
          const section: ISection = { id: 123 };
          expectedResult = service.addSectionToCollectionIfMissing([], section);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(section);
        });

        it('should not add a Section to an array that contains it', () => {
          const section: ISection = { id: 123 };
          const sectionCollection: ISection[] = [
            {
              ...section,
            },
            { id: 456 },
          ];
          expectedResult = service.addSectionToCollectionIfMissing(sectionCollection, section);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Section to an array that doesn't contain it", () => {
          const section: ISection = { id: 123 };
          const sectionCollection: ISection[] = [{ id: 456 }];
          expectedResult = service.addSectionToCollectionIfMissing(sectionCollection, section);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(section);
        });

        it('should add only unique Section to an array', () => {
          const sectionArray: ISection[] = [{ id: 123 }, { id: 456 }, { id: 69524 }];
          const sectionCollection: ISection[] = [{ id: 123 }];
          expectedResult = service.addSectionToCollectionIfMissing(sectionCollection, ...sectionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const section: ISection = { id: 123 };
          const section2: ISection = { id: 456 };
          expectedResult = service.addSectionToCollectionIfMissing([], section, section2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(section);
          expect(expectedResult).toContain(section2);
        });

        it('should accept null and undefined values', () => {
          const section: ISection = { id: 123 };
          expectedResult = service.addSectionToCollectionIfMissing([], null, section, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(section);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
