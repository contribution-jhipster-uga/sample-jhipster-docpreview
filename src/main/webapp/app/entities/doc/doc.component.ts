import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IDoc } from 'app/shared/model/doc.model';
import { DocService } from './doc.service';
import { DocDeleteDialogComponent } from './doc-delete-dialog.component';

@Component({
  selector: 'jhi-doc',
  templateUrl: './doc.component.html'
})
export class DocComponent implements OnInit, OnDestroy {
  docs?: IDoc[];
  eventSubscriber?: Subscription;

  constructor(
    protected docService: DocService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.docService.query().subscribe((res: HttpResponse<IDoc[]>) => (this.docs = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInDocs();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IDoc): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInDocs(): void {
    this.eventSubscriber = this.eventManager.subscribe('docListModification', () => this.loadAll());
  }

  delete(doc: IDoc): void {
    const modalRef = this.modalService.open(DocDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.doc = doc;
  }
}
