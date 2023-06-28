import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAdulto, getAdultoIdentifier } from '../adulto.model';

export type EntityResponseType = HttpResponse<IAdulto>;
export type EntityArrayResponseType = HttpResponse<IAdulto[]>;

@Injectable({ providedIn: 'root' })
export class AdultoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/adultos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(adulto: IAdulto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(adulto);
    return this.http
      .post<IAdulto>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(adulto: IAdulto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(adulto);
    return this.http
      .put<IAdulto>(`${this.resourceUrl}/${getAdultoIdentifier(adulto) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(adulto: IAdulto): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(adulto);
    return this.http
      .patch<IAdulto>(`${this.resourceUrl}/${getAdultoIdentifier(adulto) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IAdulto>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IAdulto[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAdultoToCollectionIfMissing(adultoCollection: IAdulto[], ...adultosToCheck: (IAdulto | null | undefined)[]): IAdulto[] {
    const adultos: IAdulto[] = adultosToCheck.filter(isPresent);
    if (adultos.length > 0) {
      const adultoCollectionIdentifiers = adultoCollection.map(adultoItem => getAdultoIdentifier(adultoItem)!);
      const adultosToAdd = adultos.filter(adultoItem => {
        const adultoIdentifier = getAdultoIdentifier(adultoItem);
        if (adultoIdentifier == null || adultoCollectionIdentifiers.includes(adultoIdentifier)) {
          return false;
        }
        adultoCollectionIdentifiers.push(adultoIdentifier);
        return true;
      });
      return [...adultosToAdd, ...adultoCollection];
    }
    return adultoCollection;
  }

  protected convertDateFromClient(adulto: IAdulto): IAdulto {
    return Object.assign({}, adulto, {
      fechaNacimiento: adulto.fechaNacimiento?.isValid() ? adulto.fechaNacimiento.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.fechaNacimiento = res.body.fechaNacimiento ? dayjs(res.body.fechaNacimiento) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((adulto: IAdulto) => {
        adulto.fechaNacimiento = adulto.fechaNacimiento ? dayjs(adulto.fechaNacimiento) : undefined;
      });
    }
    return res;
  }
}
