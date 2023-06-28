import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdulto } from '../adulto.model';
import { AdultoService } from '../service/adulto.service';

@Component({
  templateUrl: './adulto-delete-dialog.component.html',
})
export class AdultoDeleteDialogComponent {
  adulto?: IAdulto;

  constructor(protected adultoService: AdultoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.adultoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
