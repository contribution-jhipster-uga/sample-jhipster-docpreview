import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'doc',
        loadChildren: () => import('./doc/doc.module').then(m => m.DocpreviewDocModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class DocpreviewEntityModule {}
