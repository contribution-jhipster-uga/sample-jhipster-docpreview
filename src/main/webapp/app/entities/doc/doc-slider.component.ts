/* eslint-disable no-console */
import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IDoc } from 'app/shared/model/doc.model';

import {
  SwiperComponent,
  SwiperDirective,
  SwiperConfigInterface,
  SwiperScrollbarInterface,
  SwiperPaginationInterface
} from 'ngx-swiper-wrapper';

@Component({
  selector: 'jhi-doc-slider',
  templateUrl: './doc-slider.component.html',
  styleUrls: ['./doc-slider.component.scss']
})
export class DocSliderComponent implements OnInit {
  doc: IDoc | null = null;

  public show = true;

  public slides: any[] = [];

  public type = 'component';

  public disabled = false;

  public config: SwiperConfigInterface = {
    a11y: true,
    direction: 'vertical', // 'horizontal'
    slidesPerView: 1, // can be a float
    keyboard: true,
    mousewheel: true,
    scrollbar: false,
    navigation: true,
    pagination: false
  };

  private scrollbar: SwiperScrollbarInterface = {
    el: '.swiper-scrollbar',
    hide: false,
    draggable: true
  };

  private pagination: SwiperPaginationInterface = {
    el: '.swiper-pagination',
    clickable: true,
    hideOnClick: false
  };

  @ViewChild(SwiperComponent, { static: false }) componentRef?: SwiperComponent;
  @ViewChild(SwiperDirective, { static: false }) directiveRef?: SwiperDirective;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ doc }) => {
      this.doc = doc;
      this.constructSlides();
    });
  }

  previousState(): void {
    window.history.back();
  }

  constructSlides(): void {
    if (this.doc?.numberOfPages === undefined) {
      return;
    }

    for (let i = 1; i <= this.doc.numberOfPages; i++) {
      console.debug(' Slide', i);
      const slide = {
        img: '/api/docs/' + this.doc.id + '/img/' + i,
        id: this.doc.id,
        page: i
      };
      this.slides.push(slide);
    }
  }

  getThumbnail(url: string): string {
    // return url.replace('.jpg', '.x1.jpg');
    return url;
  }

  public toggleType(): void {
    this.type = this.type === 'component' ? 'directive' : 'component';
  }

  public toggleDisabled(): void {
    this.disabled = !this.disabled;
  }

  public toggleDirection(): void {
    this.config.direction = this.config.direction === 'horizontal' ? 'vertical' : 'horizontal';
  }

  public toggleSlidesPerView(): void {
    if (this.config.slidesPerView !== 1) {
      this.config.slidesPerView = 1;
    } else {
      this.config.slidesPerView = 2;
    }
  }

  public toggleOverlayControls(): void {
    if (this.config.navigation) {
      this.config.scrollbar = false;
      this.config.navigation = false;

      this.config.pagination = this.pagination;
    } else if (this.config.pagination) {
      this.config.navigation = false;
      this.config.pagination = false;

      this.config.scrollbar = this.scrollbar;
    } else {
      this.config.scrollbar = false;
      this.config.pagination = false;

      this.config.navigation = true;
    }

    if (this.type === 'directive' && this.directiveRef) {
      this.directiveRef.setIndex(0);
    } else if (this.type === 'component' && this.componentRef && this.componentRef.directiveRef) {
      this.componentRef.directiveRef.setIndex(0);
    }
  }

  public toggleKeyboardControl(): void {
    this.config.keyboard = !this.config.keyboard;
  }

  public toggleMouseWheelControl(): void {
    this.config.mousewheel = !this.config.mousewheel;
  }

  public onIndexChange(index: number): void {
    console.log('Swiper index: ', index);
  }

  public onSwiperEvent(event: string): void {
    console.log('Swiper event: ', event);
  }
}
