import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { INinio, Ninio } from '../ninio.model';

import { NinioService } from './ninio.service';

describe('Ninio Service', () => {
  let service: NinioService;
  let httpMock: HttpTestingController;
  let elemDefault: INinio;
  let expectedResult: INinio | INinio[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NinioService);
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

    it('should create a Ninio', () => {
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

      service.create(new Ninio()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ninio', () => {
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

    it('should partial update a Ninio', () => {
      const patchObject = Object.assign(
        {
          nombre: 'BBBBBB',
          apellido: 'BBBBBB',
        },
        new Ninio()
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

    it('should return a list of Ninio', () => {
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

    it('should delete a Ninio', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNinioToCollectionIfMissing', () => {
      it('should add a Ninio to an empty array', () => {
        const ninio: INinio = { id: 123 };
        expectedResult = service.addNinioToCollectionIfMissing([], ninio);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ninio);
      });

      it('should not add a Ninio to an array that contains it', () => {
        const ninio: INinio = { id: 123 };
        const ninioCollection: INinio[] = [
          {
            ...ninio,
          },
          { id: 456 },
        ];
        expectedResult = service.addNinioToCollectionIfMissing(ninioCollection, ninio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ninio to an array that doesn't contain it", () => {
        const ninio: INinio = { id: 123 };
        const ninioCollection: INinio[] = [{ id: 456 }];
        expectedResult = service.addNinioToCollectionIfMissing(ninioCollection, ninio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ninio);
      });

      it('should add only unique Ninio to an array', () => {
        const ninioArray: INinio[] = [{ id: 123 }, { id: 456 }, { id: 94818 }];
        const ninioCollection: INinio[] = [{ id: 123 }];
        expectedResult = service.addNinioToCollectionIfMissing(ninioCollection, ...ninioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ninio: INinio = { id: 123 };
        const ninio2: INinio = { id: 456 };
        expectedResult = service.addNinioToCollectionIfMissing([], ninio, ninio2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ninio);
        expect(expectedResult).toContain(ninio2);
      });

      it('should accept null and undefined values', () => {
        const ninio: INinio = { id: 123 };
        expectedResult = service.addNinioToCollectionIfMissing([], null, ninio, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ninio);
      });

      it('should return initial array if no Ninio is added', () => {
        const ninioCollection: INinio[] = [{ id: 123 }];
        expectedResult = service.addNinioToCollectionIfMissing(ninioCollection, undefined, null);
        expect(expectedResult).toEqual(ninioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
