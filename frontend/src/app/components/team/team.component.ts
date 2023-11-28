import { Component, OnInit } from '@angular/core';
import { OwlOptions } from 'ngx-owl-carousel-o';

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html',
  styleUrls: ['./team.component.css'],
})
export class TeamComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {}

  dynamicSlides = [
    {
      id: '1',
      src: '../../../assets/img/toteu.jpg',
      alt: 'Side 1',
      name: 'Dragos Polifronie',
      role: 'UI/UX - Frontend Developer',
      git: 'https://github.com/Dragosp33',
    },
    {
      id: '2',
      src: '../../../assets/img/toteu.jpg',
      alt: 'Side 2',
      name: 'Dragos Polifronie',
      role: 'Backend developer',
      git: 'https://github.com/Dragosp33',
    },
  ];

  customOptions: OwlOptions = {
    autoplay: false,
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
