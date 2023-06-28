import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AdultoService } from '../service/adulto.service';
import { IAdulto, Adulto } from '../adulto.model';

import { AdultoUpdateComponent } from './adulto-update.component';

describe('Adulto Management Update Component', () => {
  let comp: AdultoUpdateComponent;
  let fixture: ComponentFixture<AdultoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let adultoService: AdultoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AdultoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AdultoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdultoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    adultoService = TestBed.inject(AdultoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const adulto: IAdulto = { id: 456 };

      activatedRoute.data = of({ adulto });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(adulto));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Adulto>>();
      const adulto = { id: 123 };
      jest.spyOn(adultoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adulto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: adulto }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(adultoService.update).toHaveBeenCalledWith(adulto);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Adulto>>();
      const adulto = new Adulto();
      jest.spyOn(adultoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adulto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: adulto }));
      saveSubject.complete();

      // THEN
      expect(adultoService.create).toHaveBeenCalledWith(adulto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Adulto>>();
      const adulto = { id: 123 };
      jest.spyOn(adultoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adulto });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(adultoService.update).toHaveBeenCalledWith(adulto);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
