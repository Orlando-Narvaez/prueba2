import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { INinio, Ninio } from '../ninio.model';
import { NinioService } from '../service/ninio.service';
import { IAdulto } from 'app/entities/adulto/adulto.model';
import { AdultoService } from 'app/entities/adulto/service/adulto.service';

@Component({
  selector: 'jhi-ninio-update',
  templateUrl: './ninio-update.component.html',
})
export class NinioUpdateComponent implements OnInit {
  isSaving = false;

  adultosSharedCollection: IAdulto[] = [];

  editForm = this.fb.group({
    id: [],
    nombre: [null, [Validators.required]],
    apellido: [null, [Validators.required]],
    edad: [],
    fechaNacimiento: [],
    adulto: [],
  });

  constructor(
    protected ninioService: NinioService,
    protected adultoService: AdultoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ninio }) => {
      this.updateForm(ninio);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ninio = this.createFromForm();
    if (ninio.id !== undefined) {
      this.subscribeToSaveResponse(this.ninioService.update(ninio));
    } else {
      this.subscribeToSaveResponse(this.ninioService.create(ninio));
    }
  }

  trackAdultoById(_index: number, item: IAdulto): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INinio>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ninio: INinio): void {
    this.editForm.patchValue({
      id: ninio.id,
      nombre: ninio.nombre,
      apellido: ninio.apellido,
      edad: ninio.edad,
      fechaNacimiento: ninio.fechaNacimiento,
      adulto: ninio.adulto,
    });

    this.adultosSharedCollection = this.adultoService.addAdultoToCollectionIfMissing(this.adultosSharedCollection, ninio.adulto);
  }

  protected loadRelationshipsOptions(): void {
    this.adultoService
      .query()
      .pipe(map((res: HttpResponse<IAdulto[]>) => res.body ?? []))
      .pipe(map((adultos: IAdulto[]) => this.adultoService.addAdultoToCollectionIfMissing(adultos, this.editForm.get('adulto')!.value)))
      .subscribe((adultos: IAdulto[]) => (this.adultosSharedCollection = adultos));
  }

  protected createFromForm(): INinio {
    return {
      ...new Ninio(),
      id: this.editForm.get(['id'])!.value,
      nombre: this.editForm.get(['nombre'])!.value,
      apellido: this.editForm.get(['apellido'])!.value,
      edad: this.editForm.get(['edad'])!.value,
      fechaNacimiento: this.editForm.get(['fechaNacimiento'])!.value,
      adulto: this.editForm.get(['adulto'])!.value,
    };
  }
}
