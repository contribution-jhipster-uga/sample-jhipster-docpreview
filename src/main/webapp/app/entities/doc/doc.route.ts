import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { Authority } from 'app/shared/constants/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IDoc, Doc } from 'app/shared/model/doc.model';
import { DocService } from './doc.service';
import { DocComponent } from './doc.component';
import { DocDetailComponent } from './doc-detail.component';
import { DocUpdateComponent } from './doc-update.component';

@Injectable({ providedIn: 'root' })
export class DocResolve implements Resolve<IDoc> {
  constructor(private service: DocService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDoc> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((doc: HttpResponse<Doc>) => {
          if (doc.body) {
            return of(doc.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Doc());
  }
}

export const docRoute: Routes = [
  {
    path: '',
    component: DocComponent,
    data: {
      authorities: [Authority.USER],
      pageTitle: 'docpreviewApp.doc.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: DocDetailComponent,
    resolve: {
      doc: DocResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'docpreviewApp.doc.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: DocUpdateComponent,
    resolve: {
      doc: DocResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'docpreviewApp.doc.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: DocUpdateComponent,
    resolve: {
      doc: DocResolve
    },
    data: {
      authorities: [Authority.USER],
      pageTitle: 'docpreviewApp.doc.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];
