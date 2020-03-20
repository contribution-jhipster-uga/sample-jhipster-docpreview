import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiDataUtils, JhiFileLoadError, JhiEventManager, JhiEventWithContent } from 'ng-jhipster';

import { IDoc, Doc } from 'app/shared/model/doc.model';
import { DocService } from './doc.service';
import { AlertError } from 'app/shared/alert/alert-error.model';

@Component({
  selector: 'jhi-doc-update',
  templateUrl: './doc-update.component.html'
})
export class DocUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required, Validators.minLength(2)]],
    language: [],
    description: [null, [Validators.maxLength(2000)]],
    content: [null, [Validators.required]],
    contentContentType: [],
    contentSha1: [null, [Validators.minLength(40), Validators.maxLength(40), Validators.pattern('[a-f0-9]{40}')]],
    numberOfPages: [],
    createdAt: [null, [Validators.required]],
    updatedAt: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected docService: DocService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doc }) => {
      if (!doc.id) {
        const today = moment().startOf('day');
        doc.createdAt = today;
        doc.updatedAt = today;
      }

      this.updateForm(doc);
    });
  }

  updateForm(doc: IDoc): void {
    this.editForm.patchValue({
      id: doc.id,
      title: doc.title,
      language: doc.language,
      description: doc.description,
      content: doc.content,
      contentContentType: doc.contentContentType,
      contentSha1: doc.contentSha1,
      numberOfPages: doc.numberOfPages,
      createdAt: doc.createdAt ? doc.createdAt.format(DATE_TIME_FORMAT) : null,
      updatedAt: doc.updatedAt ? doc.updatedAt.format(DATE_TIME_FORMAT) : null
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe(null, (err: JhiFileLoadError) => {
      this.eventManager.broadcast(
        new JhiEventWithContent<AlertError>('docpreviewApp.error', { ...err, key: 'error.file.' + err.key })
      );
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const doc = this.createFromForm();
    if (doc.id !== undefined) {
      this.subscribeToSaveResponse(this.docService.update(doc));
    } else {
      this.subscribeToSaveResponse(this.docService.create(doc));
    }
  }

  private createFromForm(): IDoc {
    return {
      ...new Doc(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      language: this.editForm.get(['language'])!.value,
      description: this.editForm.get(['description'])!.value,
      contentContentType: this.editForm.get(['contentContentType'])!.value,
      content: this.editForm.get(['content'])!.value,
      contentSha1: this.editForm.get(['contentSha1'])!.value,
      numberOfPages: this.editForm.get(['numberOfPages'])!.value,
      createdAt: this.editForm.get(['createdAt'])!.value ? moment(this.editForm.get(['createdAt'])!.value, DATE_TIME_FORMAT) : undefined,
      updatedAt: this.editForm.get(['updatedAt'])!.value ? moment(this.editForm.get(['updatedAt'])!.value, DATE_TIME_FORMAT) : undefined
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoc>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
