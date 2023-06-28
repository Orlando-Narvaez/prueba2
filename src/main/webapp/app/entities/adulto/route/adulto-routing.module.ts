import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AdultoComponent } from '../list/adulto.component';
import { AdultoDetailComponent } from '../detail/adulto-detail.component';
import { AdultoUpdateComponent } from '../update/adulto-update.component';
import { AdultoRoutingResolveService } from './adulto-routing-resolve.service';

const adultoRoute: Routes = [
  {
    path: '',
    component: AdultoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AdultoDetailComponent,
    resolve: {
      adulto: AdultoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AdultoUpdateComponent,
    resolve: {
      adulto: AdultoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AdultoUpdateComponent,
    resolve: {
      adulto: AdultoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(adultoRoute)],
  exports: [RouterModule],
})
export class AdultoRoutingModule {}
