import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { IDoc } from 'app/shared/model/doc.model';

@Component({
  selector: 'jhi-doc-detail',
  templateUrl: './doc-detail.component.html'
})
export class DocDetailComponent implements OnInit {
  doc: IDoc | null = null;

  pdfHeight = 1200;
  pdfWidth = 900;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute, private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doc }) => (this.doc = doc));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }

  getSafeURL(): SafeResourceUrl {
    return this.sanitizer.bypassSecurityTrustResourceUrl('/api/docs/' + this.doc?.id + '/content');
  }

  isPdf(): boolean {
    return this.doc?.contentContentType === 'application/pdf';
  }
}
