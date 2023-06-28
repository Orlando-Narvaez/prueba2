import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAdulto, Adulto } from '../adulto.model';
import { AdultoService } from '../service/adulto.service';

@Injectable({ providedIn: 'root' })
export class AdultoRoutingResolveService implements Resolve<IAdulto> {
  constructor(protected service: AdultoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAdulto> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((adulto: HttpResponse<Adulto>) => {
          if (adulto.body) {
            return of(adulto.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Adulto());
  }
}
