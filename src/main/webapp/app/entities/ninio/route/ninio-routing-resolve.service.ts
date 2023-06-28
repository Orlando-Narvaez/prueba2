import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INinio, Ninio } from '../ninio.model';
import { NinioService } from '../service/ninio.service';

@Injectable({ providedIn: 'root' })
export class NinioRoutingResolveService implements Resolve<INinio> {
  constructor(protected service: NinioService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INinio> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ninio: HttpResponse<Ninio>) => {
          if (ninio.body) {
            return of(ninio.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ninio());
  }
}
