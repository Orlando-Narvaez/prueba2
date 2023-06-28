import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INinio } from '../ninio.model';
import { NinioService } from '../service/ninio.service';

@Component({
  templateUrl: './ninio-delete-dialog.component.html',
})
export class NinioDeleteDialogComponent {
  ninio?: INinio;

  constructor(protected ninioService: NinioService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ninioService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
