import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NinioComponent } from '../list/ninio.component';
import { NinioDetailComponent } from '../detail/ninio-detail.component';
import { NinioUpdateComponent } from '../update/ninio-update.component';
import { NinioRoutingResolveService } from './ninio-routing-resolve.service';

const ninioRoute: Routes = [
  {
    path: '',
    component: NinioComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NinioDetailComponent,
    resolve: {
      ninio: NinioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NinioUpdateComponent,
    resolve: {
      ninio: NinioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NinioUpdateComponent,
    resolve: {
      ninio: NinioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(ninioRoute)],
  exports: [RouterModule],
})
export class NinioRoutingModule {}
