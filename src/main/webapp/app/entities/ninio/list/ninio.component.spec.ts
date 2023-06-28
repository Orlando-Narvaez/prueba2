import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { NinioService } from '../service/ninio.service';

import { NinioComponent } from './ninio.component';

describe('Ninio Management Component', () => {
  let comp: NinioComponent;
  let fixture: ComponentFixture<NinioComponent>;
  let service: NinioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [NinioComponent],
    })
      .overrideTemplate(NinioComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NinioComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NinioService);

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
    expect(comp.ninios?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
