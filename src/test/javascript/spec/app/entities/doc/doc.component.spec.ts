import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { DocpreviewTestModule } from '../../../test.module';
import { DocComponent } from 'app/entities/doc/doc.component';
import { DocService } from 'app/entities/doc/doc.service';
import { Doc } from 'app/shared/model/doc.model';

describe('Component Tests', () => {
  describe('Doc Management Component', () => {
    let comp: DocComponent;
    let fixture: ComponentFixture<DocComponent>;
    let service: DocService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DocpreviewTestModule],
        declarations: [DocComponent]
      })
        .overrideTemplate(DocComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DocComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DocService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Doc(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.docs && comp.docs[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
