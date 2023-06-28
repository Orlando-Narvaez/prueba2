import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AdultoComponent } from './list/adulto.component';
import { AdultoDetailComponent } from './detail/adulto-detail.component';
import { AdultoUpdateComponent } from './update/adulto-update.component';
import { AdultoDeleteDialogComponent } from './delete/adulto-delete-dialog.component';
import { AdultoRoutingModule } from './route/adulto-routing.module';

@NgModule({
  imports: [SharedModule, AdultoRoutingModule],
  declarations: [AdultoComponent, AdultoDetailComponent, AdultoUpdateComponent, AdultoDeleteDialogComponent],
  entryComponents: [AdultoDeleteDialogComponent],
})
export class AdultoModule {}
