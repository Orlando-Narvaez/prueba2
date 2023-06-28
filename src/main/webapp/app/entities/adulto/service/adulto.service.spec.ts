import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IAdulto, Adulto } from '../adulto.model';

import { AdultoService } from './adulto.service';

describe('Adulto Service', () => {
  let service: AdultoService;
  let httpMock: HttpTestingController;
  let elemDefault: IAdulto;
  let expectedResult: IAdulto | IAdulto[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AdultoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      nombre: 'AAAAAAA',
      apellido: 'AAAAAAA',
      edad: 0,
      fechaNacimiento: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          fechaNacimiento: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Adulto', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          fechaNacimiento: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaNacimiento: currentDate,
        },
        returnedFromService
      );

      service.create(new Adulto()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Adulto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombre: 'BBBBBB',
          apellido: 'BBBBBB',
          edad: 1,
          fechaNacimiento: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaNacimiento: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Adulto', () => {
      const patchObject = Object.assign(
        {
          edad: 1,
        },
        new Adulto()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          fechaNacimiento: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Adulto', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombre: 'BBBBBB',
          apellido: 'BBBBBB',
          edad: 1,
          fechaNacimiento: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaNacimiento: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Adulto', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAdultoToCollectionIfMissing', () => {
      it('should add a Adulto to an empty array', () => {
        const adulto: IAdulto = { id: 123 };
        expectedResult = service.addAdultoToCollectionIfMissing([], adulto);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(adulto);
      });

      it('should not add a Adulto to an array that contains it', () => {
        const adulto: IAdulto = { id: 123 };
        const adultoCollection: IAdulto[] = [
          {
            ...adulto,
          },
          { id: 456 },
        ];
        expectedResult = service.addAdultoToCollectionIfMissing(adultoCollection, adulto);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Adulto to an array that doesn't contain it", () => {
        const adulto: IAdulto = { id: 123 };
        const adultoCollection: IAdulto[] = [{ id: 456 }];
        expectedResult = service.addAdultoToCollectionIfMissing(adultoCollection, adulto);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(adulto);
      });

      it('should add only unique Adulto to an array', () => {
        const adultoArray: IAdulto[] = [{ id: 123 }, { id: 456 }, { id: 28384 }];
        const adultoCollection: IAdulto[] = [{ id: 123 }];
        expectedResult = service.addAdultoToCollectionIfMissing(adultoCollection, ...adultoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const adulto: IAdulto = { id: 123 };
        const adulto2: IAdulto = { id: 456 };
        expectedResult = service.addAdultoToCollectionIfMissing([], adulto, adulto2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(adulto);
        expect(expectedResult).toContain(adulto2);
      });

      it('should accept null and undefined values', () => {
        const adulto: IAdulto = { id: 123 };
        expectedResult = service.addAdultoToCollectionIfMissing([], null, adulto, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(adulto);
      });

      it('should return initial array if no Adulto is added', () => {
        const adultoCollection: IAdulto[] = [{ id: 123 }];
        expectedResult = service.addAdultoToCollectionIfMissing(adultoCollection, undefined, null);
        expect(expectedResult).toEqual(adultoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
