import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INinio, getNinioIdentifier } from '../ninio.model';

export type EntityResponseType = HttpResponse<INinio>;
export type EntityArrayResponseType = HttpResponse<INinio[]>;

@Injectable({ providedIn: 'root' })
export class NinioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ninios');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ninio: INinio): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ninio);
    return this.http
      .post<INinio>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(ninio: INinio): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ninio);
    return this.http
      .put<INinio>(`${this.resourceUrl}/${getNinioIdentifier(ninio) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(ninio: INinio): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ninio);
    return this.http
      .patch<INinio>(`${this.resourceUrl}/${getNinioIdentifier(ninio) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INinio>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INinio[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNinioToCollectionIfMissing(ninioCollection: INinio[], ...niniosToCheck: (INinio | null | undefined)[]): INinio[] {
    const ninios: INinio[] = niniosToCheck.filter(isPresent);
    if (ninios.length > 0) {
      const ninioCollectionIdentifiers = ninioCollection.map(ninioItem => getNinioIdentifier(ninioItem)!);
      const niniosToAdd = ninios.filter(ninioItem => {
        const ninioIdentifier = getNinioIdentifier(ninioItem);
        if (ninioIdentifier == null || ninioCollectionIdentifiers.includes(ninioIdentifier)) {
          return false;
        }
        ninioCollectionIdentifiers.push(ninioIdentifier);
        return true;
      });
      return [...niniosToAdd, ...ninioCollection];
    }
    return ninioCollection;
  }

  protected convertDateFromClient(ninio: INinio): INinio {
    return Object.assign({}, ninio, {
      fechaNacimiento: ninio.fechaNacimiento?.isValid() ? ninio.fechaNacimiento.format(DATE_FORMAT) : undefined,
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
      res.body.forEach((ninio: INinio) => {
        ninio.fechaNacimiento = ninio.fechaNacimiento ? dayjs(ninio.fechaNacimiento) : undefined;
      });
    }
    return res;
  }
}
