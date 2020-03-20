import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { DocpreviewTestModule } from '../../../test.module';
import { DocUpdateComponent } from 'app/entities/doc/doc-update.component';
import { DocService } from 'app/entities/doc/doc.service';
import { Doc } from 'app/shared/model/doc.model';

describe('Component Tests', () => {
  describe('Doc Management Update Component', () => {
    let comp: DocUpdateComponent;
    let fixture: ComponentFixture<DocUpdateComponent>;
    let service: DocService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [DocpreviewTestModule],
        declarations: [DocUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(DocUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DocUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DocService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Doc(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new Doc();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
