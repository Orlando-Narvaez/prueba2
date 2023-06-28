import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INinio } from '../ninio.model';

@Component({
  selector: 'jhi-ninio-detail',
  templateUrl: './ninio-detail.component.html',
})
export class NinioDetailComponent implements OnInit {
  ninio: INinio | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ninio }) => {
      this.ninio = ninio;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
