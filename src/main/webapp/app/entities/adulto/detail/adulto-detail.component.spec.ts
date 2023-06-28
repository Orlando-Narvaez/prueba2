import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AdultoDetailComponent } from './adulto-detail.component';

describe('Adulto Management Detail Component', () => {
  let comp: AdultoDetailComponent;
  let fixture: ComponentFixture<AdultoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdultoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ adulto: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AdultoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AdultoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load adulto on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.adulto).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
