import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAdulto, Adulto } from '../adulto.model';
import { AdultoService } from '../service/adulto.service';

@Component({
  selector: 'jhi-adulto-update',
  templateUrl: './adulto-update.component.html',
})
export class AdultoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nombre: [null, [Validators.required]],
    apellido: [null, [Validators.required]],
    edad: [],
    fechaNacimiento: [],
  });

  constructor(protected adultoService: AdultoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ adulto }) => {
      this.updateForm(adulto);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const adulto = this.createFromForm();
    if (adulto.id !== undefined) {
      this.subscribeToSaveResponse(this.adultoService.update(adulto));
    } else {
      this.subscribeToSaveResponse(this.adultoService.create(adulto));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAdulto>>): void {
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

  protected updateForm(adulto: IAdulto): void {
    this.editForm.patchValue({
      id: adulto.id,
      nombre: adulto.nombre,
      apellido: adulto.apellido,
      edad: adulto.edad,
      fechaNacimiento: adulto.fechaNacimiento,
    });
  }

  protected createFromForm(): IAdulto {
    return {
      ...new Adulto(),
      id: this.editForm.get(['id'])!.value,
      nombre: this.editForm.get(['nombre'])!.value,
      apellido: this.editForm.get(['apellido'])!.value,
      edad: this.editForm.get(['edad'])!.value,
      fechaNacimiento: this.editForm.get(['fechaNacimiento'])!.value,
    };
  }
}
