import { bikes_typeService } from '../../services/bikes_type.service';
import {
  AfterViewChecked,
  Component,
  HostListener,
  OnInit,
  ViewChild,
} from '@angular/core';
import { OwlOptions } from 'ngx-owl-carousel-o';
import { Bikes_type } from 'src/app/interfaces/bikes_type';

import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css'],
})
export class ProductsComponent implements OnInit {
  public bikes_type: Bikes_type[];
  customOptions?: OwlOptions;
  private isDataLoaded = false;

  constructor(private bikes_typeService: bikes_typeService) {
    this.bikes_type = [];
  }

  ngOnInit() {
    this.getBikes();
    console.log(this.bikes_type);
  }

  public getBikes(): void {
    this.bikes_typeService.getBikesType().subscribe(
      (Response: Bikes_type[]) => {
        this.bikes_type = Response;

        this.isDataLoaded = true;
        this.initializeCarousel();
        //  return Response;
      },
      (error: HttpErrorResponse) => {
        console.log(error);
        alert(error.message);
        // return [;]
      }
    );
  }
  private initializeCarousel(): void {
    // Initialize the carousel only if data is loaded
    if (this.isDataLoaded) {
      this.customOptions = {
        autoplay: true,
        smartSpeed: 1000,
        margin: 45,
        navSpeed: 600,
        dots: false,
        loop: true,
        // responsiveBaseElement: 'true',

        navText: [
          '<i class="bi bi-arrow-left"></i>',
          '<i class="bi bi-arrow-right"></i>',
        ],
        responsive: {
          0: {
            items: 1,
          },
          768: {
            items: 2,
          },
          992: {
            items: 2,
          },
          1200: {
            items: 2,
          },
        },

        nav: true,
      };
    }
  }

  /*
  dynamicSlides = [
    {
      id: '1',
      src: '../../../assets/img/product-1.png',
      alt: 'Side 1',
      title: 'Side 1',
    },
    {
      id: '2',
      src: 'url(https://via.placeholder.com/600/771796)',
      alt: 'Side 2',
      title: 'Side 2',
    },
    {
      id: '3',
      src: 'https://via.placeholder.com/600/24f355',
      alt: 'Side 3',
      title: 'Side 3',
    },
    {
      id: '4',
      src: 'https://via.placeholder.com/600/d32776',
      alt: 'Side 4',
      title: 'Side 4',
    },
    {
      id: '5',
      src: 'https://via.placeholder.com/600/f66b97',
      alt: 'Side 5',
      title: 'Side 5',
    },
  ];

    /*customOptions: OwlOptions = {
    autoplay: true,
    smartSpeed: 1000,
    margin: 45,
    navSpeed: 600,
    dots: false,
    loop: true,
    // responsiveBaseElement: 'true',

    navText: [
      '<i class="bi bi-arrow-left"></i>',
      '<i class="bi bi-arrow-right"></i>',
    ],
    responsive: {
      0: {
        items: 1,
      },
      768: {
        items: 2,
      },
      992: {
        items: 3,
      },
      1200: {
        items: 3,
      },
    },

    nav: true,
  };

  updateCarousel(): void {
    // Use a timeout to allow time for the DOM to update after a resize
    setTimeout(() => {
      this.customOptions = { ...this.customOptions, autoWidth: true };
      // console.log(this.customOptions);
    }, 20);
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any): void {
    this.updateCarousel();
  }*/
}
