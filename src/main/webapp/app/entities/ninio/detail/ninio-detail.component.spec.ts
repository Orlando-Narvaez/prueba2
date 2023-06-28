import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NinioDetailComponent } from './ninio-detail.component';

describe('Ninio Management Detail Component', () => {
  let comp: NinioDetailComponent;
  let fixture: ComponentFixture<NinioDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NinioDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ ninio: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NinioDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NinioDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load ninio on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.ninio).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
