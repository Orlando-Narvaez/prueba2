import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAdulto } from '../adulto.model';

@Component({
  selector: 'jhi-adulto-detail',
  templateUrl: './adulto-detail.component.html',
})
export class AdultoDetailComponent implements OnInit {
  adulto: IAdulto | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ adulto }) => {
      this.adulto = adulto;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
