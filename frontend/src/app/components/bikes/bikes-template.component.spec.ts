import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BikesTemplateComponent } from './bikes-template.component';

describe('BikesTemplateComponent', () => {
  let component: BikesTemplateComponent;
  let fixture: ComponentFixture<BikesTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BikesTemplateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BikesTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
