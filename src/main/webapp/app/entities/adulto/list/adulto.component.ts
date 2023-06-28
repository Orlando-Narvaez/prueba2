import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdulto } from '../adulto.model';
import { AdultoService } from '../service/adulto.service';
import { AdultoDeleteDialogComponent } from '../delete/adulto-delete-dialog.component';

@Component({
  selector: 'jhi-adulto',
  templateUrl: './adulto.component.html',
})
export class AdultoComponent implements OnInit {
  adultos?: IAdulto[];
  isLoading = false;

  constructor(protected adultoService: AdultoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.adultoService.query().subscribe({
      next: (res: HttpResponse<IAdulto[]>) => {
        this.isLoading = false;
        this.adultos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IAdulto): number {
    return item.id!;
  }

  delete(adulto: IAdulto): void {
    const modalRef = this.modalService.open(AdultoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.adulto = adulto;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
