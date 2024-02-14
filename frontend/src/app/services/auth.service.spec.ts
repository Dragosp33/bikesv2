import { firebaseConfig } from './../../environments/environment.prod';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Import the HttpClientTestingModule
import { AuthService } from './auth.service';
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    const app = initializeApp(firebaseConfig);
    const auth = getAuth(app);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // Add HttpClientTestingModule to the imports array
      providers: [
        AuthService, // Inject the initialized auth into the service
      ],
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
