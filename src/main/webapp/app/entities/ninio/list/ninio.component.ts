import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INinio } from '../ninio.model';
import { NinioService } from '../service/ninio.service';
import { NinioDeleteDialogComponent } from '../delete/ninio-delete-dialog.component';

@Component({
  selector: 'jhi-ninio',
  templateUrl: './ninio.component.html',
})
export class NinioComponent implements OnInit {
  ninios?: INinio[];
  isLoading = false;

  constructor(protected ninioService: NinioService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.ninioService.query().subscribe({
      next: (res: HttpResponse<INinio[]>) => {
        this.isLoading = false;
        this.ninios = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: INinio): number {
    return item.id!;
  }

  delete(ninio: INinio): void {
    const modalRef = this.modalService.open(NinioDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ninio = ninio;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
