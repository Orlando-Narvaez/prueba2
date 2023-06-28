import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { AdultoService } from '../service/adulto.service';

import { AdultoComponent } from './adulto.component';

describe('Adulto Management Component', () => {
  let comp: AdultoComponent;
  let fixture: ComponentFixture<AdultoComponent>;
  let service: AdultoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [AdultoComponent],
    })
      .overrideTemplate(AdultoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdultoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AdultoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.adultos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
