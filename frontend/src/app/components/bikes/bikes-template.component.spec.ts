import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http'; // Add this line

import { BikesTemplateComponent } from './bikes-template.component';
import { CheckoutComponent } from '../checkout/checkout.component';

import { AppModule } from 'src/app/app.module';

describe('BikesTemplateComponent', () => {
  let component: BikesTemplateComponent;
  let fixture: ComponentFixture<BikesTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BikesTemplateComponent],
      imports: [HttpClientModule, AppModule], // Add HttpClientModule to the imports array
      providers: [
        { provide: CheckoutComponent, useValue: {} }, // Mock CheckoutComponent
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BikesTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
