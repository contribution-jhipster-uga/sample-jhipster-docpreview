import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDoc } from 'app/shared/model/doc.model';
import { DocService } from './doc.service';

@Component({
  templateUrl: './doc-delete-dialog.component.html'
})
export class DocDeleteDialogComponent {
  doc?: IDoc;

  constructor(protected docService: DocService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.docService.delete(id).subscribe(() => {
      this.eventManager.broadcast('docListModification');
      this.activeModal.close();
    });
  }
}
