import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { Observable, of } from 'rxjs';

// Mock AuthService
class MockAuthService {
  onAuthStateChanged(callback: (user: any) => void) {
    // Implement the mock behavior here if needed
  }
}

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let router: Router;
  let authService: MockAuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        AuthGuard,
        { provide: MockAuthService, useClass: MockAuthService },
      ],
    });
    guard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
    authService = TestBed.inject(MockAuthService);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access to /profile if the user is authenticated', async(() => {
    spyOn(authService, 'onAuthStateChanged').and.callFake(
      (callback: (user: any) => void) => {
        callback({
          /* mock authenticated user */
        });
      }
    );

    guard.canActivate().subscribe((result) => {
      expect(result).toBe(true);
    });
  }));

  it('should redirect to /login if the user is not authenticated', async(() => {
    spyOn(authService, 'onAuthStateChanged').and.callFake(
      (callback: (user: any) => void) => {
        callback(null); // No user, not authenticated
      }
    );

    const navigateSpy = spyOn(router, 'navigate');

    guard.canActivate().subscribe((result) => {
      expect(result).toBe(false);
      expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    });
  }));
});
