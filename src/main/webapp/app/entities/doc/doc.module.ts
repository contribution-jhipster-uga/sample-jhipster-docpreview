import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DocpreviewSharedModule } from 'app/shared/shared.module';
import { DocComponent } from './doc.component';
import { DocDetailComponent } from './doc-detail.component';
import { DocUpdateComponent } from './doc-update.component';
import { DocDeleteDialogComponent } from './doc-delete-dialog.component';
import { DocSliderComponent } from './doc-slider.component';
import { docRoute } from './doc.route';
import { AuthImagePipe } from './authimg.pipe';
import { SwiperModule, SwiperConfigInterface, SWIPER_CONFIG } from 'ngx-swiper-wrapper';

const DEFAULT_SWIPER_CONFIG: SwiperConfigInterface = {
  observer: true,
  direction: 'horizontal',
  threshold: 50,
  spaceBetween: 5,
  slidesPerView: 1,
  centeredSlides: true
};

@NgModule({
  imports: [DocpreviewSharedModule, RouterModule.forChild(docRoute), SwiperModule],
  declarations: [DocComponent, DocDetailComponent, DocUpdateComponent, DocDeleteDialogComponent, DocSliderComponent, AuthImagePipe],
  entryComponents: [DocDeleteDialogComponent],
  providers: [
    {
      provide: SWIPER_CONFIG,
      useValue: DEFAULT_SWIPER_CONFIG
    }
  ]
})
export class DocpreviewDocModule {}
